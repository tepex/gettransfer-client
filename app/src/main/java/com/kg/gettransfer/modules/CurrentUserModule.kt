package com.kg.gettransfer.modules


/**
 * Created by denisvakulenko on 05/02/2018.
 */


interface CurrentUserEvent

class NewUser : CurrentUserEvent
class NoUser : CurrentUserEvent
class Updated : CurrentUserEvent


object CurrentUserModule {
    var email: String? = null


}