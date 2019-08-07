package com.kg.gettransfer.utilities

sealed class ScreenNavigationState {
    var currentState: Int = NO_STATE

    fun reset() {
        currentState = NO_STATE
    }

    companion object {
        const val NO_STATE = 0
    }
}

class NewTransferState : ScreenNavigationState() {

    val isSwitchToMain: Boolean
        get() = currentState == SWITCH_TO_MAIN
    val isChoosePointOnMap: Boolean
        get() = currentState == CHOOSE_POINT_ON_MAP

    fun switchToMain() {
        currentState = SWITCH_TO_MAIN
    }

    fun choosePointOnMap() {
        currentState = CHOOSE_POINT_ON_MAP
    }

    companion object {
        private const val SWITCH_TO_MAIN = 1
        private const val CHOOSE_POINT_ON_MAP = 2
    }
}