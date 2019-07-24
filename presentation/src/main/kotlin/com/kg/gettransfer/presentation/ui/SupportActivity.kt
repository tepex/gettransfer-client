package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.SupportPresenter
import com.kg.gettransfer.presentation.view.SupportView
import kotlinx.android.synthetic.main.activity_support.*
import kotlinx.android.synthetic.main.layout_phones.*
import kotlinx.android.synthetic.main.layout_write_us.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*
import kotlinx.android.synthetic.main.view_contacts.view.*

class SupportActivity : BaseActivity(), SupportView {

    companion object {
        private const val VIBER_PACKAGE = "com.viber.voip"
        private const val FACEBOOK_URL = "http://m.me/548999978590475"
        private const val VIBER_URL = "https://viber.me/GetTransferSupport"
        private const val VIBER_URI = "viber://pa?chatURI=GetTransferSupport"
        private const val TELEGRAM_URL = "https://t.me/gettransfersupportbot"
    }

    @InjectPresenter
    internal lateinit var presenter: SupportPresenter

    @ProvidePresenter
    fun createSettingsPresenter() = SupportPresenter()

    override fun getPresenter(): SupportPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        setupToolbar()
        fabFacebook.setOnClickListener { facebookClick() }
        fabViber.setOnClickListener { viberClick() }
        fabTelegram.setOnClickListener { telegramClick() }
        fabEmail.setOnClickListener { presenter.sendEmail(null, null) }
        cyPhone.setOnClickListener { presenter.callPhone(cyPhone.tvPhone.text.toString()) }
        gbPhone.setOnClickListener { presenter.callPhone(gbPhone.tvPhone.text.toString()) }
        hkPhone.setOnClickListener { presenter.callPhone(hkPhone.tvPhone.text.toString()) }
        ruPhone.setOnClickListener { presenter.callPhone(ruPhone.tvPhone.text.toString()) }
        swPhone.setOnClickListener { presenter.callPhone(swPhone.tvPhone.text.toString()) }
        usPhone1.setOnClickListener { presenter.callPhone(usPhone1.tvPhone.text.toString()) }
    }

    private fun facebookClick() =
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL)))

    private fun viberClick() {
        val intent: Intent = try {
            packageManager.getPackageInfo(VIBER_PACKAGE, 0)
            Intent(Intent.ACTION_VIEW, Uri.parse(VIBER_URI))
                    .apply { setPackage(VIBER_PACKAGE) }
        } catch (e: PackageManager.NameNotFoundException) {
            Intent(Intent.ACTION_VIEW, Uri.parse(VIBER_URL))
        }
        startActivity(intent)
    }

    private fun telegramClick() =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_URL)))

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
        toolbar.toolbar_title.text = getString(R.string.LNG_CUSTOMER_SUPPORT)
    }

    override fun showEmail(email: String) {
        tvEmail.text = email
    }
}