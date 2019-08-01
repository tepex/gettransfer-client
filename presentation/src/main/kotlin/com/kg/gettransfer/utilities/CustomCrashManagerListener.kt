package com.kg.gettransfer.utilities

import net.hockeyapp.android.CrashManagerListener

class CustomCrashManagerListener : CrashManagerListener() {
    override fun shouldAutoUploadCrashes() = true
}
