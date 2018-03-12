package com.kg.gettransfer.mainactivity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.createtransfer.CreateTransferFragment
import com.kg.gettransfer.fragments.TransfersFragment
import com.kg.gettransfer.login.LoginActivity
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


    private val LOGIN_REQUEST = 1
    fun login(v: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, LOGIN_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                showCreateTransfer(null)
            }
        }
    }


    fun showTransfers(v: View?) {
        if (tab == 0) return
        tab = 0

        btnList.alpha = 1f
        btnCreate.alpha = 0.2f
        btnUser.alpha = 0.2f

        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.flFragment, frTransfers)
        ft.disallowAddToBackStack()
        ft.commit()
    }

    fun showCreateTransfer(v: View?) {
        if (tab == 1) return
        tab = 1

        btnList.alpha = 0.2f
        btnCreate.alpha = 1f
        btnUser.alpha = 0.2f

        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.flFragment, frCreateTransfer)
        ft.disallowAddToBackStack()
        ft.commit()
    }

    private fun showProfile(v: View?) {
        if (tab == 2) return
        tab = 2

        btnList.alpha = 0.2f
        btnCreate.alpha = 0.2f
        btnUser.alpha = 1f

        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.flFragment, frTransfers)
        ft.disallowAddToBackStack()
        ft.commit()
    }
}