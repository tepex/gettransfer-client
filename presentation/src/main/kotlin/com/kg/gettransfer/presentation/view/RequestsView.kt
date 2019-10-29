package com.kg.gettransfer.presentation.view

import androidx.annotation.IntDef
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE

@StateStrategyType(OneExecutionStateStrategy::class)
interface RequestsView : BaseView {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(TRANSFER_ACTIVE, TRANSFER_ARCHIVE)
    annotation class TransferTypeAnnotation{
        companion object {
            const val TRANSFER_ACTIVE    = 0
            const val TRANSFER_ARCHIVE   = 1
        }
    }
}
