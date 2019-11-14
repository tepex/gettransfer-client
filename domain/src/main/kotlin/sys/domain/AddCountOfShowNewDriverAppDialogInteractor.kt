package com.kg.gettransfer.sys.domain

class AddCountOfShowNewDriverAppDialogInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke() {
        repository.getResult().getModel().also { prefs ->
            prefs.copy(countOfShowNewDriverAppDialog = prefs.countOfShowNewDriverAppDialog + 1).also { newPrefs ->
                repository.put(newPrefs)
            }
        }
    }
}
