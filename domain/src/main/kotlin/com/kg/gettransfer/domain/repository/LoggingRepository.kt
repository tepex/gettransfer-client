package com.kg.gettransfer.domain.repository

import java.io.File

interface LoggingRepository {
    val file: File
    val logs: String

    fun clearLogs()
}
