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
        const val FB_PACKAGE = "com.facebook.katana"
        const val VIBER_PACKAGE = "com.viber.voip"
        const val FACEBOOK_URL = "https://www.facebook.com/gettransfer"
        const val FACEBOOK_URI = "fb://facewebmodal/f?href="
        const val VIBER_URL = "https://viber.me/GetTransferSupport"
        const val VIBER_URI = "viber://pa?chatURI=GetTransferSupport"
        const val TELEGRAM_URL = "https://t.me/gettransfersupportbot"
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
        showEuropeanRegion()
        fabFacebook.setOnClickListener { facebookClick() }
//        fabWhatsapp.setOnClickListener { whatsAppClick() }
        fabViber.setOnClickListener { viberClick() }
        fabTelegram.setOnClickListener { telegramClick() }
        fabEmail.setOnClickListener { presenter.sendEmail(null, null) }
        region.setOnClickListener { showBottomSheetRegion() }
        america.setOnClickListener { showAmericanRegion() }
        europe.setOnClickListener { showEuropeanRegion() }
        asia.setOnClickListener { showAsianRegion() }
    }

    private fun showAsianRegion() {
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

    private fun showAmericanRegion() {
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

    private fun showEuropeanRegion() {
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

    private fun facebookClick() {
        val intent: Intent = try {
            packageManager.getPackageInfo(FB_PACKAGE, 0)
            Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URI + FACEBOOK_URL))
                    .apply { setPackage(FB_PACKAGE) }
        } catch (e: PackageManager.NameNotFoundException) {
            Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL))
        }
        startActivity(intent)
    }

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

    private fun telegramClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_URL))
        startActivity(intent)
    }

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