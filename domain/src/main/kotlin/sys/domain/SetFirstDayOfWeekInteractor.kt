package com.kg.gettransfer.sys.domain

class SetFirstDayOfWeekInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Int) {
        repository.getResult().getModel().copy(firstDayOfWeek = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
