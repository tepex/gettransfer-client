package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.presentation.model.DayOfWeekModel
import java.time.DayOfWeek

open class DayOfWeekMapper : Mapper<DayOfWeekModel, DayOfWeek> {
    override fun toView(type: DayOfWeek) = DayOfWeekModel(type)
    override fun fromView(type: DayOfWeekModel): DayOfWeek { throw UnsupportedOperationException() }
}