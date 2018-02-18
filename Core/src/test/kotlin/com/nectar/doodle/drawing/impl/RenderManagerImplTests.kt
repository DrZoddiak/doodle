package com.nectar.doodle.drawing.impl

import com.nectar.doodle.JsName
import com.nectar.doodle.core.Container
import com.nectar.doodle.core.Display
import com.nectar.doodle.core.Gizmo
import com.nectar.doodle.drawing.Canvas
import com.nectar.doodle.drawing.GraphicsDevice
import com.nectar.doodle.drawing.GraphicsSurface
import com.nectar.doodle.geometry.Rectangle
import com.nectar.doodle.geometry.Size
import com.nectar.doodle.scheduler.Scheduler
import com.nectar.doodle.scheduler.Task
import com.nectar.doodle.theme.InternalThemeManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import kotlin.test.Test

/**
 * Created by Nicholas Eddy on 11/6/17.
 */

class RenderManagerImplTests {
    @Test
    @JsName("renderIgnoresUnknownGizmos")
    fun `render ignores unknown Gizmos`() {
        val gizmo = spyk(gizmo())

        renderManager().render(gizmo)

        verify(exactly = 0) { gizmo.render(any()) }
    }

    @Test
    @JsName("rendersDisplayedGizmos")
    fun `renders displayed Gizmos`() {
        val gizmos = (0 until 2).mapTo(mutableListOf()) { spyk(gizmo()) }

        val display = display(*gizmos.toTypedArray())

        renderManager(display)

        gizmos.forEach {
            verify(atLeast = 1) { it.render(any()) } // TODO: tighten this up
        }
    }

    @Test
    @JsName("doesNotRenderInvisibleGizmos")
    fun `does not render invisible Gizmos`() = doesNotRender(spyk(gizmo()).apply { visible = false })

    @Test
    @JsName("doesNotRenderZeroBoundsGizmos")
    fun `does not render zero bounds Gizmos`() = doesNotRender(spyk(gizmo()).apply { bounds = Rectangle.Empty })

    @Test
    @JsName("renderNowIgnoresUnknownGizmos")
    fun `renderNow ignores unknown Gizmos`() {
        val gizmo = spyk(gizmo())

        renderManager().renderNow(gizmo)

        verify(exactly = 0) { gizmo.render(any()) }
    }

    private fun gizmo(): Gizmo = object: Gizmo() {}.apply { bounds = Rectangle(size = Size(10.0, 10.0)) }

    private fun doesNotRender(gizmo: Gizmo) {
        renderManager(display(gizmo))

        verify(exactly = 0) { gizmo.render(any()) }
    }

    private fun renderManager(
            display       : Display              = mockk(relaxed = true),
            themeManager  : InternalThemeManager = mockk(relaxed = true),
            scheduler     : Scheduler            = instantScheduler,
            graphicsDevice: GraphicsDevice<*>    = defaultGraphicsDevice) = RenderManagerImpl(display, scheduler, themeManager, graphicsDevice)

    private val defaultGraphicsDevice by lazy {
        val result = mockk<GraphicsDevice<*>>(relaxed = true)

        val surface = mockk<GraphicsSurface>(relaxed = true)

        every { surface.render(captureLambda()) } answers {
            lambda<(Canvas) -> Unit>().captured(mockk(relaxed = true))
        }

        every { result[any()] } answers { surface }

        result
    }

    private fun display(vararg children: Gizmo): Display = mockk<Display>(relaxed = true).apply {
        val container = Container()

        container.children.addAll(children)

        val gizmo = slot<Gizmo>()

        every { this@apply.children                   } returns container.children
        every { this@apply.iterator()                 } answers { container.children.iterator() }
        every { sizeChanged                           } returns mockk(relaxed = true)
        every { this@apply.isAncestor(capture(gizmo)) } answers { container.isAncestor(gizmo.captured) }
    }

    private val instantScheduler by lazy { mockk<Scheduler>(relaxed = true).apply {
        every { this@apply.after(any(), captureLambda()) } answers {
            lambda<() -> Unit>().captured()

            val task = mockk<Task>()

            every { task.completed } returns true

            task
        }
    }}
}