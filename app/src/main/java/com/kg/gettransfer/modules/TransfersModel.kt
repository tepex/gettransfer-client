package com.kg.gettransfer.modules


import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.NewTransferField
import com.kg.gettransfer.realm.Transfer
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 30/01/2018.
 */


// Singleton
class TransfersModel(
        private val realm: Realm,
        private val api: HttpApi,
        currentAccount: CurrentAccount)
    : AsyncModel(), KoinComponent {

    private val log = Logger.getLogger("TransfersModel")


    init {
        currentAccount.addOnAccountChanged {
            //TODO: Another way
            if (!currentAccount.loggedIn) deleteTransfers()
        }

        if (!currentAccount.loggedIn) deleteTransfers()
    }


    private fun deleteTransfers() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val transfers = realm.where(Transfer::class.java).findAll()
            transfers.deleteAllFromRealm()
            log.info("realm.where(Transfer).deleteAll()")
        }
        realm.close()
    }


    fun createTransfer(transfer: NewTransfer): Observable<Transfer> =
            Observable.create<Transfer> { observable ->
                log.info("createTransfer()")
                val response = api.postTransfer(NewTransferField(transfer)).execute()
                if (response.isSuccessful) {
                    val body = response.body()!!
                    if (body.success) {
                        log.info("createTransfer() responded success, id = ${body.data?.id}")
                        val t = Transfer()
                        t.id = body.data?.id ?: -1
                        observable.onNext(t)
                        observable.onComplete()
                    } else {
                        if (body.error?.type == "unprocessable") {
                            log.info("createTransfer() responded unprocessable")
                            observable.onError(Exception("The request is incomplete or unprocessable."))
                        } else {
                            log.info("createTransfer() responded fail, result = ${body.result}")
                            observable.onError(Exception(body.error?.message))
                        }
                    }
                } else {
                    log.info("createTransfer() fail, ${response.message()}")
                    observable.onError(Exception(response.message()))
                }
            }


    fun getAllAsync(): RealmResults<Transfer> =
            realm.where(Transfer::class.java).sort("dateTo").findAllAsync()


    fun getAllAsync(active: Boolean): RealmResults<Transfer> =
            realm.where(Transfer::class.java).equalTo("isActive", active).sort("dateTo").findAllAsync()


    fun updateTransfers() {
        log.info("updateTransfers()")
        if (busy) return
        log.info("getTransfers() call")
        api.getTransfers().fastSubscribe { data ->
            log.info("getTransfers() responded success, N = ${data?.transfers?.size}")
            val transfers = data?.transfers ?: return@fastSubscribe

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val transfers = realm.copyToRealmOrUpdate(transfers)
                transfers.forEach { it.updateIsActive() }
                val ids = transfers
                        .map { it.id }
                        .toIntArray().toTypedArray()
                val transfersToDelete = realm
                        .where(Transfer::class.java)
                        .not()
                        .`in`("id", ids)
                        .findAll()
                transfersToDelete.deleteAllFromRealm()
                log.info("getTransfers() saved to realm")
            }
            realm.close()
        }
    }


    override fun stop() {
        super.stop()
        realm.close()
    }
}
