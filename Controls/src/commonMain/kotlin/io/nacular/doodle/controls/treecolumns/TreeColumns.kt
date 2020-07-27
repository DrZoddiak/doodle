package io.nacular.doodle.controls.treecolumns

import io.nacular.doodle.controls.IndexedItemVisualizer
import io.nacular.doodle.controls.MultiSelectionModel
import io.nacular.doodle.controls.Selectable
import io.nacular.doodle.controls.SelectionModel
import io.nacular.doodle.controls.SimpleMutableListModel
import io.nacular.doodle.controls.list.List
import io.nacular.doodle.controls.list.ListBehavior
import io.nacular.doodle.controls.list.MutableList
import io.nacular.doodle.controls.panels.ScrollPanel
import io.nacular.doodle.controls.tree.TreeModel
import io.nacular.doodle.core.Layout
import io.nacular.doodle.core.PositionableContainer
import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.geometry.Rectangle
import io.nacular.doodle.utils.ObservableSet
import io.nacular.doodle.utils.Path
import io.nacular.doodle.utils.Pool
import io.nacular.doodle.utils.PropertyObservers
import io.nacular.doodle.utils.SetObserver
import io.nacular.doodle.utils.SetPool
import kotlin.math.max

/**
 * Created by Nicholas Eddy on 6/1/19.
 */
class FilteringSelectionModel(delegate: SelectionModel<Path<Int>>): SelectionModel<Path<Int>> by delegate {
    var root     = null as Path<Int>?
    val children = mutableListOf<Int>()

    init {
        delegate.changed += { _, removed, added ->
            root?.let { root ->
                removed.forEach {
                    // Toggle leaf
                    when {
                        root == it.parent -> {
                            children -= it.bottom!!
                        }
                        it == root || it ancestorOf root -> {
                            children.clear()
                            this.root = null
                        }
                    }
                }
            }

            val r = root

            added.forEach {
                when {
                    r != null && it != r && it.parent == r.parent -> {
                        children += listOf(it.bottom!!, r.bottom!!)
                        root = it.parent
                    }
                    else -> {
                        children.clear()

                        root = it
                    }
                }
            }
        }
    }
}

fun main() {
    val selectionModel = FilteringSelectionModel(MultiSelectionModel())

    selectionModel.add(Path(0) + 0 + 1)
    selectionModel.add(Path(0) + 0 + 2)
    selectionModel.add(Path(0) + 1    )
}

private interface Focusable {
    val hasFocus    : Boolean
    val focusChanged: PropertyObservers<View, Boolean>
}

