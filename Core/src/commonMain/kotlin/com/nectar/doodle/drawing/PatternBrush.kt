package com.nectar.doodle.drawing

import com.nectar.doodle.geometry.Rectangle
import com.nectar.doodle.geometry.Size


/**
 * A [Brush] that repeats the contents drawn to a [Canvas].
 *
 * @author Nicholas Eddy
 *
 * @property size The size of the Canvas that will be repeated
 * @property fill Specifies the render operations for the Canvas
 */
class PatternBrush(val size: Size, val fill: Canvas.() -> Unit): Brush() {
    override val visible = !size.empty
}

/**
 * Creates a [PatternBrush] that draws an alternating horizontal striped pattern.
 */
fun horizontalStripedBrush(rowHeight: Double, evenRowColor: Color? = null, oddRowColor: Color? = null) = PatternBrush(Size(rowHeight, 2 * rowHeight)) {
    evenRowColor?.let { rect(Rectangle(                rowHeight, rowHeight), ColorBrush(it)) }
    oddRowColor?.let  { rect(Rectangle(0.0, rowHeight, rowHeight, rowHeight), ColorBrush(it)) }
}

/**
 * Creates a [PatternBrush] that draws an alternating vertical striped pattern.
 */
fun verticalStripedBrush(colWidth: Double, evenRowColor: Color? = null, oddRowColor: Color? = null) = PatternBrush(Size(2 * colWidth, colWidth)) {
    evenRowColor?.let { rect(Rectangle(               colWidth, colWidth), ColorBrush(it)) }
    oddRowColor?.let  { rect(Rectangle(colWidth, 0.0, colWidth, colWidth), ColorBrush(it)) }
}

fun checkerBrush(checkerSize: Size, firstColor: Color? = null, secondColor: Color? = null) = PatternBrush(checkerSize * 2) {
    val w  = checkerSize.width
    val h  = checkerSize.height
    val b1 = firstColor?.let  { ColorBrush(it) }
    val b2 = secondColor?.let { ColorBrush(it) }

    b1?.let { rect(Rectangle(0.0, 0.0, w, h), it) }
    b2?.let { rect(Rectangle(0.0,   h, w, h), it) }
    b2?.let { rect(Rectangle(w,   0.0, w, h), it) }
    b1?.let { rect(Rectangle(w,     h, w, h), it) }
}

