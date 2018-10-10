package com.kg.gettransfer.domain.repository

import java.io.File

interface Logging {
    fun getLogs(): String
    fun clearLogs()
    fun getLogsFile(): File
}