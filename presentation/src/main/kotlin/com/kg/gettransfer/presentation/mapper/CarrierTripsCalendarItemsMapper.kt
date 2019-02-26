package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import kotlin.collections.HashMap

open class CarrierTripsCalendarItemsMapper : KoinComponent {
    private val carrierTripBaseMapper = get<CarrierTripBaseMapper>()

    fun toCalendarView(type: List<CarrierTripBase>) : Map<String, List<CarrierTripBaseModel>> {
        val mapCarrierTrips = HashMap<String, List<CarrierTripBaseModel>>()
        type.forEach {
            val calendarWithoutTime = SystemUtils.formatDateWithoutTime(it.dateLocal)
            if (mapCarrierTrips.containsKey(calendarWithoutTime)){
                val tripsList = mapCarrierTrips[calendarWithoutTime]!!.toMutableList()
                tripsList.add(carrierTripBaseMapper.toView(it))
                mapCarrierTrips[calendarWithoutTime] = tripsList
            } else {
                mapCarrierTrips[calendarWithoutTime] = listOf(carrierTripBaseMapper.toView(it))
            }
        }
        return mapCarrierTrips
    }
}