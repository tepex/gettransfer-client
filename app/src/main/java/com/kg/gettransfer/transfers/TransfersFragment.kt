package com.kg.gettransfer.transfers


import android.app.Fragment
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.modules.CurrentAccount
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.views.EmptyRecyclerView
import com.kg.gettransfer.views.TransfersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_transfers.*
import kotlinx.android.synthetic.main.fragment_transfers.view.*
import org.koin.android.ext.android.inject


/**
 * Created by ivanpchelintsev on 04/02/2018.
 */


class TransfersFragment : Fragment() {
    private val currentAccount: CurrentAccount by inject()
    private val transfers: Transfers by inject()

    private val disposables = CompositeDisposable()

    private val adapterActive by lazy { transfers.getAllAsync(true) }
    private val adapterArchive by lazy { transfers.getAllAsync(false) }

    private var savedview: View? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (savedview == null) {
            val view = inflater?.inflate(R.layout.fragment_transfers, container, false)!!
            with(view) {
                initRecyclerView(view.rvTransfers, view.swipeRefreshLayout)

                disposables.add(
                        currentAccount.loggedIn
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { updateUI() })

                tvActive.setOnClickListener {
                    updateTabs(true)
                    rvTransfers.adapter = TransfersAdapter(adapterActive, true)
                }

                tvArchive.setOnClickListener {
                    updateTabs(false)
                    rvTransfers.adapter = TransfersAdapter(adapterArchive, true)
                }

                btnLogIn.setOnClickListener { logIn() }
            }

            savedview = view
        }
        return savedview!!
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        updateUI()
    }


    private fun updateTabs(active: Boolean) {
        if (active) {
            tvActive.typeface = Typeface.DEFAULT_BOLD
            tvArchive.typeface = Typeface.DEFAULT
            tvActive.setTextColor(resources.getColor(R.color.colorTextLocation))
            tvArchive.setTextColor(resources.getColor(R.color.colorTextGray))
        } else {
            tvActive.typeface = Typeface.DEFAULT
            tvArchive.typeface = Typeface.DEFAULT_BOLD
            tvActive.setTextColor(resources.getColor(R.color.colorTextGray))
            tvArchive.setTextColor(resources.getColor(R.color.colorTextLocation))
        }
    }


    private fun initRecyclerView(rvTransfers: EmptyRecyclerView,
                                 swipeRefreshLayout: SwipeRefreshLayout) {
        rvTransfers.adapter = TransfersAdapter(adapterActive, true)
        rvTransfers.layoutManager = LinearLayoutManager(activity)
        rvTransfers.emptyView = clEmptyTransfers

        swipeRefreshLayout.setOnRefreshListener { transfers.updateTransfers() }

        disposables.add(
                transfers.busy
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { swipeRefreshLayout.isRefreshing = it })
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
        transfers.updateTransfers()
    }


    private fun showLoggedOut() {
        swipeRefreshLayout.visibility = GONE
        clLoggedOut.visibility = VISIBLE
        tvActive.visibility = GONE
        tvArchive.visibility = GONE
    }


//    fun createTransfer(v: View?) {
//        val intent = Intent(activity, CreateTransferFragment::class.java)
//        startActivity(intent)
//    }


    private fun logIn() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivityForResult(intent, 2)
    }


    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}