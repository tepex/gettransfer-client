package com.kg.gettransfer.modules

import com.kg.gettransfer.RxBus
import com.kg.gettransfer.modules.session.SessionEvent
import com.kg.gettransfer.modules.session.SessionModule
import com.kg.gettransfer.modules.session.SessionState


/**
 * Created by denisvakulenko on 05/02/2018.
 */


object CurrentUserModule {
    interface CurrentUserEvent

    interface UserChanged : CurrentUserEvent

    class UserLoggedIn : UserChanged
    class UserLoggedOut : UserChanged

    class UserInfoUpdated : CurrentUserEvent


    // --


    var email: String? = null
        private set

    var phone: String? = null
    var sign: String? = null


    // --


    init {
        SessionModule.state.subscribe{
            if (it == SessionState.LOGGED_IN) {
                val newEmail = SessionModule.sessionModel?.email
                if (newEmail != email) {
                    email = newEmail
                    RxBus.publish(UserLoggedIn())
                }
            } else if (it == SessionState.LOGGED_OUT) {
                RxBus.publish(UserLoggedOut())
            }
        }
    }
}