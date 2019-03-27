package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.Toolbar
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Region
import com.kg.gettransfer.extensions.isGone
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.presenter.SupportPresenter
import com.kg.gettransfer.presentation.view.SupportView
import kotlinx.android.synthetic.main.activity_support.*
import kotlinx.android.synthetic.main.bottom_sheet_support.*
import kotlinx.android.synthetic.main.layout_select_region.*
import kotlinx.android.synthetic.main.layout_write_us.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*
import kotlinx.android.synthetic.main.view_region.view.*

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
    private lateinit var sheetRegion: BottomSheetBehavior<View>

    @ProvidePresenter
    fun createSettingsPresenter() = SupportPresenter()

    override fun getPresenter(): SupportPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        setupToolbar()
        setupBottomSheet()
        presenter.checkRegion()
        fabFacebook.setOnClickListener { facebookClick() }
//        fabWhatsapp.setOnClickListener { whatsAppClick() }
        fabViber.setOnClickListener { viberClick() }
        fabTelegram.setOnClickListener { telegramClick() }
        fabEmail.setOnClickListener { presenter.sendEmail(null, null) }
        region.setOnClickListener { showBottomSheetRegion() }
        america.setOnClickListener {
            presenter.setRegion(Region.AMERICA)
            showAmericanRegion()
        }
        europe.setOnClickListener {
            presenter.setRegion(Region.EUROPE)
            showEuropeanRegion()
        }
        asia.setOnClickListener {
            presenter.setRegion(Region.ASIA)
            showAsianRegion()
        }
    }

    override fun showAsianRegion() {
        tvRegion.text = getString(R.string.LNG_REGION_NAMES_ASIA)
        asia.ivCheck.isVisible = true
        america.ivCheck.isVisible = false
        europe.ivCheck.isVisible = false
        showAsianPhones()
    }

    private fun showAsianPhones() {
        asianPhones.isVisible = true
        americanPhones.isGone = true
        europeanPhones.isGone = true
    }

    override fun showAmericanRegion() {
        tvRegion.text = getString(R.string.LNG_REGION_NAMES_AMERICA)
        america.ivCheck.isVisible = true
        europe.ivCheck.isVisible = false
        asia.ivCheck.isVisible = false
        showAmericanPhones()
    }

    private fun showAmericanPhones() {
        americanPhones.isVisible = true
        europeanPhones.isGone = true
        asianPhones.isGone = true
    }

    override fun showEuropeanRegion() {
        tvRegion.text = getString(R.string.LNG_REGION_NAMES_EUROPE)
        europe.ivCheck.isVisible = true
        america.ivCheck.isVisible = false
        asia.ivCheck.isVisible = false
        showEuropeanPhones()
    }

    private fun showEuropeanPhones() {
        europeanPhones.isVisible = true
        americanPhones.isGone = true
        asianPhones.isGone = true
    }

    private fun setupBottomSheet() {
        sheetRegion = BottomSheetBehavior.from(sheetSupport)
        hideSheetRegion()
    }

    private fun showBottomSheetRegion() {
        sheetRegion.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun facebookClick() =
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL)))


    private fun whatsAppClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(""))
        startActivity(intent)
    }

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

    override fun onBackPressed() {
        if (sheetRegion.state == BottomSheetBehavior.STATE_EXPANDED) hideSheetRegion()
        else super.onBackPressed()
    }

    private fun hideSheetRegion() {
        sheetRegion.state = BottomSheetBehavior.STATE_HIDDEN
    }
}