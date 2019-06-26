package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.res.ResourcesCompat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatFragment

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.android.synthetic.main.fragment_pager_authorization.*
import kotlinx.serialization.json.JSON

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
    private lateinit var params: String
    private var nextScreen = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_pager_authorization, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginTitleTabs.setupWithViewPager(loginPager)
        val semiboldFontRes = ResourcesCompat.getFont(context!!, R.font.sf_pro_text_semibold)
        val regularFontRes = ResourcesCompat.getFont(context!!, R.font.sf_pro_text_regular)
        loginTitleTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {
                (((loginTitleTabs.getChildAt(0) as ViewGroup)
                    .getChildAt(tab.position) as LinearLayout)
                    .getChildAt(1) as TextView)
                    .typeface = regularFontRes
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                (((loginTitleTabs.getChildAt(0) as ViewGroup)
                    .getChildAt(tab.position) as LinearLayout)
                    .getChildAt(1) as TextView)
                    .typeface = semiboldFontRes
            }
        })
        loginPager.adapter = LoginPagerAdapter(fragmentManager!!)

        loginBackButton.setOnClickListener { router.exit() }

        arguments?.let { args ->
            params = args.getString(LogInView.EXTRA_PARAMS) ?: ""
            JSON.parse(LogInView.Params.serializer(), params).let { nextScreen = it.nextScreen }
        }
    }

    fun showOtherPage() {
        loginPager.currentItem = if (loginPager.currentItem == 0) 1 else 0
    }

    companion object {
        fun newInstance() = AuthorizationPagerFragment()
    }

    /* TODO: Magic numbers! */
    private inner class LoginPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> getString(R.string.LNG_MENU_TITLE_LOGIN)
            1 -> getString(R.string.LNG_SIGNUP)
            else -> throw UnsupportedOperationException()
        }

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> LogInFragment.newInstance().apply {
                changePage = { showOtherPage() }
                arguments = Bundle().apply {
                    putString(LogInView.EXTRA_PARAMS, params)
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
