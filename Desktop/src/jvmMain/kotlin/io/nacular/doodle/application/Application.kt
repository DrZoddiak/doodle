package io.nacular.doodle.application

import io.nacular.doodle.core.Display
import io.nacular.doodle.core.InternalDisplay
import io.nacular.doodle.core.impl.DisplayImpl
import io.nacular.doodle.datatransport.dragdrop.DragManager
import io.nacular.doodle.deviceinput.KeyboardFocusManager
import io.nacular.doodle.deviceinput.PointerInputManager
import io.nacular.doodle.drawing.GraphicsDevice
import io.nacular.doodle.drawing.RenderManager
import io.nacular.doodle.drawing.TextMetrics
import io.nacular.doodle.drawing.impl.DesktopRenderManagerImpl
import io.nacular.doodle.drawing.impl.GraphicsSurfaceFactory
import io.nacular.doodle.drawing.impl.RealGraphicsDevice
import io.nacular.doodle.drawing.impl.RealGraphicsSurface
import io.nacular.doodle.drawing.impl.RealGraphicsSurfaceFactory
import io.nacular.doodle.drawing.impl.TextMetricsImpl
import io.nacular.doodle.scheduler.AnimationScheduler
import io.nacular.doodle.scheduler.Scheduler
import io.nacular.doodle.scheduler.Strand
import io.nacular.doodle.scheduler.impl.AnimationSchedulerImpl
import io.nacular.doodle.scheduler.impl.SchedulerImpl
import io.nacular.doodle.scheduler.impl.StrandImpl
import io.nacular.doodle.time.Timer
import io.nacular.doodle.time.impl.TimerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.datetime.Clock
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontSlant.UPRIGHT
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.PathMeasure
import org.jetbrains.skia.Typeface
import org.jetbrains.skia.paragraph.FontCollection
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEvent
import org.jetbrains.skiko.SkikoInputEvent
import org.jetbrains.skiko.SkikoKeyboardEvent
import org.jetbrains.skiko.SkikoPointerEvent
import org.jetbrains.skiko.SkikoView
import org.kodein.di.Copy.All
import org.kodein.di.DI.Companion.direct
import org.kodein.di.DI.Module
import org.kodein.di.DirectDI
import org.kodein.di.bind
import org.kodein.di.bindFactory
import org.kodein.di.bindInstance
import org.kodein.di.bindSingleton
import org.kodein.di.bindings.NoArgBindingDI
import org.kodein.di.instance
import org.kodein.di.instanceOrNull
import org.kodein.di.singleton
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.UIManager

/**
 * Created by Nicholas Eddy on 5/14/21.
 */
public inline fun <reified T: Application> application(
        allowDefaultDarkMode: Boolean     = false,
        modules             : List<Module> = emptyList(),
        noinline creator    : NoArgBindingDI<*>.() -> T): Application = createApplication(direct {
    bind<Application> { singleton(creator = creator) }
}, allowDefaultDarkMode, modules)

public fun createApplication(
        injector            : DirectDI,
        allowDefaultDarkMode: Boolean,
        modules             : List<Module>): Application = ApplicationHolderImpl(injector, allowDefaultDarkMode = allowDefaultDarkMode, modules = modules)

internal class CustomSkikoView: SkikoView {
    internal var onRender       : (canvas: Canvas, width: Int, height: Int, nanoTime: Long) -> Unit = { _,_,_,_ -> }
    internal var onKeyboardEvent: (SkikoKeyboardEvent) -> Unit = {}
    internal var onPointerEvent : (SkikoPointerEvent ) -> Unit = {}
    internal var onInputEvent   : (SkikoInputEvent   ) -> Unit = {}
    internal var onGestureEvent : (SkikoGestureEvent ) -> Unit = {}

    override fun onKeyboardEvent(event: SkikoKeyboardEvent) = onKeyboardEvent.invoke(event)
    override fun onPointerEvent (event: SkikoPointerEvent ) = onPointerEvent.invoke (event)
    override fun onInputEvent   (event: SkikoInputEvent   ) = onInputEvent.invoke   (event)
    override fun onGestureEvent (event: SkikoGestureEvent ) = onGestureEvent.invoke (event)

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) = onRender.invoke(canvas, width, height, nanoTime)
}

