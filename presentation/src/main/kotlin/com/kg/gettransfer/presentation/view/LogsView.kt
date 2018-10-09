package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import java.io.File

@StateStrategyType(OneExecutionStateStrategy::class)
interface LogsView: BaseView {
    fun setLogs(logs: String)
    fun share(logsFile: File)
}