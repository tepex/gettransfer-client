package com.kg.gettransfer.presentation.ui


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.dialog_read_more.btnClose

class ReadMoreFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.LNG_BESTPRICE_TITLE)
        builder.setView(layoutInflater.inflate(R.layout.dialog_read_more, null))

        btnClose.setOnClickListener { dialog.dismiss() }

        return builder.create()
    }

}
