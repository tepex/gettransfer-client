package com.kg.gettransfer.fragment


import android.content.Intent
import android.graphics.LightingColorFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.activity.login.LoginActivity
import com.kg.gettransfer.modules.ConfigModel
import com.kg.gettransfer.modules.CurrentAccount
import com.kg.gettransfer.modules.ProfileModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 14/03/2018.
 */


class AccountFragment : Fragment(), KoinComponent {
    private val configModel: ConfigModel by inject()
    private val currentAccount: CurrentAccount by inject()
    private val profileModel: ProfileModel by inject()

    private val disposables = CompositeDisposable()

    private var savedView: View? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?)
            : View? {
        var v = savedView

        if (v == null) {
            v = inflater.inflate(
                    R.layout.fragment_account,
                    container,
                    false)!!

            with(v) {
                btnLogIn.setOnClickListener { logIn() }
                btnLogOut.setOnClickListener { currentAccount.logOut() }
                btnRegister.setOnClickListener {
                    val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://gettransfer.com/en/passenger/new"))
                    startActivity(browserIntent)
                }
                btnLogIn.background.colorFilter =
                        LightingColorFilter(
                                ContextCompat.getColor(context, R.color.colorYellow), 0)

                val phoneEditTextWatcher: TextWatcher = object : TextWatcher {
                    private val DELAY: Long = 500

                    val handler = Handler(Looper.getMainLooper()) // UI thread
                    var workRunnable: Runnable? = null

                    override fun afterTextChanged(s: Editable?) {
                        handler.removeCallbacks(workRunnable)
                        if (s.isNullOrEmpty()) {
                        } else if (s.toString() != currentAccount.accountInfo.phone) {
                            workRunnable = Runnable { currentAccount.putAccount(phone = s.toString()) }
                            handler.postDelayed(workRunnable, DELAY)
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                }

                etPhone.addTextChangedListener(phoneEditTextWatcher)
            }

            savedView = v
        }

        configModel

        return v
    }


    override fun onResume() {
        super.onResume()

        disposables.add(currentAccount.addOnAccountChanged { updateUI() })
        disposables.add(currentAccount.addOnBusyProgressBar(progressBarAccount))

        disposables.add(profileModel.addOnProfileUpdated { updateUI() })
        disposables.add(profileModel.addOnBusyProgressBar(progressBarProfile))

        updateUI()
    }


    private fun updateUI() {
        if (currentAccount.loggedIn) {
            showAccount()
        } else {
            showHello()
        }
    }


    private fun showAccount() {
        clLoggedOut.visibility = GONE
        clAccount.visibility = VISIBLE

        tvEmail.text = currentAccount.accountInfo.email
        if (etPhone.text.toString() != currentAccount.accountInfo.phone) {
            etPhone.setText(currentAccount.accountInfo.phone)
        }

        val profile = profileModel.profile
        if (profile.valid) {
            clProfileInfo.visibility = VISIBLE
            tvName.text = profile.fullName
            cbEmailNotifications.isChecked = profile.emailNotifications ?: false
        } else {
            clProfileInfo.visibility = GONE
        }
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

