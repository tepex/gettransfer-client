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
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*
import kotlinx.android.synthetic.main.view_contacts.view.*

class SupportActivity : BaseActivity(), SupportView {

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
//        fabWhatsapp.setOnClickListener { whatsAppClick() }
        fabViber.setOnClickListener { viberClick() }
        fabTelegram.setOnClickListener { telegramClick() }
    }

    private fun facebookClick() {
        var intent: Intent
        try {
            packageManager.getPackageInfo("com.facebook.katana", 0)
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/gettransfer"))
        } catch (e: PackageManager.NameNotFoundException) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/gettransfer"))
        }
        startActivity(intent)
    }

    private fun whatsAppClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(""))
        startActivity(intent)
    }

    private fun viberClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://viber.me/GetTransferSupport"))
        startActivity(intent)
    }

    private fun telegramClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/gettransfersupportbot"))
        startActivity(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
        toolbar.toolbar_title.text = getString(R.string.LNG_CUSTOMER_SUPPORT)
    }
}