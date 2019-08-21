package com.kg.gettransfer.presentation.ui.helpers

import java.util.Date

interface DateTimeHandler {
    fun onDateChosen(date: Date)
    fun onTimeChosen(date: Date)
}