package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.ProfileSettingsView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.core.inject

@InjectViewState
class ProfileSettingsPresenter : BasePresenter<ProfileSettingsView>() {

    private val profileMapper: ProfileMapper by inject()

    private var phoneChanged = false

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
        accountManager.tempProfile.fullName = name.trim()
        setEnabledBtnSave()
    }

    fun setPhone(phone: String) {
        accountManager.tempProfile.phone = phone.trim().replace(" ", "")
        phoneChanged = true
        setEnabledBtnSave()
    }

    private fun setEnabledBtnSave() {
        with (accountManager) {
            viewState.setEnabledBtnSave(
                remoteProfile.fullName != tempProfile.fullName || remoteProfile.phone != tempProfile.phone
            )
        }
    }

    fun onChangeEmailClicked() {
        router.navigateTo(Screens.ChangeEmail())
    }

    fun onChangePasswordClicked() {
        router.navigateTo(Screens.ChangePassword())
    }

    fun onSaveBtnClicked() {
        if (phoneChanged && !Utils.checkPhone(accountManager.tempProfile.phone)) {
            viewState.setError(false, R.string.LNG_ERROR_PHONE)
            return
        }

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = fetchResultOnly { accountManager.putAccount() }
            if (result.error == null) {
                viewState.setEnabledBtnSave(false)
                if (phoneChanged) {
                    phoneChanged = false
                    viewState.setEnabledPhoneField(false)
                }
            } else {
                result.error?.let {
                    when {
                        it.isAccountExistError() -> viewState.setError(false, R.string.LNG_PHONE_TAKEN_ERROR)
                        else                     -> viewState.setError(result.error!!)
                    }
                }
            }
            viewState.blockInterface(false)
        }
    }
}
