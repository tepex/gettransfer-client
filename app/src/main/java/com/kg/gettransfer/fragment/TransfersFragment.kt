package com.kg.gettransfer.fragment


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.activity.login.LoginActivity
import com.kg.gettransfer.mainactivity.MainActivity
import com.kg.gettransfer.module.CurrentAccount
import com.kg.gettransfer.module.TransfersModel
import com.kg.gettransfer.view.base.DividerItemDecoration
import com.kg.gettransfer.view.base.EmptyRecyclerView
import com.kg.gettransfer.adapter.TransfersAdapter
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
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?)
            : View? {
        if (savedview == null) {
            val view = inflater.inflate(
                    R.layout.fragment_transfers,
                    container,
                    false)!!

            with(view) {
                initRecyclerView(view.rvTransfers, view.srlTransfer)

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


    override fun onResume() {
        super.onResume()

        val activity = activity ?: return

        disposables.add(currentAccount.addOnAccountChanged { updateUI() })

        disposables.add(transfersModel.addOnBusyChanged {
            srlTransfer.isRefreshing = it
        })

        disposables.add(transfersModel.toastOnError(
                activity, getString(R.string.unable_to_update_transfers)))

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

        class WrapContentLinearLayoutManager : LinearLayoutManager(activity) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    Log.e("TransfersFragment", "IndexOutOfBoundsException in RecyclerView happens")
                }
            }
        }

        val dividerItemDecoration = DividerItemDecoration(
                activity, VERTICAL,
                0, //(resources.displayMetrics.density * 16).toInt(),
                0)

        rvTransfers.addItemDecoration(dividerItemDecoration)
        rvTransfers.adapter = adapterActive
        rvTransfers.layoutManager = WrapContentLinearLayoutManager()
        rvTransfers.emptyView = clEmptyTransfers

        swipeRefreshLayout.setOnRefreshListener { transfersModel.updateTransfers() }
    }


    private fun updateUI() {
        if (currentAccount.loggedIn) {
            showTransfers()
        } else {
            showLoggedOut()
        }
    }


    private fun showTransfers() {
        srlTransfer.visibility = VISIBLE
        clLoggedOut.visibility = GONE
        tvActive.visibility = VISIBLE
        tvArchive.visibility = VISIBLE
        transfersModel.updateTransfers()
    }


    private fun showLoggedOut() {
        srlTransfer.visibility = GONE
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

