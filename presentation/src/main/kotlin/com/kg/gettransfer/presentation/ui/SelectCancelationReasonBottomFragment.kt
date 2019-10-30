package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.adapter.CancelationReasonsListAdapter
import com.kg.gettransfer.presentation.listeners.CancelationReasonListener
import com.kg.gettransfer.presentation.presenter.SelectCancelationReasonPresenter
import com.kg.gettransfer.presentation.view.SelectCancelationReasonView
import kotlinx.android.synthetic.main.layout_select_cancelation_reason.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SelectCancelationReasonBottomFragment : BaseBottomSheetFragment(), SelectCancelationReasonView {

    override val layout = R.layout.fragment_select_cancelation_reason_bottom

    @InjectPresenter
    internal lateinit var presenter: SelectCancelationReasonPresenter

    @ProvidePresenter
    fun createSelectCancelationReasonPresenter() = SelectCancelationReasonPresenter()

    private var listener: CancelationReasonListener? = null

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CancelationReasonListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { hideBottomSheet() }
    }

    override fun setCancelationReasonsList(reasons: List<Int>) {
        rvCancelationReasons.adapter = CancelationReasonsListAdapter(reasons) { onReasonSelected(it) }
    }

    private fun onReasonSelected(reason: String) {
        hideBottomSheet()
        listener?.onCancelationReasonSelected(reason)
    }

    @CallSuper
    override fun onDestroyView() {
        listener = null
        rvCancelationReasons.adapter = null
        super.onDestroyView()
    }
}