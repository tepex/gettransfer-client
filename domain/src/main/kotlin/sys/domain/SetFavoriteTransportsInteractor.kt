package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.domain.model.TransportType

class SetFavoriteTransportsInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Set<TransportType.ID>) {
        repository.getResult().getModel().copy(favoriteTransports = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
