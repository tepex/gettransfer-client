package com.kg.gettransfer.sys.domain

class SetOnboardingShowedInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Boolean) {
        repository.getResult().getModel().copy(isOnboardingShowed = value).also { repository.put(it) }
    }
}
