package com.kg.gettransfer.utilities

sealed class ScreenNavigationState {
    var currentState: Int = NO_STATE

    companion object {
        const val NO_STATE = 0
    }
}

class MainState: ScreenNavigationState() {
    companion object {
        const val CHOOSE_POINT_ON_MAP   = 1
    }
}