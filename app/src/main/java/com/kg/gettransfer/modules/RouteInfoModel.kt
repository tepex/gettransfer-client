package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.RouteInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import java.util.*


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class RouteInfoModel(
        private val api: HttpApi,
        private val transportTypes: TransportTypes)
    : AsyncModel(), KoinComponent {

    private val brPrices: BehaviorRelay<RouteInfo> = BehaviorRelay.create()

    fun addOnUpdated(f: (RouteInfo) -> Unit): Disposable =
            brPrices.observeOn(AndroidSchedulers.mainThread()).subscribe(f)

    val info: RouteInfo?
        get() = brPrices.value


    fun get(transfer: NewTransfer) {
        val request =
                if (transfer.to != null)
                    getRequest(transfer.from!!.point,
                            transfer.to!!.point,
                            transfer.dateTo?.date ?: Date().toString(),
                            transfer.dateReturn != null)
                else
                    getRequest(transfer.from!!.point,
                            transfer.dateTo?.date ?: Date().toString(),
                            (transfer.hireDuration ?: 0) * 60 * 60)

        disposables.add(request.fastSubscribe {
            val pricesRaw = it.prices ?: return@fastSubscribe
            val prices = pricesRaw.mapKeys { transportTypes.typesMap[it.key]!! }

            val info = RouteInfo()
            info.distance = it.distance
            info.duration = it.duration
            info.prices = prices

            brPrices.accept(info)
        })
    }

    private fun getRequest(llFrom: String, llTo: String, date: String, back: Boolean) =
            api.getRouteInfo(arrayOf(llFrom, llTo), date, back)

    private fun getRequest(llFrom: String, date: String, hireDuration: Int) =
            api.getPrice(arrayOf(llFrom), date, hireDuration)
}