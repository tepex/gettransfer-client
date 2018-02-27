package com.kg.gettransfer


import android.app.Application
import android.content.pm.ApplicationInfo
import com.kg.gettransfer.modules.Transfers
import org.koin.Koin
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger


/**
 * Created by denisvakulenko on 31/01/2018.
 */


class GTApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Display some logs
        val isDebug = (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
        if (isDebug) {
            Koin.logger = AndroidLogger()
        }

        // Start Koin
        startKoin(this, todoAppModules)

        val t = inject<Transfers>().value
    }
}