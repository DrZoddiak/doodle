package io.nacular.doodle.deviceinputimport io.nacular.doodle.controls.panels.ScrollPanelimport io.nacular.doodle.core.Displayimport io.nacular.doodle.core.Internalimport io.nacular.doodle.core.Viewimport io.nacular.doodle.event.Pointerimport io.nacular.doodle.event.PointerEventimport io.nacular.doodle.event.withimport io.nacular.doodle.system.Cursorimport io.nacular.doodle.system.PointerInputServiceimport io.nacular.doodle.system.SystemPointerEventimport io.nacular.doodle.system.SystemPointerEvent.Typeimport io.nacular.doodle.system.SystemPointerEvent.Type.Clickimport io.nacular.doodle.system.SystemPointerEvent.Type.Downimport io.nacular.doodle.system.SystemPointerEvent.Type.Dragimport io.nacular.doodle.system.SystemPointerEvent.Type.Enterimport io.nacular.doodle.system.SystemPointerEvent.Type.Exitimport io.nacular.doodle.system.SystemPointerEvent.Type.Moveimport io.nacular.doodle.system.SystemPointerEvent.Type.Up@Internalpublic interface PointerInputManager {    public fun shutdown()}@Internal@Suppress("NestedLambdaShadowedImplicitParameter")public class PointerInputManagerImpl(private val display: Display, private val inputService: PointerInputService, private val viewFinder: ViewFinder): PointerInputManager, PointerInputService.Listener {    private inner class ClickedViewMap {        private val map: MutableMap<Int, View> = mutableMapOf()        operator fun set(event: SystemPointerEvent, value: View): View? = map.put(event.id, value)        operator fun get(event: SystemPointerEvent): View? = map[event.id]        operator fun minusAssign(event: SystemPointerEvent) {            map.remove(event.id)?.also {                if (coveredView[event] != it) {                    cleanupPointers(it, event)                }            }        }    }    private inner class CoveredViewMap {        private val map: MutableMap<Int, View> = mutableMapOf()        operator fun set(event: SystemPointerEvent, value: View): View? {            return map.put(event.id, value.also { registerListeners(it) })?.also {                cleanupPointers(it, event)            }        }        operator fun get(event: SystemPointerEvent): View? = map[event.id]        operator fun minusAssign(event: SystemPointerEvent) {            map.remove(event.id)?.also {                cleanupPointers(it, event)            }        }    }    private val pressedPointers = mutableSetOf<Int>()    private fun isPointerDown(event: SystemPointerEvent) = event.id in pressedPointers    private var latestSystemEvent = null as SystemPointerEvent?    private val clickedView = ClickedViewMap()    private val coveredView = CoveredViewMap()    private val targetedPointers = mutableMapOf<View, MutableList<Pointer>>()    private fun cleanupPointers(view: View, event: SystemPointerEvent) {        unregisterListeners(view)        targetedPointers[view]?.let {            it.removeAll { it.id == event.id }            if (it.isEmpty()) {                targetedPointers -= view            }        }    }    private var cursor = null as Cursor?        set(new) {            field = new            inputService.cursor = cursor ?: display.cursor        }    private var toolTipText = ""        set(new) {            field = new            inputService.toolTipText = field        }    private val displayCursorChanged = { _: Display, _: Cursor?, new: Cursor? -> cursor = new }    private val enabledChanged = { view: View, _: Boolean, enabled: Boolean ->//        latestSystemEvent?.let {//            coveredView = view(from = it)//            when {//                enabled -> view.handlePointerEvent_(createPointerEvent(it, view, Enter))//                else    -> view.handlePointerEvent_(createPointerEvent(it, view, Exit ))//            }//        }//        Unit    }    private val viewCursorChanged = { view: View, _: Cursor?, _: Cursor? ->        cursor = cursor(of = view)    }    init {        inputService += this        display.cursorChanged += displayCursorChanged        cursor = display.cursor    }    override fun shutdown() {        inputService -= this        display.cursorChanged -= displayCursorChanged    }    override fun changed(event: SystemPointerEvent) {        when (event.type) {            Up -> when(event.clickCount) {                1    -> pointerUp  (event)                else -> doubleClick(event)            }            Move -> pointerMove(event)            Down -> pointerDown(event)            Exit -> pointerExit(event)            else -> {}        }    }    private fun pointerExit(event: SystemPointerEvent) {        clickedView[event]?.let {            deliver(event, createPointerEvent(event, it, Exit))            clickedView -= event        }        coveredView[event]?.let {            deliver(event, createPointerEvent(event, it, Exit))            coveredView -= event            cleanupPointers(it, event)        }        pressedPointers -= event.id    }    private fun pointerUp(event: SystemPointerEvent) {        latestSystemEvent = event        val view = view(from = event)        if (clickedView[event] != null || isPointerDown(event)) {            clickedView[event]?.let {                deliver(event, createPointerEvent(event, it))                if (view === it) {                    deliver(event, createPointerEvent(event, it, Click))                }            }            if (view !== clickedView[event]) {                clickedView[event]?.let {                    // Avoid case where pointer-move hasn't been seen (possible if drag-drop happened)                    if (coveredView[event] == it) {                        coveredView -= event                        deliver(event, createPointerEvent(event, it, Exit))                    }                }                if (view != null) {                    coveredView[event] = view                    deliver(event, createPointerEvent(event, view, Enter))                    deliver(event, createPointerEvent(event, view       ))                    cursor = cursor(of = view)                } else {                    cursor = display.cursor                }            } else {                cursor = cursor(of = view)            }            clickedView -= event        } else if (view != null) {            coveredView[event] = view            deliver(event, createPointerEvent(event, view, Enter))            deliver(event, createPointerEvent(event, view       ))            cursor = cursor(of = view)        } else {            cursor = display.cursor        }        pressedPointers -= event.id    }    private fun pointerDown(event: SystemPointerEvent) {        latestSystemEvent = event        toolTipText = ""        view(from = event)?.let { view ->            if (view != coveredView[event]) {                createPointerEvent(event, view, Enter).also {                    deliver(event, it)                    toolTipText = view.toolTipText(it)                }                coveredView[event] = view                cursor      = cursor(of = coveredView[event])            }            deliver(event, createPointerEvent(event, view))            clickedView[event] = view        }        pressedPointers += event.id    }    private fun doubleClick(event: SystemPointerEvent) {        toolTipText = ""        view(from = event)?.let {            coveredView[event] = it            deliver(event, createPointerEvent(event, it, Up   ))            deliver(event, createPointerEvent(event, it, Click))            clickedView -= event        }    }    private fun pointerMove(event: SystemPointerEvent) {        latestSystemEvent = event        clickedView[event]?.let {            deliver(event, createPointerEvent(event, it, Drag))            cursor = cursor(of = it)        }        val view = view(from = event)        if (view !== coveredView[event]) {            coveredView[event]?.let {                if (!isPointerDown(event) || it === clickedView[event]) {                    deliver(event, createPointerEvent(event, it, Exit))                }            }            when (view) {                null -> coveredView        -= event                else -> coveredView[event]  = view            }            if (view != null) {                if (!isPointerDown(event) || view === clickedView[event]) {                    createPointerEvent(event, view, Enter).also {                        deliver(event, it)                        toolTipText = view.toolTipText(it)                    }                    cursor = cursor(of = coveredView[event])                }            } else if (clickedView[event] == null) {                toolTipText = ""                cursor = null            }        } else if (!isPointerDown(event)) {            coveredView[event]?.let {                deliver(event, createPointerEvent(event, it, Move))            }            if (coveredView[event] == null) {                toolTipText = ""            }            cursor = cursor(of = coveredView[event])        }    }    private fun deliver(systemEvent: SystemPointerEvent, event: PointerEvent): Boolean {        val chain = mutableListOf(event.target)        var view = event.target.parent        while (view != null) {            if (view.enabled && view.visible) {                chain += view            }            view = view.parent        }        // Sinking        chain.asReversed().forEach {            val newEvent = event.with(source = it)            when (newEvent.type) {                Move, Drag -> it.filterPointerMotionEvent_(newEvent)                else       -> it.filterPointerEvent_      (newEvent)            }            if (newEvent.consumed) {                systemEvent.consume()                return true            }        }        // Floating        chain.forEach {            val newEvent = event.with(source = it)            when (newEvent.type) {                Move, Drag -> it.handlePointerMotionEvent_(newEvent)                else       -> it.handlePointerEvent_      (newEvent)            }            if (newEvent.consumed) {                systemEvent.consume()                return true            }        }        return false    }    private fun registerListeners(view: View) {        view.cursorChanged  += viewCursorChanged        view.enabledChanged += enabledChanged    }    private fun unregisterListeners(view: View) {        view.cursorChanged  -= viewCursorChanged        view.enabledChanged -= enabledChanged    }    private fun cursor(of: View?) = when (display.cursor) {        null -> of?.cursor        else -> display.cursor    }    private fun view(from: SystemPointerEvent): View? {        var view = viewFinder.find(from.location)        return view?.let {            if (from.nativeScrollPanel) {                while(view != null && view !is ScrollPanel) {                    view = view?.parent                }            }            view        }    }    private fun createPointerEvent(event: SystemPointerEvent, target: View, type: Type = event.type): PointerEvent {        targetedPointers.getOrPut(target) { mutableListOf() }.let { list ->            list.removeAll { it.id == event.id }            list += createPointer(target, event, type)        }        return PointerEvent(                target,                target,                event.buttons,                event.clickCount,                targetPointers    = targetedPointers[target]!!,                changedPointers   = setOf(createPointer(target, event, type)),                allActivePointers = { targetedPointers.values.asSequence().flatten().toList() },                modifiers         = event.modifiers)    }    private fun createPointer(target: View, event: SystemPointerEvent, type: Type = event.type) = Pointer(            event.id,            target,            type,            target.fromAbsolute(event.location)    )}