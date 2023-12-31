package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.SupportPresenter
import com.kg.gettransfer.presentation.ui.custom.ContactsView
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SupportView

import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.layout_phones.*
import kotlinx.android.synthetic.main.layout_social_network.*
import kotlinx.android.synthetic.main.layout_write_us.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_contacts.view.*

class SupportFragment : BaseFragment(), SupportView {

    @InjectPresenter
    internal lateinit var presenter: SupportPresenter

    @ProvidePresenter
    fun createSupportPresenter() = SupportPresenter()

    private var transferId: Long? = null
    private var showBackArrow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            transferId = getLong(TRANSFER_ID, -1L)
            showBackArrow = getBoolean(SHOW_BACK_ARROW)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_support, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        setupToolbar(showBackArrow)
    }

    private fun setupToolbar(showBackArrow: Boolean) {
        toolbar_title.text = getString(R.string.LNG_CUSTOMER_SUPPORT)
        toolbar_btnBack.isInvisible = !showBackArrow
        if (showBackArrow) toolbar_btnBack.setOnClickListener { presenter.onBackCommandClick() }
    }

    private fun initClickListeners() {
        aboutUs.setOnClickListener { presenter.onAboutUsClick() }
        fabFacebook.setOnClickListener { facebookClick() }
        fabViber.setOnClickListener { viberClick() }
        fabTelegram.setOnClickListener { telegramClick() }
        fabEmail.setOnClickListener { presenter.sendEmail(transferId) }
        cyPhone.setOnClickListener { callPhone(cyPhone) }
        gbPhone.setOnClickListener { callPhone(gbPhone) }
        hkPhone.setOnClickListener { callPhone(hkPhone) }
        ruPhone.setOnClickListener { callPhone(ruPhone) }
        swPhone.setOnClickListener { callPhone(swPhone) }
        usPhone1.setOnClickListener { callPhone(usPhone1) }
    }

    private fun callPhone(tv: ContactsView) = presenter.callPhone(tv.tvPhone.text.toString())

    private fun facebookClick() = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL)))

    private fun viberClick() {
        val intent = try {
            requireActivity().packageManager.getPackageInfo(VIBER_PACKAGE, 0)
            Intent(Intent.ACTION_VIEW, Uri.parse(VIBER_URI)).setPackage(VIBER_PACKAGE)
        } catch (e: PackageManager.NameNotFoundException) {
            Intent(Intent.ACTION_VIEW, Uri.parse(VIBER_URL))
        }
        startActivity(intent)
    }

    private fun telegramClick() = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_URL)))

    override fun showEmail(email: String) {
        tvEmail.text = email
    }

    override fun showAboutUs() {
        // TODO fix it after support single activity in whole app
        if (activity is MainNavigateActivity) {
            findNavController().navigate(SupportFragmentDirections.goToAbout())
        } else {
            parentFragmentManager.let { Screens.showAboutScreen(it) }
        }
    }

    companion object {
        private const val VIBER_PACKAGE = "com.viber.voip"
        private const val FACEBOOK_URL = "http://m.me/548999978590475"
        private const val VIBER_URL = "https://viber.me/GetTransferSupport"
        private const val VIBER_URI = "viber://pa?chatURI=GetTransferSupport"
        private const val TELEGRAM_URL = "https://t.me/gettransfersupportbot"

        private const val TRANSFER_ID = "transferId"
        private const val SHOW_BACK_ARROW = "showBackArrow"

        const val TAG = "Support"

        fun newInstance(transferId: Long?, showBackArrow: Boolean = true): Fragment {
            return SupportFragment().apply {
                arguments = Bundle().apply {
                    transferId?.let { putLong(TRANSFER_ID, it) }
                    putBoolean(SHOW_BACK_ARROW, showBackArrow)
                }
            }
        }
    }
}
