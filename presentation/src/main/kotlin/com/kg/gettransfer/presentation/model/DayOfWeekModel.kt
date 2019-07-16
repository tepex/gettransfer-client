package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek
import java.text.DateFormatSymbols

class DayOfWeekModel(val delegate: GTDayOfWeek) : CharSequence {
    private val nameLowerCase = DateFormatSymbols.getInstance().weekdays[delegate.day]
    val name = nameLowerCase.substring(0, 1).toUpperCase().plus(nameLowerCase.substring(1))
    override val length = name.length

    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}