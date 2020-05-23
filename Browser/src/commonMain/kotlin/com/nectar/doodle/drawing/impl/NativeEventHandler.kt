package com.nectar.doodle.drawing.impl

import com.nectar.doodle.HTMLElement


internal typealias NativeEventHandlerFactory = (element: HTMLElement, listener: NativeEventListener) -> NativeEventHandler

internal interface NativeEventHandler {
    fun startConsumingPointerMoveEvents(onlySelf: Boolean = false)
    fun stopConsumingPointerMoveEvents ()

    fun startConsumingPointerPressEvents()
    fun stopConsumingPointerPressEvents ()

    fun startConsumingSelectionEvents()
    fun stopConsumingSelectionEvents ()

    fun registerFocusListener  ()
    fun unregisterFocusListener()

    fun registerFocusInListener  ()
    fun unregisterFocusInListener()

    fun registerClickListener  ()
    fun unregisterClickListener()

    fun registerKeyListener  ()
    fun unregisterKeyListener()

    fun registerScrollListener  ()
    fun unregisterScrollListener()

    fun registerChangeListener  ()
    fun unregisterChangeListener()

    fun registerInputListener  ()
    fun unregisterInputListener()
}