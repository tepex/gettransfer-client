package com.kg.gettransfer.presentation.ui

import android.content.Intent

import android.net.Uri

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v4.app.MemoryLeakUtils
import android.support.v4.content.FileProvider
import android.support.v7.widget.Toolbar

import android.view.View

import android.widget.Toast

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.presenter.SettingsPresenter

import com.kg.gettransfer.presentation.view.SettingsView

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.jetbrains.anko.toast

import timber.log.Timber
import java.io.File

class SettingsActivity: BaseActivity(), SettingsView {
    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter
    
    @ProvidePresenter
    fun createSettingsPresenter() = SettingsPresenter()
    
	override fun getPresenter(): SettingsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_SETTINGS)
        
        btnSignOut.setOnClickListener { presenter.onLogout() }
        layoutSettingsLogs.setOnClickListener { presenter.onLogsClicked() }
        layoutSettingsResetOnboarding.setOnClickListener { presenter.onResetOnboardingClicked() }
        btnSupport.setOnClickListener { presenter.onSupportButtonClicked() }

        //Not showing some layouts in release
        if(BuildConfig.FLAVOR != "dev") {
            layoutSettingsEndpoint.visibility = View.GONE
            layoutSettingsLogs.visibility = View.GONE
            layoutSettingsResetOnboarding.visibility = View.GONE
        }
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) =
        Utils.setCurrenciesDialogListener(this, layoutSettingsCurrency, currencies) { presenter.changeCurrency(it) }

    override fun setLocales(locales: List<LocaleModel>) =
        Utils.setLocalesDialogListener(this, layoutSettingsLanguage, locales) {
            localeManager.updateResources(this, presenter.changeLocale(it))
            recreate()
        }

    override fun setDistanceUnits(distanceUnits: List<DistanceUnitModel>) =
        Utils.setDistanceUnitsDialogListener(this, layoutSettingsDistanceUnits, distanceUnits) { presenter.changeDistanceUnit(it) }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
        Utils.setEndpointsDialogListener(this, layoutSettingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

    override fun setCurrency(currency: String)         { tvSelectedCurrency.text = currency }
    override fun setLocale(locale: String)             { tvSelectedLanguage.text = locale }
    override fun setDistanceUnit(distanceUnit: String) { tvSelectedDistanceUnits.text = distanceUnit }
    override fun setEndpoint(endpoint: EndpointModel)  { tvSelectedEndpoint.text = endpoint.name }

    override fun setLogoutButtonEnabled(enabled: Boolean) {
        if(enabled) btnSignOut.visibility = View.VISIBLE else btnSignOut.visibility = View.GONE
    }

    override fun restartApp() {
        baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(it)
        }
        finish()
    }

    override fun sendEmailInSupport(logsFile: File) {
        val path = FileProvider.getUriForFile(applicationContext, getString(R.string.file_provider_authority), logsFile)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.type = "text/*"
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_support)))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.LNG_EMAIL_SUBJECT))
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        } catch (ex: android.content.ActivityNotFoundException) {
            this.toast(getString(R.string.no_email_apps))
        }
    }
}
