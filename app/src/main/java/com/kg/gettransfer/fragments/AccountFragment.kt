package com.kg.gettransfer.fragments


import android.app.Fragment
import android.content.Intent
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.modules.CurrentAccount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 14/03/2018.
 */


class AccountFragment : Fragment(), KoinComponent {
    private val currentAccount: CurrentAccount by inject()

    private val disposables = CompositeDisposable()

    private var savedView: View? = null


    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?)
            : View {
        var v = savedView

        if (v == null) {
            v = inflater?.inflate(
                    R.layout.fragment_account,
                    container,
                    false)!!

            with(v) {
                btnLogIn.setOnClickListener { logIn() }
                btnLogOut.setOnClickListener { currentAccount.logOut() }
//              btnRegister.setOnClickListener { createTransfer() }
                btnLogIn.background.colorFilter =
                        LightingColorFilter(
                                ContextCompat.getColor(activity, R.color.colorYellow), 0)
            }

            savedView = v
        }

        return v
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        disposables.add(
                currentAccount.loggedIn
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { updateUI() })

        updateUI()
    }


    private fun updateUI() {
        if (currentAccount.isLoggedIn) {
            showAccount()
        } else {
            showHello()
        }
    }


    private fun showAccount() {
        clLoggedOut.visibility = GONE
        clAccount.visibility = VISIBLE

        tvEmail.text = currentAccount.email
    }


    private fun showHello() {
        clLoggedOut.visibility = VISIBLE
        clAccount.visibility = GONE
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

