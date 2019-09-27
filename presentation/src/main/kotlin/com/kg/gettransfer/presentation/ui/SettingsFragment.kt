package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater

import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat

import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener

import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.sys.presentation.EndpointModel
import kotlinx.android.synthetic.main.activity_main_navigate.*

import java.util.Locale

import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_settings_field_horizontal_picker.view.field_text
import kotlinx.android.synthetic.main.view_settings_field_switch.view.switch_button
import org.koin.core.KoinComponent

import timber.log.Timber
import kotlinx.android.synthetic.main.view_settings_field_vertical_picker.*

@Suppress("TooManyFunctions")
class SettingsFragment : BaseFragment(), KoinComponent, SettingsView,
        CurrencyChangedListener {

    @InjectPresenter
    internal lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun createSettingsPresenter() = SettingsPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View =
        inflater.inflate(R.layout.fragment_settings, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitleText()
    }

    private fun setTitleText() {
        toolbar.toolbar_title.text = getString(R.string.LNG_MENU_TITLE_SETTINGS)
    }

    override fun recreate() {
        requireActivity().recreate()
    }

    override fun setBalance(balance: String?) {
        settingsProfile.tvBalance.isVisible = true
        settingsProfile.tvBalance.text = getString(R.string.LNG_PAYMENT_AVAILABLE_S, balance)
    }

    override fun hideBalance() {
        settingsProfile.tvBalance.isVisible = false
    }

    @SuppressLint("SetTextI18n")
    override fun setCreditLimit(limit: String?) {
        settingsProfile.tvCreditLimit.isVisible = true
        settingsProfile.tvCreditLimit.text = "${getString(R.string.LNG_PAYMENT_LIMIT, limit)} $limit"
    }

    override fun hideCreditLimit() {
        settingsProfile.tvCreditLimit.isVisible = false
    }

    override fun initGeneralSettingsLayout() {
        Timber.d("current locale: ${Locale.getDefault()}")
        settingsLanguage.setOnClickListener { presenter.onLanguageClicked() }
        settingsCurrency.setOnClickListener { presenter.onCurrencyClicked() }
        with(settingsDistanceUnit) {
            setOnClickListener { view ->
                with(view.switch_button) {
                    isChecked = !isChecked
                    presenter.onDistanceUnitSwitched(isChecked)
                }
            }
        }

        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        versionApp.text = String.format(getString(R.string.app_version), versionName, versionCode)
        shareBtn.setOnClickListener { presenter.onShareClick() }
        layoutAboutApp.setOnClickListener { presenter.onAboutAppClicked() }
    }

    override fun initProfileField(isLoggedIn: Boolean, profile: Profile) {
        with(settingsProfile) {
            if (isLoggedIn) {
                titleText.text = profile.fullName ?: getString(R.string.LNG_PROFILE)
                subtitleText.isVisible = !profile.email.isNullOrEmpty() || !profile.phone.isNullOrEmpty()
                subtitleText.text = profile.email ?: profile.phone ?: ""
            } else {
                titleText.text = getString(R.string.LNG_LOGIN_LOGIN_TITLE)
                subtitleText.isVisible = false
            }
            this.setOnClickListener {
                presenter.onProfileFieldClicked()
            }
        }
    }

    override fun setEmailNotifications(enabled: Boolean) {
        with(settingsEmailNotif) {
            isVisible = true
            setOnClickListener { view ->
                with(view.switch_button) {
                    isChecked = !isChecked
                    presenter.onEmailNotificationSwitched(isChecked)
                }
            }
            switch_button.isChecked = enabled
        }
    }

    override fun hideEmailNotifications() {
        settingsEmailNotif.isVisible = false
    }

    override fun showDebugMenu() {
        layoutDebugSettings.isVisible = true
        settingsResetOnboarding.setOnClickListener { presenter.onResetOnboardingClicked() }
        settingsResetRate.setOnClickListener { presenter.onResetRateClicked() }
        settingsClearAccessToken.setOnClickListener { presenter.onClearAccessTokenClicked() }
        settingsResetNewDriverAppDialog.setOnClickListener { presenter.onResetNewDriverAppDialogClicked() }
        forceCrash.setOnClickListener { presenter.onForceCrashClick() }
    }

    override fun hideDebugMenu() {
        layoutDebugSettings.isVisible = false
    }

    override fun setEndpoints(endpoints: List<EndpointModel>) =
        Utils.setEndpointsDialogListener(requireContext(), settingsEndpoint, endpoints) { presenter.changeEndpoint(it) }

    override fun setCurrency(currency: String) { settingsCurrency.field_text.text = currency }

    override fun setLocale(locale: String, code: String) {
        settingsLanguage.field_text.text = locale
        val langIconParams = LanguageDrawer.LanguageLayoutParamsRes.SETTINGS
        with(settingsLanguage.field_text) {
            text = locale
            setCompoundDrawables(
                null,
                null,
                    ContextCompat.getDrawable(
                        requireContext(),
                        Utils.getLanguageImage(code)
                    )?.apply {
                        setBounds(
                            0,
                            0,
                            resources.getDimensionPixelSize(langIconParams.width),
                            resources.getDimensionPixelSize(langIconParams.height))
                    },
                null
            )
            compoundDrawablePadding = Utils.dpToPxInt(requireContext(), COMPOUND_DRAWABLE_PADDING)
        }
    }

    override fun setEndpoint(endpoint: EndpointModel)  { settingsEndpoint.field_text.text = endpoint.name }

    override fun setDistanceUnit(inMiles: Boolean) {
        settingsDistanceUnit.switch_button.isChecked = inMiles
    }

    override fun currencyChanged(currency: CurrencyModel) {
        presenter.currencyChanged(currency)
    }

    override fun hideSomeDividers() {
        if (!settingsEmailNotif.isVisible) settingsDistanceUnit.hideDivider()
        else settingsDistanceUnit.showDivider()
    }

    override fun showCurrencyChooser() {
        findNavController().navigate(SettingsFragmentDirections.goToSelectCurrency())
    }

    override fun showLanguageChooser() {
        findNavController().navigate(SettingsFragmentDirections.goToSelectLanguage())
    }

    override fun showOrderItem() {
        (activity as MainNavigateActivity).bottom_nav.selectedItemId = R.id.nav_order
    }

    companion object {
        private const val COMPOUND_DRAWABLE_PADDING = 8f
    }
}
