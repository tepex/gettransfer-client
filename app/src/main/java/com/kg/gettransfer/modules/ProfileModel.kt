package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.ProfileInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 19/03/2018.
 */


// Singleton
class ProfileModel(
        private val api: HttpApi,
        private val currentAccount: CurrentAccount)
    : AsyncModel(), KoinComponent {

    private val brProfile: BehaviorRelay<ProfileInfo> = BehaviorRelay.createDefault(ProfileInfo())


    init {
        currentAccount.addOnAccountChanged { reset() }
    }


    fun addOnProfileUpdated(f: ((ProfileInfo) -> Unit)): Disposable {
        val d = brProfile.observeOn(AndroidSchedulers.mainThread()).subscribe(f)
        disposables.add(d)
        return d
    }

    val profile: ProfileInfo
        get() {
            val value = brProfile.value
            if (!value.valid) update()
            return value
        }


    fun update() {
        if (busy) return

        val email = currentAccount.email

        api.getProfile().fastSubscribe {
            if (email == currentAccount.email) brProfile.accept(it)
        }
    }


    private fun reset() {
        brProfile.accept(ProfileInfo())
    }
}