package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens
import kotlinx.android.synthetic.main.fragment_pager_authorization.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import ru.terrakok.cicerone.Router

/**
 * This fragment contain the pagers for login and registration
 *
 * @author П. Густокашин (Diwixis)
 */
class AuthorizationPagerFragment : MvpAppCompatFragment(), KoinComponent {

    private val router by inject<Router>()
    private var nextScreen = ""
    private var emailOrPhone = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_pager_authorization, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginTitleTabs.setupWithViewPager(loginPager)
        loginPager.adapter = LoginPagerAdapter(fragmentManager!!)

        loginBackButton.setOnClickListener { router.exit() }

        nextScreen = arguments?.getString(LogInView.EXTRA_NEXT_SCREEN) ?: ""
        emailOrPhone = arguments?.getString(LogInView.EXTRA_EMAIL_TO_LOGIN) ?: ""
    }

    companion object {
        fun newInstance() = AuthorizationPagerFragment()
    }

    private inner class LoginPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> getString(R.string.LNG_MENU_TITLE_LOGIN)
            1 -> getString(R.string.LNG_SIGNUP)
            else -> throw UnsupportedOperationException()
        }

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> LogInFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putString(LogInView.EXTRA_NEXT_SCREEN, nextScreen)
                    putString(LogInView.EXTRA_EMAIL_TO_LOGIN, emailOrPhone)
                }
            }
            1 -> {
                if (nextScreen == Screens.CARRIER_MODE) SignUpCarrierFragment.newInstance()
                else SignUpFragment.newInstance()
            }
            else -> throw UnsupportedOperationException()
        }
    }
}