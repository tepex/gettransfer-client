package com.kg.gettransfer.presentation.mapper

import android.text.format.DateUtils

import com.kg.gettransfer.domain.model.CarrierTripBase

import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemsListModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel
import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel.Type

import com.kg.gettransfer.presentation.ui.SystemUtils

import java.util.Calendar
import java.util.Date

import org.koin.core.get
import org.koin.core.KoinComponent

open class CarrierTripsListItemsMapper: KoinComponent {
    private val carrierTripBaseMapper = get<CarrierTripBaseMapper>()

    fun toRecyclerView(type: List<CarrierTripBase>): CarrierTripsRVItemsListModel {
        val rvItemsList = arrayListOf<CarrierTripsRVItemModel>()
        var startTodayPosition: Int? = null
        var endTodayPosition: Int? = null

        val dateNow = Calendar.getInstance().time
        var isToday: Boolean
        var dateLastItem: Date? = null

        if (!type.isNullOrEmpty()) {
            var i = 0
            while (i < type.size) {
                val trip = type[i]
                var itemDate = trip.dateLocal
                isToday = DateUtils.isToday(itemDate.time)

                if (startTodayPosition == null && isToday) startTodayPosition = rvItemsList.size
                if (startTodayPosition == null && dateNow.before(itemDate)) { // if today there are no trips
                    startTodayPosition = rvItemsList.size
                    isToday = true
                    itemDate = dateNow
                }

                if (dateLastItem == null || equalsDates(dateLastItem, itemDate, true)) {
                    addViewNewMonth(rvItemsList, itemDate)
                }
                if (dateLastItem == null || equalsDates(dateLastItem, itemDate, false)) {
                    addViewNewDay(rvItemsList, itemDate, isToday)
                }
                dateLastItem = itemDate

                if (isToday && !DateUtils.isToday(trip.dateLocal.time)) { // if today there are no trips
                    endTodayPosition = addViewEndOfToday(rvItemsList)
                } else {
                    addViewItem(rvItemsList, carrierTripBaseMapper.toView(trip))
                    if (isToday && (i == type.size - 1 || i + 1 < rvItemsList.size && !DateUtils.isToday(type[i + 1].dateLocal.time))) { //
                        endTodayPosition = addViewEndOfToday(rvItemsList)
                    }
                    i++
                }
            }
        } else {
            startTodayPosition = 0
            endTodayPosition = addTodayViews(rvItemsList, dateNow)
        }

        if (startTodayPosition == null) {
            startTodayPosition = rvItemsList.size
            endTodayPosition = addTodayViews(rvItemsList, dateNow)
        }

        return CarrierTripsRVItemsListModel(rvItemsList, startTodayPosition, endTodayPosition!!)
    }

    private fun addTodayViews(rvItemsList: MutableList<CarrierTripsRVItemModel>, date: Date): Int {
        addViewNewMonth(rvItemsList, date)
        addViewNewDay(rvItemsList, date, true)
        return addViewEndOfToday(rvItemsList)
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

    private fun addViewNewMonth(rvItemsList: MutableList<CarrierTripsRVItemModel>, date: Date) {
        rvItemsList.add(CarrierTripsRVItemModel(Type.TITLE, SystemUtils.formatMonth(date), null,  null))
    }

    private fun addViewNewDay(rvItemsList: MutableList<CarrierTripsRVItemModel>, date: Date, isToday: Boolean) {
        rvItemsList.add(CarrierTripsRVItemModel(Type.SUBTITLE, SystemUtils.formatDayMonth(date), isToday, null))
    }

    private fun addViewItem(rvItemsList: MutableList<CarrierTripsRVItemModel>, item: CarrierTripBaseModel) {
        rvItemsList.add(CarrierTripsRVItemModel(Type.ITEM, null, null, item))
    }

    private fun addViewEndOfToday(rvItemsList: MutableList<CarrierTripsRVItemModel>): Int {
        rvItemsList.add(CarrierTripsRVItemModel(CarrierTripsRVItemModel.Type.END_TODAY_VIEW, null, null, null))
        return rvItemsList.size
    }
}