// TODO: Use filtered SelectionModel to avoid selecting hidden items?
open class TreeColumns<T, M: TreeModel<T>>(
        protected val model         : M,
                  val itemVisualizer: IndexedItemVisualizer<T>?  = null,
                      selectionModel: SelectionModel<Path<Int>>? = null): View(), Selectable<Path<Int>>, Focusable {

    private val columns = mutableListOf<Column<T>>()

    protected val selectionModel = selectionModel?.let { FilteringSelectionModel(it) }

    private class LocalSelectionModel(var root: Path<Int>, private val delegate: SelectionModel<Path<Int>>): SelectionModel<Int> { //MultiSelectionModel<Int>() {

        private val changeObserver: (ObservableSet<Path<Int>>, Set<Path<Int>>, Set<Path<Int>>) -> Unit = { set, removed, added ->
            (changed as SetPool).forEach {
                it(
                    ObservableSet(set.filter { root ancestorOf it }.mapNotNull { it.nonOverlappingStem(root).firstOrNull() }.toMutableSet()),
                    removed.filter { root ancestorOf it }.mapNotNull { it.nonOverlappingStem(root).firstOrNull() }.toSet(),
                    added.filter   { root ancestorOf it }.mapNotNull { it.nonOverlappingStem(root).firstOrNull() }.toSet()
                )
            }
        }

        private var delegateHandle = null as SelectionModel<Path<Int>>?

        fun enable() {
            if (delegateHandle == null) {
                delegateHandle = delegate
                delegate.changed += changeObserver
            }
        }

        fun disable() {
            delegateHandle?.let {
                it.changed -= changeObserver
                delegateHandle = null
            }
        }

        override fun add   (item : Int            ) = delegateHandle?.add      (root + item) ?: false
        override fun addAll(items: Collection<Int>) = delegateHandle?.addAll   (items.map { root + it }) ?: false
        override fun remove(item : Int            ) = delegateHandle?.remove   (root + item) ?: false
        override fun clear (                      ) { delegateHandle?.removeAll(delegate.iterator().asSequence().filter { root ancestorOf it }.toList()) }

        override fun removeAll  (items: Collection<Int>) = delegateHandle?.removeAll  (items.map { root + it }) ?: false
        override fun retainAll  (items: Collection<Int>) = delegateHandle?.retainAll  (items.map { root + it }) ?: false
        override fun containsAll(items: Collection<Int>) = delegateHandle?.containsAll(items.map { root + it }) ?: false
        override fun replaceAll (items: Collection<Int>) = delegateHandle?.replaceAll (items.map { root + it }) ?: false
        override fun toggle     (items: Collection<Int>) = delegateHandle?.toggle     (items.map { root + it }) ?: false

        private fun overlappingIndex(path: Path<Int>?): Int? {
            return when {
                path != null && root ancestorOf path -> path.nonOverlappingStem(root).firstOrNull()
                else                                 -> null
            }
        }

        override val first  get() = overlappingIndex(delegateHandle?.first)
        override val last   get() = overlappingIndex(delegateHandle?.last )
        override val anchor get() = overlappingIndex(delegateHandle?.anchor)
        override val size   get() = delegateHandle?.iterator()?.asSequence()?.fold(0) { size, path ->
            size + (overlappingIndex(path)?.let { 1 } ?: 0)
        } ?: 0

        override val isEmpty get() = size == 0

        override fun contains(item: Int) = delegateHandle?.iterator()?.asSequence()?.find { overlappingIndex(it) == item } != null


        // FIXME: This is pretty inefficient
        override val changed: Pool<SetObserver<Int>> = SetPool()

        override fun iterator() = delegateHandle?.iterator()?.asSequence()?.mapNotNull { overlappingIndex(it) }?.iterator() ?: emptyList<Int>().iterator()
    }

    private class CustomMutableList<T>(
            model         : SimpleMutableListModel<T>,
            itemGenerator : IndexedItemVisualizer<T>? = null,
            val localSelectionModel: LocalSelectionModel? = null,
            fitContent    : Boolean                   = true,
            scrollCache   : Int): MutableList<T, SimpleMutableListModel<T>>(model, itemGenerator, localSelectionModel, fitContent, scrollCache) {
        public override val model = super.model

        fun enable() {
            localSelectionModel?.enable()
        }

        fun disable() {
            localSelectionModel?.disable()
        }

        override fun addedToDisplay() {
            enable()
        }

        override fun removedFromDisplay() {
            super.removedFromDisplay()
            disable()
        }
    }

    var behavior = null as TreeColumnsBehavior<T>?
        set(new) {
            if (new == behavior) {
                return
            }

            field?.uninstall(this)

            field = new

            new?.also { behavior ->
                behavior.install(this)

                columns.forEach {
                    installBehavior(it, behavior)
                }
            }
        }

    private data class Column<T>(var path: Path<Int>, val list: CustomMutableList<T>)

    private fun installBehavior(column: Column<T>, behavior: TreeColumnsBehavior<T>) {
        column.list.behavior = object: ListBehavior<T> {
            override val generator = object: ListBehavior.RowGenerator<T> {
                override fun invoke(list: List<T, *>, row: T, index: Int, current: View?) = behavior.generator(
                        this@TreeColumns,
                        row,
                        column.path + index,
                        index
                )
            }

            override val positioner = object: ListBehavior.RowPositioner<T> {
                override fun invoke(list: List<T, *>, row: T, index: Int) = behavior.positioner(
                        this@TreeColumns,
                        list.width,
                        column.path + index,
                        row,
                        index
                )

                override fun rowFor(list: List<T, *>, y: Double) = behavior.positioner.row(
                        this@TreeColumns, y
                )
            }

            override fun render(view: List<T, *>, canvas: Canvas) {
                behavior.renderColumnBody(this@TreeColumns, column.path, canvas)
            }
        }
    }

    public override var insets
        get(   ) = super.insets
        set(new) { super.insets = new }

    init {
        this.selectionModel?.changed?.plusAssign { _,_,_ ->
            val rootDepth = this.selectionModel.root?.depth ?: 0

            this.selectionModel.root?.let { root ->
                var path = Path<Int>()

                root.forEachIndexed { index, element ->
                    path += element
                    val columnIndex = index + 1

                    if (columnIndex < columns.size) {
                        columns[columnIndex].list.disable()

                        columns[columnIndex].path = path
                        columns[columnIndex].list.localSelectionModel?.root = path
                        columns[columnIndex].list.model.replaceAll(model.children(path).asSequence().toList())
                        columns[columnIndex].list.enable()
                    } else {
                        columns += createColumn(path)
                    }
                }
            }

            children.batch {
                when {
                    rootDepth < size - 1 -> {
                        repeat((children.size - 1) - rootDepth) {
                            columns.removeAt(columns.size - 1)
                            removeAt(size - 1) // remove from children
                        }
                    }
                    else                      -> {
                        (size until columns.size).forEach {
                            this += ScrollPanel(columns[it].list).apply { scrollsHorizontally = false } // add to children
                        }
                    }
                }
            }
        }

        columns += createColumn(Path())

        children.addAll(columns.map { ScrollPanel(it.list).apply { scrollsHorizontally = false } })

        layout = object: Layout {
            override fun layout(container: PositionableContainer) {
                val y     = container.insets.top
                var x     = container.insets.left
                val h     = container.height - container.insets.run { top + bottom }
                var width = 0.0

                container.children.forEach {
                    it.bounds = Rectangle(x, y, 100.0 /*it.width*/, h) // FIXME

                    x += it.width

                    width += it.width
                }

                if (width > 0) {
                    container.width = max(container.width, width + container.insets.run { left + right })
                }
            }
        }
    }

    fun visible(path: Path<Int>) = path.parent?.let { selected(it) }

    fun isLeaf(path: Path<Int>) = model.isLeaf(path)

    override fun selected(item: Path<Int>) = selectionModel?.contains(item) ?: false

    fun enclosedBySelection(item: Path<Int>) = selectionModel?.root?.let {
        item == it || item ancestorOf it || item in selectionModel
    } ?: false

    override fun selectAll() {
        selectionModel?.root?.let { root ->
            repeat(model.numChildren(root)) {
                selectionModel.add(root + it)
            }
        }
    }

    override fun addSelection(items: Set<Path<Int>>) { selectionModel?.addAll(items) }

    override fun setSelection(items: Set<Path<Int>>) { selectionModel?.replaceAll(items) }

    override fun removeSelection(items: Set<Path<Int>>) { selectionModel?.removeAll(items) }

    override fun toggleSelection(items: Set<Path<Int>>) { selectionModel?.toggle(items) }

    override fun clearSelection() { selectionModel?.clear() }

    override fun next(after: Path<Int>) = after.parent?.let { parent ->
        val numChildren = model.numChildren(parent)

        after.bottom?.let { bottom ->
            when {
                bottom < numChildren - 1 -> parent + (bottom + 1)
                else                     -> null
            }
        }
    }

    override fun previous(before: Path<Int>) = before.parent?.let { parent ->
        before.bottom?.let { bottom ->
            when {
                bottom > 0 -> parent + (bottom - 1)
                else       -> null
            }
        }
    }

    override val firstSelection  get() = selectionModel?.first
    override val lastSelection   get() = selectionModel?.last
    override val selectionAnchor get() = selectionModel?.anchor
    override val selection       get() = selectionModel?.toSet() ?: emptySet()

    internal fun columnDirty(path: Path<Int>) {
        // FIXME: IMPLEMENT
    }

    private fun createColumn(node: Path<Int>): Column<T> = Column(node, CustomMutableList(
            SimpleMutableListModel(model.children(node).asSequence().toList()),
            itemVisualizer,
            selectionModel?.let { LocalSelectionModel(node, it) },
            fitContent  = false,
            scrollCache = 10
    ).apply {
        width = 100.0 // FIXME

        acceptsThemes = false
    }).also {
        behavior?.let { behavior ->
            installBehavior(it, behavior)
        }
    }
}