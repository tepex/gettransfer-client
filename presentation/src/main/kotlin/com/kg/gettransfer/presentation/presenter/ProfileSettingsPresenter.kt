package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.view.ProfileSettingsView
import com.kg.gettransfer.presentation.view.Screens
import org.koin.standalone.inject

@InjectViewState
class ProfileSettingsPresenter : BasePresenter<ProfileSettingsView>() {
    private val profileMapper: ProfileMapper by inject()

    override fun attachView(view: ProfileSettingsView) {
        super.attachView(view)
        accountManager.initTempUser()
        viewState.initFields(profileMapper.toView(accountManager.remoteProfile))
    }

    fun setName(name: String) {
        accountManager.tempProfile.fullName = name.trim()
        setEnabledBtnSave()
    }

    private fun setEnabledBtnSave() {
        with (accountManager){
            viewState.setEnabledBtnSave(remoteProfile.fullName != tempProfile.fullName)
        }
    }

    fun onChangeEmailClicked() {
        router.navigateTo(Screens.ChangeEmail())
    }

    fun onChangePasswordClicked() {
        router.navigateTo(Screens.ChangePassword())
    }

    fun onSaveBtnClicked() {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = fetchResultOnly { accountManager.putAccount(true, updateTempUser = true) }
            result.error?.let { viewState.setError(it) }
            viewState.blockInterface(false)
        }
    }
}