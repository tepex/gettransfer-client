package com.kg.gettransfer.mainactivity


import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageButton
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.fragment.AccountFragment
import com.kg.gettransfer.fragment.CreateTransferFragment
import com.kg.gettransfer.fragment.TransfersFragment
import com.kg.gettransfer.modules.CurrentAccount
import org.koin.android.ext.android.inject


/**
 * Created by denisvakulenko on 06/02/2018.
 */


class MainActivity : AppCompatActivity() {
    private val currentAccount: CurrentAccount by inject()

    private val TAG: String = "Screen"

    private var tab = 1
    private var prevTab = 1

    private var frTransfers: TransfersFragment? = null
        get() {
            if (field == null) field = TransfersFragment()
            return field
        }

    private var frCreateTransfer: CreateTransferFragment? = null
        get() {
            if (field == null) field = CreateTransferFragment()
            return field
        }

    private var frAccount: AccountFragment? = null
        get() {
            if (field == null) field = AccountFragment()
            return field
        }

    private val clNavbar by lazy { findViewById<ConstraintLayout>(R.id.clNavbar) }

    private val btnTransfers by lazy { clNavbar.findViewById<AppCompatImageButton>(R.id.btnTransfersFragment) }
    private val btnCreateTransfer by lazy { clNavbar.findViewById<AppCompatImageButton>(R.id.btnCreateTransferFragment) }
    private val btnAccount by lazy { clNavbar.findViewById<AppCompatImageButton>(R.id.btnAccountFragment) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentManager.addOnBackStackChangedListener {
            clNavbar.visibility =
                    if (fragmentManager.backStackEntryCount < 1) View.VISIBLE
                    else View.GONE
        }

        if (savedInstanceState != null) {
            val findFragmentByTag = fragmentManager.findFragmentByTag(TAG)
            when (findFragmentByTag) {
                is TransfersFragment -> {
                    frTransfers = findFragmentByTag
                    tab = 0
                }
                is CreateTransferFragment -> {
                    frCreateTransfer = findFragmentByTag
                    tab = 1
                }
                is AccountFragment -> {
                    frAccount = findFragmentByTag
                    tab = 2
                }
            }
            updateButtons(tab)
        } else {
            val ft = fragmentManager.beginTransaction()
            ft.add(R.id.flFragment, frCreateTransfer, TAG)
            ft.disallowAddToBackStack()
            ft.commit()
            tab = 1
        }
    }


    override fun onResume() {
        super.onResume()

//        if (currentAccount.loggedIn) {
//            btnAccount.setImageResource(R.drawable.ic_person_black_24dp)
//        } else {
//            btnAccount.setImageResource(R.drawable.ic_person_outline_gray_24dp)
//        }
    }


    fun showTransfers(v: View?) = selectTab(0)

    fun showCreateTransfer(v: View?) = selectTab(1)

    fun showAccount(v: View?) = selectTab(2)


    private fun selectTab(i: Int) {
        if (tab == i) return
        prevTab = tab
        tab = i

        updateButtons(i)

        val ft = fragmentManager.beginTransaction()
        ft.replace(
                R.id.flFragment,
                when (i) {
                    0 -> frTransfers
                    1 -> frCreateTransfer
                    else -> frAccount
                },
                TAG)
        ft.disallowAddToBackStack()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ft.commitNow()
        } else {
            ft.commit()
            fragmentManager.executePendingTransactions()
        }
    }


    private fun updateButtons(i: Int) {
        btnTransfers.alpha = if (i == 0) 1f else 0.40f
        btnCreateTransfer.alpha = if (i == 1) 1f else 0.35f
        btnAccount.alpha = if (i == 2) 1f else 0.35f

        btnTransfers.setImageResource(
                if (i == 0) R.drawable.ic_view_headline_blue_24dp
                else R.drawable.ic_view_headline_black_24dp)
        btnCreateTransfer.setImageResource(
                if (i == 1) R.drawable.ic_add_box_blue_24dp
                else R.drawable.ic_add_box_black_24dp)
        btnAccount.setImageResource(
                if (i == 2) R.drawable.ic_person_blue_24dp
                else R.drawable.ic_person_black_24dp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnTransfers.elevation = if (i == 0) 4f else 0f
            btnCreateTransfer.elevation = if (i == 1) 4f else 0f
            btnAccount.elevation = if (i == 2) 4f else 0f
        }
    }


    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount == 0 && (tab != 1 || prevTab != 1)) {
            selectTab(prevTab)
            prevTab = 1
        } else super.onBackPressed()
    }
}