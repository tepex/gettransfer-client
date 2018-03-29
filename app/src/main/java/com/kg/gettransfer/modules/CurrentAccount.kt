package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.AccountField
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


    fun login(email: String, pass: String): Boolean {
        if (busy) return false
        api.login(email, pass).fastSubscribe {
            val newAccountInfo = it.account
            newAccountInfo.dateUpdated = Date()

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                it.copyToRealmOrUpdate(newAccountInfo)
            }

            if (newAccountInfo.email == email) session.loggedIn(email)

            realm.close()
        }
        return true
    }


    fun putAccount(currency: String? = null, distanceUnit: String? = null): Boolean {
        if (busy) return false
        if (currency != null) accountInfo.currency = currency
        if (distanceUnit != null) accountInfo.distanceUnit = distanceUnit
        api.putAccount(AccountField(accountInfo)).fastSubscribe {
            val newAccountInfo = it.account
            newAccountInfo.dateUpdated = Date()

            accountInfo = newAccountInfo.saveAndGetUnmanaged()
        }
        return true
    }


    fun logOut() {
        session.logOut()
    }
}


