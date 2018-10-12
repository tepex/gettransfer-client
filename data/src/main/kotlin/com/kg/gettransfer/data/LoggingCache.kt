package com.kg.gettransfer.data

import java.io.File

interface LoggingCache {
    fun getLogs(): String
    fun clearLogs()
    fun getLogsFile(): File
}
