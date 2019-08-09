package com.kg.gettransfer.sys.domain

class SetLastCarrierTripsTypeViewInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: String) {
        repository.getResult().getModel().copy(lastCarrierTripsTypeView = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
