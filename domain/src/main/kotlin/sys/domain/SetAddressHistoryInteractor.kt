package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.core.domain.GTAddress

class SetAddressHistoryInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: List<GTAddress>) {
        repository.getResult().getModel().copy(addressHistory = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
