package com.kg.gettransfer.sys.domain

class SetSelectedFieldInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: String) {
        repository.getResult().getModel().copy(selectedField = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
