package com.kg.gettransfer.fragment


import android.app.Fragment
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.activity.login.LoginActivity
import com.kg.gettransfer.mainactivity.MainActivity
import com.kg.gettransfer.modules.CurrentAccount
import com.kg.gettransfer.modules.TransfersModel
import com.kg.gettransfer.views.EmptyRecyclerView
import com.kg.gettransfer.views.TransfersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_transfers.*
import kotlinx.android.synthetic.main.fragment_transfers.view.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by ivanpchelintsev on 04/02/2018.
 */


class TransfersFragment : Fragment(), KoinComponent {
    private val currentAccount: CurrentAccount by inject()
    private val transfersModel: TransfersModel by inject()

    private val disposables = CompositeDisposable()

    private val adapterActive by lazy {
        TransfersAdapter(transfersModel.getAllAsync(true), true)
    }
    private val adapterArchive by lazy {
        TransfersAdapter(transfersModel.getAllAsync(false), true)
    }

    private var savedview: View? = null


    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?)
            : View {
        if (savedview == null) {
            val view = inflater?.inflate(
                    R.layout.fragment_transfers,
                    container,
                    false)!!

            with(view) {
                initRecyclerView(view.rvTransfers, view.swipeRefreshLayout)

                tvActive.setOnClickListener {
                    updateTabs(true)
                    rvTransfers.adapter = adapterActive
                }

                tvArchive.setOnClickListener {
                    updateTabs(false)
                    rvTransfers.adapter = adapterArchive
                }

                btnLogIn.setOnClickListener { logIn() }
                btnCreate.setOnClickListener { createTransfer() }
            }

            savedview = view
        }
        return savedview!!
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        disposables.add(
                currentAccount.loggedIn
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { updateUI() })

        disposables.add(transfersModel.addOnBusyChanged {
            swipeRefreshLayout.isRefreshing = it
        })

        disposables.add(transfersModel.addOnError {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        })

        updateUI()
    }


    private fun updateTabs(active: Boolean) {
        if (active) {
            tvActive.typeface = Typeface.DEFAULT_BOLD
            tvArchive.typeface = Typeface.DEFAULT
            tvActive.setTextColor(resources.getColor(R.color.colorTextAccent))
            tvArchive.setTextColor(resources.getColor(R.color.colorTextGray))
        } else {
            tvActive.typeface = Typeface.DEFAULT
            tvArchive.typeface = Typeface.DEFAULT_BOLD
            tvActive.setTextColor(resources.getColor(R.color.colorTextGray))
            tvArchive.setTextColor(resources.getColor(R.color.colorTextAccent))
        }
    }


    private fun initRecyclerView(rvTransfers: EmptyRecyclerView,
                                 swipeRefreshLayout: SwipeRefreshLayout) {
        rvTransfers.adapter = adapterActive
        rvTransfers.layoutManager = LinearLayoutManager(activity)
        rvTransfers.emptyView = clEmptyTransfers

        swipeRefreshLayout.setOnRefreshListener { transfersModel.updateTransfers() }
    }


    private fun updateUI() {
        if (currentAccount.isLoggedIn) {
            showTransfers()
        } else {
            showLoggedOut()
        }
    }


    private fun showTransfers() {
        swipeRefreshLayout.visibility = VISIBLE
        clLoggedOut.visibility = GONE
        tvActive.visibility = VISIBLE
        tvArchive.visibility = VISIBLE
        transfersModel.updateTransfers()
    }


    private fun showLoggedOut() {
        swipeRefreshLayout.visibility = GONE
        clLoggedOut.visibility = VISIBLE
        tvActive.visibility = INVISIBLE
        tvArchive.visibility = INVISIBLE
    }


    private fun createTransfer() {
        (activity as MainActivity).showCreateTransfer(null)
    }


    private fun logIn() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivityForResult(intent, 2)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }
}

