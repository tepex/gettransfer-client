package com.kg.gettransfer.presentation.delegate

interface ChildSeatsView {
    val minusEnabled: Int
    val minusDisabled: Int
    fun updateView(count: Int, type: Int)
}