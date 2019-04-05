package com.kg.gettransfer.presentation.model

import java.text.DateFormatSymbols
import java.time.DayOfWeek

class DayOfWeekModel(val delegate: DayOfWeek) : CharSequence {
    private val nameLowerCase = DateFormatSymbols.getInstance().weekdays[delegate.ordinal + 1]
    val name = nameLowerCase.substring(0, 1).toUpperCase().plus(nameLowerCase.substring(1))
    override val length = name.length

    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}

class DayOfWeekModel1(val delegate: com.kg.gettransfer.presentation.ui.days.GTDayOfWeek) : CharSequence {
    private val nameLowerCase = DateFormatSymbols.getInstance().weekdays[delegate.day + 1]
    val name = nameLowerCase.substring(0, 1).toUpperCase().plus(nameLowerCase.substring(1))
    override val length = name.length

    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}