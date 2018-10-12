package com.kg.gettransfer.domain.repository

import java.io.File

interface LoggingRepository {
    fun getLogs(): String
    fun clearLogs()
    fun getLogsFile(): File
}
