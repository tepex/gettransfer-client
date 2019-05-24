package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.LoggingRepository

class LogsInteractor(private val loggingRepository: LoggingRepository) {

    val logsFile  by lazy { loggingRepository.file }

    fun onLogRequested() = loggingRepository.logs
    val logs: String
        get() = loggingRepository.logs

    fun clearLogs() = loggingRepository.clearLogs()
}