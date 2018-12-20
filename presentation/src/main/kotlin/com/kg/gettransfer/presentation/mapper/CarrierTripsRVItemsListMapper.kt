package com.kg.gettransfer.presentation.mapper

import android.text.format.DateUtils

import com.kg.gettransfer.domain.model.CarrierTripBase

import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemsListModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import java.util.Calendar
import java.util.Date

import org.koin.standalone.get
import org.koin.standalone.KoinComponent

open class CarrierTripsRVItemsListMapper: KoinComponent {
    private val carrierTripBaseMapper = get<CarrierTripBaseMapper>()

    fun toRecyclerView(type: List<CarrierTripBase>): CarrierTripsRVItemsListModel {
        val rvItemsList = arrayListOf<CarrierTripsRVItemModel>()
        var startTodayPosition: Int? = null
        var endTodayPosition: Int? = null

        var i = 0
        val dateNow = Calendar.getInstance().time
        var isToday: Boolean
        var dateLastItem: Date? = null

        while (i < type.size) {
            val trip = type[i]
            var itemDate = trip.dateLocal
            isToday = DateUtils.isToday(itemDate.time)

            if(isToday) startTodayPosition = rvItemsList.size
            if (startTodayPosition == null && dateNow.before(itemDate)) { // if today there are no trips
                startTodayPosition = rvItemsList.size
                isToday = true
                itemDate = dateNow
            }

            if(dateLastItem == null || equalsDates(dateLastItem, itemDate, true)){
                addViewNewMonth(rvItemsList, itemDate)
            }
            if(dateLastItem == null || equalsDates(dateLastItem, itemDate, false)){
                addViewNewDay(rvItemsList, itemDate, isToday)
            }
            dateLastItem = itemDate

            if (isToday && !DateUtils.isToday(trip.dateLocal.time)) { // if today there are no trips
                endTodayPosition = addViewEndOfToday(rvItemsList)
            } else {
                addViewItem(rvItemsList, carrierTripBaseMapper.toView(trip))
                if (isToday && i + 1 < rvItemsList.size && !DateUtils.isToday(type[i + 1].dateLocal.time)) { //
                    endTodayPosition = addViewEndOfToday(rvItemsList)
                }
                i++
            }
        }
        return CarrierTripsRVItemsListModel(rvItemsList, startTodayPosition!!, endTodayPosition!!)
    }

    private fun equalsDates(dateFirst: Date, dateSecond: Date, checkWithoutDayOfMonth: Boolean): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = dateFirst

        val thenYear = calendar.get(Calendar.YEAR)
        val thenMonth = calendar.get(Calendar.MONTH)
        val thenMonthDay = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.time = dateSecond
        return if(!checkWithoutDayOfMonth) !(thenYear == calendar.get(Calendar.YEAR)
                && thenMonth == calendar.get(Calendar.MONTH)
                && thenMonthDay == calendar.get(Calendar.DAY_OF_MONTH))
        else !(thenYear == calendar.get(Calendar.YEAR)
                && thenMonth == calendar.get(Calendar.MONTH))
    }

    private fun addViewNewMonth(rvItemsList: ArrayList<CarrierTripsRVItemModel>, date: Date) {
        rvItemsList.add(CarrierTripsRVItemModel(CarrierTripsRVItemModel.TYPE_TITLE, SystemUtils.formatMonth(date), null,  null))
    }

    private fun addViewNewDay(rvItemsList: ArrayList<CarrierTripsRVItemModel>, date: Date, isToday: Boolean) {
        rvItemsList.add(CarrierTripsRVItemModel(CarrierTripsRVItemModel.TYPE_SUBTITLE, SystemUtils.formatDayMonth(date), isToday, null))
    }

    private fun addViewItem(rvItemsList: ArrayList<CarrierTripsRVItemModel>, item: CarrierTripBaseModel) {
        rvItemsList.add(CarrierTripsRVItemModel(CarrierTripsRVItemModel.TYPE_ITEM, null, null, item))
    }

    private fun addViewEndOfToday(rvItemsList: ArrayList<CarrierTripsRVItemModel>): Int {
        rvItemsList.add(CarrierTripsRVItemModel(CarrierTripsRVItemModel.TYPE_END_TODAY_VIEW, null, null, null))
        return rvItemsList.size
    }
}
