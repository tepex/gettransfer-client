package com.kg.gettransfer.logging

import android.content.Context

import com.kg.gettransfer.domain.repository.LoggingRepository

import java.io.File
import java.io.FileInputStream

class LoggingRepositoryImpl(private val context: Context, private val logFileName: String): LoggingRepository {
    override val file = File(context.filesDir, logFileName)
    override val logs: String
        get() = FileInputStream(file).bufferedReader().use { it.readText() }

    override fun clearLogs() {
        context.openFileOutput(logFileName, Context.MODE_PRIVATE).use { it.write("".toByteArray()) }
    }
}
