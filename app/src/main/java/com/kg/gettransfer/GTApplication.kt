package com.kg.gettransfer


import android.content.pm.ApplicationInfo
import android.support.multidex.MultiDexApplication
import com.kg.gettransfer.module.TransfersModel
import org.koin.Koin
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger


/**
 * Created by denisvakulenko on 31/01/2018.
 */


class GTApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        // Display some logs
        val isDebug = (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
        if (isDebug) {
            Koin.logger = AndroidLogger()
        }

        // Start Koin
        startKoin(this, appModules)

        val t = inject<TransfersModel>().value
    }
}