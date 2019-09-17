package com.kg.gettransfer.sys.domain

class SetNewDriverAppDialogShowedInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Boolean) {
        repository.getResult().getModel().copy(isNewDriverAppDialogShowed = value).also { repository.put(it) }
    }
}
