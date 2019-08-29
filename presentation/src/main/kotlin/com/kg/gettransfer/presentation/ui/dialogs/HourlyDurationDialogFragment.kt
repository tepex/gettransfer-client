package com.kg.gettransfer.presentation.ui.dialogs

import android.os.Bundle
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.utilities.Analytics
import kotlinx.android.synthetic.main.view_hourly_picker.*
import org.koin.android.ext.android.inject
import android.view.KeyEvent

class HourlyDurationDialogFragment : BaseBottomSheetDialogFragment() {

    protected val analytics: Analytics by inject()
    override val layout: Int = R.layout.dialog_fragment_hourly_duration

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)

        dialog?.setOnKeyListener { dialog, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                listener?.onDone(selectedDuration())
                dialog?.dismiss()
            }
            return@setOnKeyListener true
        }
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tv_okBtn.setOnClickListener {
            listener?.onDone(selectedDuration())
            dismiss()
        }

        with(np_hours) {
            displayedValues = HourlyValuesHelper.getHourlyValues(requireContext()).toTypedArray()
            minValue = 0
            maxValue = HourlyValuesHelper.durationValues.size - 1
            wrapSelectorWheel = false
            value = HourlyValuesHelper.durationValues
                    .indexOf(arguments?.getInt(EXTRA_DURATION_VALUE) ?: 0)

            setOnValueChangedListener{ _, _, _ ->
                listener?.onDone(selectedDuration())
            }
        }
    }

    private fun selectedDuration() : Int {
        return HourlyValuesHelper.durationValues[np_hours.value]
    }

    companion object {
        const val DIALOG_TAG = "hourly_duration_dialog_tag"
        const val EXTRA_DURATION_VALUE = "duration_value"
        private var listener: OnHourlyDurationListener? = null
        fun newInstance(durationValue: Int?, listener: OnHourlyDurationListener? = null): HourlyDurationDialogFragment {
            this.listener = listener
            return HourlyDurationDialogFragment().apply {
                arguments = Bundle().apply {
                    durationValue?.let { putInt(EXTRA_DURATION_VALUE, it) }
                }
            }
        }
    }

    interface OnHourlyDurationListener {
        fun onDone(durationValue: Int)
    }
}