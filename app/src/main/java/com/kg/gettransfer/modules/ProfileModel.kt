package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.ProfileInfo
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

    private val brProfile: BehaviorRelay<ProfileInfo> =
            BehaviorRelay.createDefault(ProfileInfo(0))

    var d: Disposable? = null


    init {
        currentAccount.addOnAccountChanged { reset() }
    }

    val profile: ProfileInfo
        get() {
            val value = brProfile.value
            if (!value.valid && value.updated + 5 * 1000 < System.currentTimeMillis()) {
                update()
            }
            return value
        }


    fun addOnProfileUpdated(f: (ProfileInfo) -> Unit): Disposable = brProfile.subscribeUIThread(f)


    fun update() {
        d?.dispose()
        val email = currentAccount.accountInfo
        d = api.getProfile().fastSubscribe {
            if (email == currentAccount.accountInfo) brProfile.accept(it.currentProfile)
        }
    }


    private fun reset() {
        brProfile.accept(ProfileInfo(0))
    }
}