package com.kg.gettransfer.module


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.module.http.HttpApi
import com.kg.gettransfer.module.http.json.AccountField
import com.kg.gettransfer.realm.AccountInfo
import com.kg.gettransfer.realm.saveAndGetUnmanaged
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import org.koin.standalone.KoinComponent
import java.util.*


/**
 * Created by denisvakulenko on 05/02/2018.
 */


// Singleton
class CurrentAccount(
        private val session: Session,
        private val api: HttpApi)
    : AsyncModel(), KoinComponent {

    private val brAccountInfo =
            BehaviorRelay.createDefault<AccountInfo>(getAccount(session.email))!!

    var accountInfo: AccountInfo
        get() = brAccountInfo.value
        private set(v) = brAccountInfo.accept(v)

    val loggedIn: Boolean get() = accountInfo.email?.isNotEmpty() == true

    fun addOnAccountChanged(f: (AccountInfo) -> Unit): Disposable {
        val d = brAccountInfo.observeOn(AndroidSchedulers.mainThread()).subscribe(f)
        disposables.add(d)
        return d
    }

    fun getAccount(email: String): AccountInfo {
        val realm = Realm.getDefaultInstance()

        val newAccountInfo = realm
                .where(AccountInfo::class.java)
                .equalTo("email", email)
                .findFirst()

        val a = if (newAccountInfo == null) AccountInfo(email)
        else realm.copyFromRealm(newAccountInfo)

        realm.close()

        return a
    }


    // --


    init {
        session.state.subscribe {
            val newEmail = session.email
            if (newEmail != accountInfo.email) {
                accountInfo = getAccount(newEmail)
            }
        }
    }


    var loggingInDisposable: Disposable? = null
    fun login(email: String, pass: String): Disposable? {
        loggingInDisposable = api.login(email, pass).fastSubscribe {
            val newAccountInfo = it.account
            newAccountInfo.dateUpdated = Date()

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                it.copyToRealmOrUpdate(newAccountInfo)
            }

            if (newAccountInfo.email == email) session.loggedIn(email)

            realm.close()
        }
        return loggingInDisposable
    }


    fun putAccount(
            currency: String? = null,
            distanceUnit: String? = null,
            phone: String? = null): Boolean {

        val accountInfoCopy = accountInfo
        if (currency != null) accountInfoCopy.currency = currency
        if (distanceUnit != null) accountInfoCopy.distanceUnit = distanceUnit
        if (phone != null) accountInfoCopy.phone = phone
        accountInfo = accountInfoCopy.saveAndGetUnmanaged()

        if (putAccount() == null) {
            var d: Disposable? = null
            d = addOnBusyChanged {
                if (putAccount() != null) {
                    d?.dispose()
                }
            }
        }

        return true
    }


    private fun putAccount(): Disposable? {
        return api.putAccount(AccountField(accountInfo)).fastSubscribe {
            val newAccountInfo = it.account
            newAccountInfo.dateUpdated = Date()
            accountInfo = newAccountInfo.saveAndGetUnmanaged()
        }
    }


    fun logOut() {
        if (loggingInDisposable?.isDisposed == false) {
            loggingInDisposable?.dispose()
            busy = false
        }
        session.logOut()
    }
}


