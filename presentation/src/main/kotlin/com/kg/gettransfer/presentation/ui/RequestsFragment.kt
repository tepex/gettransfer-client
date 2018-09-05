package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.view.RequestsFragmentView
import kotlinx.android.synthetic.main.fragment_requests.*

class RequestsFragment: MvpAppCompatFragment(), RequestsFragmentView{
    var text = ""

    companion object {
        fun newInstance(transfers: ArrayList<Transfer>): RequestsFragment {
            val fragment = RequestsFragment()
            val bundle = Bundle()
            bundle.putArrayList
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //text = arguments?.get("Text").toString()
        rvRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //view.textView.setText(text)
    }

    override fun setRequests(transfers: List<Transfer>, distanceUnit: String) {
        rvRequests.adapter = RequestsRVAdapter(transfers, distanceUnit)
    }
}