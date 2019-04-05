package com.kg.gettransfer.presentation.ui.days

abstract class GTDayOfWeek {
    abstract val day: Int
    abstract fun getOffset(): Int

    companion object {
        fun getWeekDays() =
                listOf(Monday(), Tuesday(), Wednesday(), Thursday(), Friday(), Saturday(), Sunday())
    }
}

class Monday: GTDayOfWeek() {
    override val day = 1
    override fun getOffset() = -1
}
class Tuesday: GTDayOfWeek() {
    override val day = 2
    override fun getOffset() = -2
}
class Wednesday: GTDayOfWeek() {
    override val day = 3
    override fun getOffset() = -3
}
class Thursday: GTDayOfWeek() {
    override val day = 4
    override fun getOffset() = +3
}
class Friday: GTDayOfWeek() {
    override val day = 5
    override fun getOffset() = +2
}
class Saturday: GTDayOfWeek() {
    override val day = 6
    override fun getOffset() = +1
}
class Sunday: GTDayOfWeek() {
    override val day = 7
    override fun getOffset() = 0
}