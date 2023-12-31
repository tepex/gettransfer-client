package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.core.content.res.ResourcesCompat
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible

import moxy.MvpAppCompatFragment
import com.google.android.material.tabs.TabLayout
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.LogInView

import kotlinx.android.synthetic.main.fragment_pager_authorization.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.serialization.json.JSON
// import leakcanary.AppWatcher

import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import ru.terrakok.cicerone.Router

/**
 * This fragment contain the pagers for login and registration
 *
 * @author П. Густокашин (Diwixis)
 */
@Suppress("UnsafeCast")
class AuthorizationPagerFragment : MvpAppCompatFragment(), KoinComponent {

    private val router: Router by inject()
    private lateinit var params: String
    private var nextScreen = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_pager_authorization, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginTitleTabs.setupWithViewPager(loginPager)
        val semiboldFontRes = context?.let { ResourcesCompat.getFont(it, R.font.sf_pro_text_semibold) }
        val regularFontRes = context?.let { ResourcesCompat.getFont(it, R.font.sf_pro_text_regular) }
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
        loginPager.adapter = LoginPagerAdapter(parentFragmentManager)

        setToolbar()

        arguments?.let { args ->
            params = args.getString(LogInView.EXTRA_PARAMS) ?: ""
            JSON.parse(LogInView.Params.serializer(), params).let { nextScreen = it.nextScreen }
        }
    }

    private fun setToolbar() {
        toolbar_title.text = getString(R.string.LNG_LOGIN_LOGIN_TITLE)
        toolbar_btnBack.isVisible = true
        toolbar_btnBack.setOnClickListener { router.exit() }
    }

    @Suppress("NestedBlockDepth")
    fun showOtherPage(emailOrPhone: String? = null, isPhone: Boolean? = null) {
        loginPager.currentItem = if (loginPager.currentItem == 0) 1 else 0

        (loginPager.adapter as LoginPagerAdapter).getItem(loginPager.currentItem).let { fragment ->
            if (fragment is SignUpFragment) emailOrPhone?.let { emailOrPhone ->
                isPhone?.let { isPhone ->
                    fragment.presenter.updateEmailOrPhone(emailOrPhone, isPhone)
                }
            }
        }
    }

    companion object {
        const val COUNT_PAGE_FRAGMENTS = 2
        fun newInstance() = AuthorizationPagerFragment()
    }

    private inner class LoginPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        // TODO for translate emailOrPhone to otherFragment
        val fragments = SparseArray<Fragment>()

        override fun getCount(): Int = COUNT_PAGE_FRAGMENTS

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> getString(R.string.LNG_MENU_TITLE_LOGIN)
            1 -> getString(R.string.LNG_SIGNUP)
            else -> throw UnsupportedOperationException()
        }

        override fun getItem(position: Int): Fragment =
            getFragment(position) {
                when (position) {
                    0 -> createLoginFragment()
                    1 -> createSignUpFragment()
                    else -> throw UnsupportedOperationException()
                }
            }

        // TODO for translate emailOrPhone to otherFragment
        private fun getFragment(position: Int, createFragment: (() -> Fragment)): Fragment {
            if (fragments[position] == null) {
                fragments.put(position, createFragment())
            }
            return fragments[position]
        }

        // TODO for translate emailOrPhone to otherFragment
        private fun createLoginFragment(): Fragment {
            return LogInFragment.newInstance().apply {
                changePage = { emailOrPhone, isPhone -> showOtherPage(emailOrPhone, isPhone) }
                arguments = Bundle().apply {
                    putString(LogInView.EXTRA_PARAMS, params)
                }
            }
        }

        // TODO for translate emailOrPhone to otherFragment
        private fun createSignUpFragment(): Fragment {
            return SignUpFragment.newInstance().apply {
                changePage = { emailOrPhone, isPhone -> showOtherPage(emailOrPhone, isPhone) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }
}
