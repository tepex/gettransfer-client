package com.kg.gettransfer.sys.presentation

import com.kg.gettransfer.core.domain.Hour
import com.kg.gettransfer.core.domain.Result
import com.kg.gettransfer.core.domain.Second

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.di.ENDPOINTS
import com.kg.gettransfer.di.systemDomain
import com.kg.gettransfer.di.systemPresentation

import com.kg.gettransfer.sys.domain.Configs
import com.kg.gettransfer.sys.domain.ConfigsRepository
import com.kg.gettransfer.sys.domain.Endpoint
import com.kg.gettransfer.sys.domain.EndpointRepository
import com.kg.gettransfer.sys.domain.MobileConfigs
import com.kg.gettransfer.sys.domain.MobileConfigsRepository
import com.kg.gettransfer.sys.domain.Preferences
import com.kg.gettransfer.sys.domain.PreferencesRepository

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe

import io.kotlintest.extensions.TopLevelTest

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.specs.StringSpec

import io.mockk.*
import io.mockk.impl.annotations.MockK

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

import org.slf4j.Logger

@ExperimentalCoroutinesApi
class ConfigsManagerTest : StringSpec(), KoinTest {
    @MockK
    lateinit var configsRepository: ConfigsRepository
    @MockK
    lateinit var mobileConfigsRepository: MobileConfigsRepository
    @MockK
    lateinit var preferencesRepository: PreferencesRepository
    @MockK
    lateinit var endpointRepository: EndpointRepository

    lateinit var configsManager: ConfigsManager
    lateinit var worker: WorkerManager
    lateinit var testEndpoint: Endpoint
    lateinit var log: Logger

    private val testDispatcher = TestCoroutineDispatcher()

    init {
        MockKAnnotations.init(this)

        "success coldStart" {

            coEvery { preferencesRepository.getResult() } coAnswers { Result.Success(PREFERENCES) }
            coEvery { preferencesRepository.put(PREFERENCES) } coAnswers { Result.Success(PREFERENCES) }
            coEvery { endpointRepository.put(testEndpoint.url) } coAnswers { Result.Success<String>(testEndpoint.url) }
            coEvery { mobileConfigsRepository.getResult() } coAnswers { Result.Success(MOBILE_CONFIGS) }
            coEvery { configsRepository.getResult() } coAnswers { Result.Success(CONFIGS) }

            val start = System.currentTimeMillis()

            val result = configsManager.coldStart(worker.backgroundScope)
            println("Test.result elapsed: ${System.currentTimeMillis()-start}")

            /* verify configsManager */
            coVerify { configsManager.coldStart(any()) }
            //verify { configsManager getProperty "preferences" }
            coVerify(exactly = 0) { configsManager.apply() }

            /* verify repositories */
            coVerify { preferencesRepository.put(PREFERENCES) }
            coVerify { preferencesRepository.getResult() }
            coVerify(exactly = 0) { preferencesRepository.clearCache() }
            verify(exactly = 0)   { preferencesRepository.clearMemoryCache() }

            coVerify { configsRepository.getResult() }
            coVerify(exactly = 0) { configsRepository.clearCache() }
            verify(exactly = 0)   { configsRepository.clearMemoryCache() }

            coVerify { mobileConfigsRepository.getResult() }
            coVerify(exactly = 0) { mobileConfigsRepository.clearCache() }
            verify(exactly = 0)   { mobileConfigsRepository.clearMemoryCache() }

            coVerify { endpointRepository.put(testEndpoint.url) }

            confirmVerified(configsManager)
            confirmVerified(configsRepository)
            confirmVerified(mobileConfigsRepository)
            confirmVerified(preferencesRepository)
            confirmVerified(endpointRepository)

            result.shouldBeNull()
            configsManager.configs shouldBe CONFIGS
            configsManager.mobile shouldBe MOBILE_CONFIGS
            configsManager.preferences shouldBe PREFERENCES
        }

        "failure mobileConfigs".config(enabled = false) {
            coEvery { configsRepository.getResult() } coAnswers {
                delay(300)
                Result.Success(CONFIGS)
            }
            coEvery { mobileConfigsRepository.getResult() } coAnswers {
                delay(500)
                Result.Success(MOBILE_CONFIGS)
            }
            coEvery { preferencesRepository.getResult() } coAnswers {
                delay(200)
                Result.Success(PREFERENCES)
            }

            testDispatcher.runBlockingTest {
                val result = configsManager.coldStart(worker.backgroundScope)

                delay(400)

                /* verify configsManager */
                coVerify { configsManager.coldStart(worker.backgroundScope) }
                coVerify(exactly = 0) { configsManager.apply() }

                /* verify repositories */
                coVerify { configsRepository.getResult() }
                coVerify(exactly = 0) { configsRepository.clearCache() }
                verify(exactly = 0)   { configsRepository.clearMemoryCache() }

                coVerify { mobileConfigsRepository.getResult() }
                coVerify(exactly = 0) { mobileConfigsRepository.clearCache() }
                verify(exactly = 0)   { mobileConfigsRepository.clearMemoryCache() }

                coVerify { preferencesRepository.getResult() }
                coVerify(exactly = 0) { preferencesRepository.clearCache() }
                verify(exactly = 0)   { preferencesRepository.clearMemoryCache() }

                confirmVerified(configsManager)
                confirmVerified(configsRepository)
                confirmVerified(mobileConfigsRepository)
                confirmVerified(preferencesRepository)

                result.shouldBeNull()
                configsManager.configs shouldBe CONFIGS
                configsManager.mobile shouldBe MOBILE_CONFIGS
                configsManager.preferences shouldBe PREFERENCES
            }
        }
    }

    override fun beforeTest(testCase: TestCase) {
        clearAllMocks()
        worker = get<WorkerManager> { parametersOf ("Test Main scope") }
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        worker.cancel()
    }

    override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                listOf(
                    module {
                        single<CoroutineDispatcher> { Dispatchers.Default }
                        single<ConfigsRepository> { configsRepository }
                        single<MobileConfigsRepository> { mobileConfigsRepository }
                        single<PreferencesRepository> { preferencesRepository }
                        single<EndpointRepository> { endpointRepository }
                    },
                    testEndpoints,
                    systemDomain,
                    systemPresentation
                )
            )
        }
        configsManager = spyk(get<ConfigsManager>())
        testEndpoint = get<List<Endpoint>>(named(ENDPOINTS)).find { it == PREFERENCES.endpoint }!!
        log = get<Logger> { parametersOf("Test") }
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        stopKoin()
        // We should finish koin after test execution
        GlobalContext.getOrNull() shouldBe null
        worker.main.isActive.shouldBeTrue()
        worker.backgroundScope.isActive.shouldBeFalse()
    }

    companion object {
        val CONFIGS =
            Configs(
                emptyList(),
                emptyList(),
                1.1f,
                emptyList(),
                emptyList(),
                emptyList()
            )

        val MOBILE_CONFIGS =
            MobileConfigs(
                orderMinimum = Hour(1).minutes,
                termsUrl = "terms_of_use",
                smsResendDelay = Second(90),
                isDriverAppNotify = false,
                isDriverModeBlock = false,
                buildsConfigs = emptyMap()
            )
        val PREFERENCES =
            Preferences(
                accessToken = "access token",
                endpoint = Endpoint("test3", "key3", "url3", false, true),
                isFirstLaunch = false,
                isOnboardingShowed = false,
                selectedField = "selected field",
                addressHistory = emptyList(),
                appEnters = 2,
                isDebugMenuShowed = false,
                favoriteTransports = emptySet()
            )
    }
}
