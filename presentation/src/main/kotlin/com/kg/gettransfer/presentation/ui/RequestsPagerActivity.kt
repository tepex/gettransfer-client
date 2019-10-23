package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper
import moxy.MvpAppCompatActivity

import com.kg.gettransfer.R

class RequestsPagerActivity : MvpAppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_requests_pager)
    }
}
