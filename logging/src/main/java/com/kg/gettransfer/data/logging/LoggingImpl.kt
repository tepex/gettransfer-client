package com.kg.gettransfer.data.logging

import android.content.Context
import com.kg.gettransfer.domain.repository.Logging
import java.io.File
import java.io.FileInputStream

class LoggingImpl(val context: Context, val logFileName: String): Logging {

    override fun getLogs(): String {
        val file = File(context.filesDir, logFileName)
        return FileInputStream(file).bufferedReader().use { it.readText() }
    }

    override fun clearLogs() {
        context.openFileOutput(logFileName, Context.MODE_PRIVATE).use {
            it.write("".toByteArray())
        }
    }

    override fun getLogsFile() = File(context.filesDir, logFileName)
}