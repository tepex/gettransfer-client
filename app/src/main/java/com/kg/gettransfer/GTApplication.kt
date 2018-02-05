package com.kg.gettransfer

import android.app.Application
import com.kg.gettransfer.modules.CurrentUserModule


/**
 * Created by denisvakulenko on 31/01/2018.
 */


class GTApplication : Application() {
    init {
        RxBus
        CurrentUserModule
    }
}