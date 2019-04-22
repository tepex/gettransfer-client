package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.ViewGroup

import com.kg.gettransfer.R

class CreateOrderNewFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_create_order_new, container, false)
}
