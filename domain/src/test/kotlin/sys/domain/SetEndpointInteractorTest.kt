package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.core.domain.Result

import io.kotlintest.Spec
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.specs.StringSpec

import io.mockk.*
import io.mockk.impl.annotations.MockK

class SetEndpointInteractorTest : StringSpec() {

    @MockK
    lateinit var preferencesRepository: PreferencesRepository
    @MockK
    lateinit var endpointRepository: EndpointRepository

    lateinit var setEndpoint: SetEndpointInteractor

    init {
        MockKAnnotations.init(this)

        "save preferences with new endpoint.key and put url into EndpointRepository" {
            val newPreferences = PREFERENCES.copy(endpoint = ID)
            coEvery { preferencesRepository.getResult() } returns Result.Success(PREFERENCES)
            coEvery { preferencesRepository.put(newPreferences) } returns Result.Success(newPreferences)
            coEvery { endpointRepository.put(URL) } returns Result.Success(URL)

            setEndpoint(ID, URL)

            coVerify { setEndpoint(ID, URL) }

            coVerify { preferencesRepository.getResult() }
            coVerify { preferencesRepository.put(newPreferences) }
            coVerify(exactly = 0) { preferencesRepository.clearCache() }
            verify(exactly = 0)   { preferencesRepository.clearMemoryCache() }

            coVerify { endpointRepository.put(URL) }

            confirmVerified(setEndpoint)
            confirmVerified(preferencesRepository)
            confirmVerified(endpointRepository)
        }
    }

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        setEndpoint = spyk(SetEndpointInteractor(preferencesRepository, endpointRepository))
    }

    companion object {
        const val ID = "id"
        const val URL = "url"

        val PREFERENCES =
            Preferences(
                accessToken = "access token",
                endpoint = "key3",
                isFirstLaunch = false,
                isOnboardingShowed = false,
                isNewDriverAppDialogShowed = false,
                countOfShowingNewDriverAppDialog = 0,
                selectedField = "selected field",
                addressHistory = emptyList(),
                favoriteTransports = emptySet(),
                appEnters = 2,
                isDebugMenuShowed = false
            )
    }
}
