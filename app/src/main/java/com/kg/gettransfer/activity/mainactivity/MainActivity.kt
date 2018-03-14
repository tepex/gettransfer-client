package com.kg.gettransfer.mainactivity


import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.fragment.CreateTransferFragment
import com.kg.gettransfer.fragment.AccountFragment
import com.kg.gettransfer.fragment.TransfersFragment
import com.kg.gettransfer.modules.CurrentAccount
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


/**
 * Created by denisvakulenko on 06/02/2018.
 */


class MainActivity : AppCompatActivity() {
    private val currentAccount: CurrentAccount by inject()

    private val frCreateTransfer by lazy { CreateTransferFragment() }
    private val frTransfers by lazy { TransfersFragment() }
    private val frAccount by lazy { AccountFragment() }

    private var tab = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentManager.addOnBackStackChangedListener {
            clNavbar.visibility =
                    if (fragmentManager.backStackEntryCount < 1) View.VISIBLE
                    else View.GONE
        }

        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.flFragment, frCreateTransfer)
        ft.disallowAddToBackStack()
        ft.commit()
    }


    override fun onResume() {
        super.onResume()

        if (currentAccount.isLoggedIn) {
            btnUser.setImageResource(R.drawable.ic_person_gray_24dp)
        } else {
            btnUser.setImageResource(R.drawable.ic_person_outline_gray_24dp)
        }
    }


    fun showTransfers(v: View?) {
        updateTab(0)
    }

    fun showCreateTransfer(v: View?) {
        updateTab(1)
    }

    fun showAccount(v: View?) {
        updateTab(2)
    }


    private fun updateTab(i: Int) {
        if (tab == i) return
        tab = i

        btnList.alpha = if (i == 0) 1f else 0.2f
        btnCreate.alpha = if (i == 1) 1f else 0.2f
        btnUser.alpha = if (i == 2) 1f else 0.2f

        val ft = fragmentManager.beginTransaction()
        ft.replace(
                R.id.flFragment,
                when (i) {
                    0 -> frTransfers
                    1 -> frCreateTransfer
                    else -> frAccount
                })
        ft.disallowAddToBackStack()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ft.commitNow()
        } else {
            ft.commit()
            fragmentManager.executePendingTransactions()
        }
    }
}