private open class ApplicationHolderImpl protected constructor(
        previousInjector    : DirectDI,
        allowDefaultDarkMode: Boolean      = false,
        modules             : List<Module> = emptyList(),
        private val isNested: Boolean      = false): Application {

    private inner class ShutdownHook: Thread() {
        override fun run() {
            // TODO: Should this be run in the UI thread?
            shutdown()
        }
    }

    private val appScope      = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val skiaWindow    = JFrame().apply {
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    private val skiaLayer = SkiaLayer().apply {
        addView(CustomSkikoView())
    }

    private val defaultFont    = Font(Typeface.makeFromName("Courier", FontStyle(300, 5, UPRIGHT)), 13f)
    private val fontCollection = FontCollection().apply {
        setDefaultFontManager(FontMgr.default)
    }

    private var injector = direct {
        extend(previousInjector, copy = All)

        bindInstance                                               { appScope                                                                                                  }
        bindInstance                                               { Clock.System                                                                                              }
        bindInstance                                               { skiaWindow                                                                                                }
        bindInstance                                               { skiaLayer                                                                                                 }
        bindInstance                                               { defaultFont                                                                                               }
        bindInstance                                               { fontCollection                                                                                            }
        bindFactory<Unit, PathMeasure>                             { PathMeasure               (                                                                             ) }
        bindSingleton<Timer>                                       { TimerImpl                 (instance()                                                                   ) }
        bindSingleton<Strand>                                      { StrandImpl                (instance(), instance()                                                       ) }
        bindSingleton<Display>                                     { DisplayImpl               (instance(), Dispatchers.Swing, instance(), instance(), instance(), instance()) }
        bindSingleton<Scheduler>                                   { SchedulerImpl             (instance(), instance()                                                       ) }
        bindSingleton<TextMetrics>                                 { TextMetricsImpl           (instance(), instance()                                                       ) }
        bindSingleton<RenderManager>                               { DesktopRenderManagerImpl  (instance(), instance(), instanceOrNull(), instanceOrNull(), instance()       ) }
        bindSingleton<AnimationScheduler>                          { AnimationSchedulerImpl    (instance(), Dispatchers.Swing, instance()                                    ) }
        bindSingleton<GraphicsDevice<RealGraphicsSurface>>         { RealGraphicsDevice        (instance()                                                                   ) }
        bindSingleton<GraphicsSurfaceFactory<RealGraphicsSurface>> { RealGraphicsSurfaceFactory(instance(), instance(), instance()                                           ) }

        // TODO: Can this be handled better?
        bindSingleton                                              { instance<Display>           () as DisplayImpl                                                             }
        bindSingleton<InternalDisplay>                             { instance<DisplayImpl>       ()                                                                            }
        bindSingleton                                              { instance<AnimationScheduler>() as AnimationSchedulerImpl                                                  }
        bindSingleton                                              { instance<TextMetrics>       () as TextMetricsImpl                                                         }

        modules.forEach {
            import(it, allowOverride = true)
        }
    }

    private var isShutdown  = false
    private var application = null as Application?

    init {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        System.setProperty("skiko.vsync.enabled", "false")
//        System.setProperty("skiko.fps.enabled",   "true" )
//        System.setProperty("skiko.renderApi", "OPENGL")

        Runtime.getRuntime().addShutdownHook(ShutdownHook())
    }

    protected fun run() {
        skiaLayer.attachTo(skiaWindow.contentPane)

        skiaWindow.preferredSize = Dimension(800, 600)
        skiaWindow.pack()
        skiaWindow.isVisible = true

        injector.instance      <RenderManager>       ()
        injector.instanceOrNull<PointerInputManager> ()
        injector.instanceOrNull<KeyboardFocusManager>()
        injector.instanceOrNull<DragManager>         ()

        application = injector.instance()
    }

    override fun shutdown() {
        if (isShutdown) {
            return
        }

        application?.shutdown()

        (injector.instance<Scheduler> () as? SchedulerImpl)?.shutdown()
        injector.instance<DisplayImpl>().shutdown()
        injector.instance<AnimationSchedulerImpl>().shutdown()
        injector.instanceOrNull<DragManager>             ()?.shutdown()
        injector.instanceOrNull<PointerInputManager>     ()?.shutdown()
        injector.instanceOrNull<KeyboardFocusManager>    ()?.shutdown()
//        injector.instanceOrNull<AccessibilityManagerImpl>()?.shutdown()

        injector = direct {}

        isShutdown = true
    }

    companion object {
        operator fun invoke(previousInjector    : DirectDI,
                            allowDefaultDarkMode: Boolean      = false,
                            modules             : List<Module> = emptyList()): Application {
            return ApplicationHolderImpl(previousInjector, allowDefaultDarkMode, modules).apply {
                appScope.launch(Dispatchers.Swing) {
                    run()
                }
            }
        }
    }
}