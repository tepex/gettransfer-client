package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.core.domain.Hour
import com.kg.gettransfer.core.domain.Result
import com.kg.gettransfer.core.domain.Second

import com.kg.gettransfer.sys.domain.MobileConfigs
import com.kg.gettransfer.sys.domain.MobileConfigsRepository

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

class MobileConfigsRepositoryTest : StringSpec(), KoinTest {
    @MockK
    lateinit var cache: MobileConfigsCacheDataSource
    @MockK
    lateinit var remote: MobileConfigsRemoteDataSource

    lateinit var repository: MobileConfigsRepository

    init {
        MockKAnnotations.init(this)

        "Cold. should return default MobileConfigs with error Result" {
            coEvery { remote.getResult() } returns REMOTE_ERROR_RESULT
            coEvery { cache.getResult() } returns ResultData.Success<MobileConfigsEntity?>(null)

            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify { remote.getResult() }

            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.put(any<MobileConfigsEntity>()) }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Failure(get<MobileConfigs>(), ERROR_RESULT)
        }

        "should return success MobileConfigs from `remote`, put it into `cache` and memoryCache" {
            coEvery { remote.getResult() } returns ResultData.Success(MOBILE_CONFIGS_ENTITY)
            coEvery { cache.put(MOBILE_CONFIGS_ENTITY) } just Runs

            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify { remote.getResult() }

            coVerify { cache.put(MOBILE_CONFIGS_ENTITY) }
            coVerify(exactly = 0) { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Success(MOBILE_CONFIGS)
        }

        "should return success MobileConfigs from memoryCache" {
            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify(exactly = 0) { remote.getResult() }

            coVerify(exactly = 0) { cache.getResult() }
            coVerify(exactly = 0) { cache.put(any<MobileConfigsEntity>()) }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Success(MOBILE_CONFIGS)
        }

        "should return MobileConfigs from `cache` with error Result" {
            coEvery { remote.getResult() } returns REMOTE_ERROR_RESULT
            coEvery { cache.getResult() } returns ResultData.Success(MOBILE_CONFIGS_ENTITY)

            repository.clearMemoryCache()
            val result = repository.getResult()

            verify { repository.clearMemoryCache() }
            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }

            coVerify { remote.getResult() }

            coVerify(exactly = 0) { cache.put(any<MobileConfigsEntity>()) }
            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Failure(MOBILE_CONFIGS, ERROR_RESULT)
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
                        single<MobileConfigsRemoteDataSource> { remote }
                        single<MobileConfigsCacheDataSource> { cache }
                    },
                    systemData
                )
            )
        }
        repository = spyk(get<MobileConfigsRepository>())
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        stopKoin()
        // We should finish koin after test execution
        GlobalContext.getOrNull() shouldBe null
    }

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

    companion object {
        val MOBILE_CONFIGS =
            MobileConfigs(
                /* pushShowDelay = 5, */
                orderMinimum = Hour(1).minutes,
                termsUrl = "terms_of_use",
                smsResendDelay = Second(90),
                isDriverAppNotify = false,
                isDriverModeBlock = false,
                buildsConfigs = emptyMap()
            )
        val MOBILE_CONFIGS_ENTITY =
            MobileConfigsEntity(
                pushShowDelay = 5,
                orderMinimumMinutes = 60,
                termsUrl = "terms_of_use",
                smsResendDelaySec = 90,
                isDriverAppNotify = false,
                isDriverModeBlock = false,
                buildsConfigs = emptyMap()
            )

        val REMOTE_ERROR_RESULT = ResultData.ApiError(-1, "", null)
        val ERROR_RESULT = Result.Error.Api(Result.Error.Api.Code.OTHER, "", null)
    }
}
