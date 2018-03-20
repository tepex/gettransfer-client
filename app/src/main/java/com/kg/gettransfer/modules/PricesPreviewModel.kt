package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.PriceRange
import com.kg.gettransfer.realm.TransportType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import java.util.*
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class PricesPreviewModel(
        private val api: HttpApi,
        private val transportTypes: TransportTypes)
    : AsyncModel(), KoinComponent {

    private val log = Logger.getLogger("PriceRange")

    private val brPrices: BehaviorRelay<Map<TransportType, PriceRange>> = BehaviorRelay.create()


    fun addOnPricesUpdated(f: ((Map<TransportType, PriceRange>) -> Unit)): Disposable =
            brPrices.observeOn(AndroidSchedulers.mainThread()).subscribe(f)

    val prices: Map<TransportType, PriceRange>? get() = brPrices.value


    fun get(transfer: NewTransfer) {
        if (busy) return

        val request =
                if (transfer.to != null)
                    getRequest(transfer.from!!.point,
                            transfer.to!!.point,
                            transfer.dateTo?.date ?: Date().toString(),
                            transfer.routeDistance ?: 0,
                            transfer.dateReturn != null)
                else
                    getRequest(transfer.from!!.point,
                            transfer.dateTo?.date ?: Date().toString(),
                            (transfer.hireDuration ?: 0) * 60 * 60)

        disposables.add(request.fastSubscribe {
            brPrices.accept(it?.mapKeys { transportTypes.typesMap[it.key]!! })
        })
    }

    private fun getRequest(llFrom: String, llTo: String, date: String, distance: Int, back: Boolean) =
            api.getPrice(arrayOf(llFrom, llTo), date, distance, back)

    private fun getRequest(llFrom: String, date: String, hireDuration: Int) =
            api.getPrice(arrayOf(llFrom), date, hireDuration)
}