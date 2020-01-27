package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.view.ProfileSettingsView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

@InjectViewState
class ProfileSettingsPresenter : BasePresenter<ProfileSettingsView>() {

    private val profileMapper: ProfileMapper by inject()

    override fun attachView(view: ProfileSettingsView) {
        super.attachView(view)
        accountManager.initTempUser()
        viewState.initFields(profileMapper.toView(accountManager.remoteProfile))
    }

    override fun onDestroy() {
        super.onDestroy()
        accountManager.initTempUser()
    }

    fun setName(name: String) {
        with(accountManager) {
            tempProfile.fullName = name.trim()
            viewState.setEnabledBtnSave(remoteProfile.fullName != tempProfile.fullName)
        }
    }

    fun onChangeEmailClicked() {
        router.navigateTo(Screens.ChangeEmail())
    }

    fun onChangePhoneClicked() {
        router.navigateTo(Screens.ChangePhone())
    }

    fun onChangePasswordClicked() {
        router.navigateTo(Screens.ChangePassword())
    }

    fun onLogout() {
        utils.launchSuspend {
            utils.asyncAwait { accountManager.logout() }.isSuccess()?.let {
                router.exit()
            }
        }
        analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.LOG_OUT_PARAM, Analytics.EMPTY_VALUE)
    }

    fun onSaveBtnClicked() {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResultOnly { accountManager.putAccount() }.run {
                error?.let { viewState.setError(it) } ?: viewState.setEnabledBtnSave(false)
            }
            viewState.blockInterface(false)
        }
    }
}
