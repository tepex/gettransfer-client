package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.core.domain.Result

import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.domain.model.Endpoint

import com.kg.gettransfer.sys.domain.Preferences
import com.kg.gettransfer.sys.domain.PreferencesRepository

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseOrder
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.koin.KoinListener
import io.kotlintest.specs.StringSpec

import io.mockk.*
import io.mockk.impl.annotations.MockK

import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

import org.koin.dsl.module

import org.koin.test.KoinTest
import org.koin.test.get

class PreferencesRepositoryTest : StringSpec(), KoinTest {
    @MockK
    lateinit var cache: PreferencesCacheDataSource

    lateinit var repository: PreferencesRepository

    init {
        MockKAnnotations.init(this)

        "Cold. Should return default Preferences with success Result and put it into memoryCache" {
            coEvery { cache.getResult() } returns ResultData.Success<PreferencesEntity?>(null)

            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.put(any<PreferencesEntity>()) }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(cache)

            result shouldBe Result.Success(get<Preferences>())
        }

        "should return success Preferences from `cache`" {
            coEvery { cache.getResult() } returns ResultData.Success(PREFERENCES_ENTITY)

            repository.clearMemoryCache()
            val result = repository.getResult()

            verify { repository.clearMemoryCache() }
            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }

            coVerify(exactly = 0) { cache.put(any<PreferencesEntity>()) }
            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(cache)

            result shouldBe Result.Success(PREFERENCES)
        }

        "should return success Preferences from memoryCache" {
            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify(exactly = 0) { cache.put(any<PreferencesEntity>()) }
            coVerify(exactly = 0) { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(cache)

            result shouldBe Result.Success(PREFERENCES)
        }
    }

    override fun beforeTest(testCase: TestCase) {
        clearAllMocks()
    }

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        startKoin {
            modules(
                listOf(
                    module {
                        single<PreferencesCacheDataSource> { cache }
                    },
                    systemData
                )
            )
        }
        repository = spyk(get<PreferencesRepository>())
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        stopKoin()
        // We should finish koin after test execution
        GlobalContext.getOrNull() shouldBe null
    }

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

    companion object {
        val PREFERENCES =
            Preferences(
                accessToken = "access token",
                endpoint = "endpoint",
                lastMode = "last mode",
                lastMainScreenMode = "last main screen mode",
                lastCarrierTripsTypeView = "last carrier trips type view",
                firstDayOfWeek = 1,
                isFirstLaunch = false,
                isOnboardingShowed = false,
                selectedField = "selected field",
                addressHistory = emptyList(),
                appEntersForMarketRate = 2,
                isDebugMenuShowed = false,
                favoriteTransports = emptySet()
            )
        val PREFERENCES_ENTITY =
            PreferencesEntity(
                accessToken = "access token",
                endpoint = "endpoint",
                lastMode = "last mode",
                lastMainScreenMode = "last main screen mode",
                lastCarrierTripsTypeView = "last carrier trips type view",
                firstDayOfWeek = 1,
                isFirstLaunch = false,
                isOnboardingShowed = false,
                selectedField = "selected field",
                addressHistory = emptyList(),
                appEntersForMarketRate = 2,
                isDebugMenuShowed = false,
                favoriteTransports = emptySet()
            )
    }
}
