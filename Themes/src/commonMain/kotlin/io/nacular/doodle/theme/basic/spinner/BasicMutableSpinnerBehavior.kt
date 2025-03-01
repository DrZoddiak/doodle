package io.nacular.doodle.theme.basic.spinner

import io.nacular.doodle.controls.EditOperation
import io.nacular.doodle.controls.spinner.MutableSpinnerModel
import io.nacular.doodle.controls.spinner.MutableSpinner
import io.nacular.doodle.controls.spinner.MutableSpinnerBehavior
import io.nacular.doodle.controls.spinner.Spinner
import io.nacular.doodle.core.View
import io.nacular.doodle.drawing.Canvas
import io.nacular.doodle.drawing.Color
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.event.PointerEvent
import io.nacular.doodle.event.PointerListener
import io.nacular.doodle.focus.FocusManager
import io.nacular.doodle.geometry.Point
import io.nacular.doodle.theme.basic.ColorMapper
import io.nacular.doodle.theme.basic.GenericTextEditOperation
import io.nacular.doodle.utils.Encoder

public class BasicMutableSpinnerBehavior<T, M: MutableSpinnerModel<T>>(
        textMetrics        : TextMetrics,
        backgroundColor    : Color,
        darkBackgroundColor: Color,
        foregroundColor    : Color,
        cornerRadius       : Double,
        buttonWidth        : Double,
        focusManager       : FocusManager? = null): MutableSpinnerBehavior<T, M>(), PointerListener {

    private val delegate = BasicSpinnerBehavior<T, M>(
            textMetrics,
            backgroundColor,
            darkBackgroundColor,
            foregroundColor,
            cornerRadius,
            buttonWidth,
            focusManager
    ).apply {
        centerChanged += { spinner, old, new ->
            (spinner as? MutableSpinner<T, M>)?.let {
                spinner.cancelEditing()
                setupEditingTriggers(old, new)
            }
        }
    }

    public var hoverColorMapper   : ColorMapper get() = delegate.hoverColorMapper;    set(new) { delegate.hoverColorMapper    = new }
    public var disabledColorMapper: ColorMapper get() = delegate.disabledColorMapper; set(new) { delegate.disabledColorMapper = new }

    override fun contains             (view: Spinner<T, M>, point: Point): Boolean = delegate.contains(view, point)
    override fun mirrorWhenRightToLeft(view: Spinner<T, M>              ): Boolean = delegate.mirrorWhenRightToLeft(view)
    override fun clipCanvasToBounds   (view: Spinner<T, M>              ): Boolean = delegate.clipCanvasToBounds(view)
    override fun install              (view: Spinner<T, M>              ) { delegate.install(view) }

    override fun uninstall(view: Spinner<T, M>): Unit = delegate.uninstall(view)

    override fun render(view: Spinner<T, M>, canvas: Canvas) { delegate.render(view, canvas) }

    override fun editingStarted(spinner: MutableSpinner<T, M>, value: T): EditOperation<T>? {
        val center = delegate.visualizedValue(spinner)

        return spinner.editor?.edit(spinner, value, center!!)?.also { operation ->
            operation()?.let { newCenter -> delegate.updateCenter(spinner, oldCenter = center, newCenter) }
        }
    }

    override fun editingEnded(spinner: MutableSpinner<T, M>) {
        delegate.updateCenter(spinner)
    }

    override fun changed(spinner: Spinner<T, M>): Unit = delegate.changed(spinner)

    override fun pressed(event: PointerEvent) {
        event.source.let { spinner ->
            if (event.clickCount >= 2 && spinner is MutableSpinner<*,*>) {
                spinner.startEditing()
                event.consume()
            }
        }
    }

    private fun setupEditingTriggers(old: View?, new: View) {
        old?.pointerFilter?.minusAssign(this)
        new.pointerFilter += this
    }
}

public open class SpinnerTextEditOperation<T>(
                    focusManager: FocusManager?,
                    mapper      : Encoder<T, String>,
        private val spinner     : MutableSpinner<T, *>,
                    value       : T,
                    current     : View): GenericTextEditOperation<T, MutableSpinner<T, *>>(focusManager, mapper, spinner, value, current) {

    private val changed = { _: Spinner<T, *> ->
        spinner.cancelEditing()
    }

    init {
        spinner.changed += changed
    }

    override fun cancel() {
        spinner.changed -= changed
    }
}