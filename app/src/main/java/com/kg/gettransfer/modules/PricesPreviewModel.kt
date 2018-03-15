package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.PricesPreview
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import java.util.*
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class PricesPreviewModel(
        private val api: HttpApi,
        private val transportTypes: TransportTypesProvider)
    : AsyncModel(), KoinComponent {

    private val log = Logger.getLogger("PricesPreview")

    private val prices: BehaviorRelay<Map<Int, PricesPreview>> = BehaviorRelay.create()


    fun addOnPricesUpdated(f: ((Map<Int, PricesPreview>) -> Unit)): Disposable =
            prices.observeOn(AndroidSchedulers.mainThread()).subscribe(f)


    fun get(transfer: NewTransfer) {
        if (transfer.to != null) {
            get(transfer.from!!.point,
                    transfer.to!!.point,
                    transfer.dateTo?.date ?: Date().toString(),
                    transfer.routeDistance ?: 0,
                    transfer.dateReturn != null)
        } else {
            get(transfer.from!!.point,
                    transfer.dateTo?.date ?: Date().toString(),
                    (transfer.hireDuration ?: 0) * 60 * 60)
        }
    }


    fun get(llFrom: String, llTo: String, date: String, distance: Int, back: Boolean) {
        if (isBusy) return
        disposables.add(
                api.getPrice(arrayOf(llFrom, llTo), date, distance, back)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { setBusy(true) }
                        .observeOn(Schedulers.newThread())
                        .doFinally { setBusy(false) }
                        .subscribe(
                                { response ->
                                    //response.data.map {  }
                                    prices.accept(response.data)
                                },
                                {
                                    log.info(it.toString())
                                    err(it)
                                }))
    }

    fun get(llFrom: String, date: String, hireDuration: Int) {
        if (isBusy) return
        disposables.add(
                api.getPrice(arrayOf(llFrom), date, hireDuration)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { setBusy(true) }
                        .observeOn(Schedulers.newThread())
                        .doFinally { setBusy(false) }
                        .subscribe(
                                { response ->
                                    //response.data.map {  }
                                    prices.accept(response.data)
                                },
                                {
                                    log.info(it.toString())
                                    err(it)
                                }))
    }
}