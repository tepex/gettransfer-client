package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.core.domain.Result
import com.kg.gettransfer.sys.domain.Configs
import com.kg.gettransfer.sys.domain.ConfigsRepository

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
import org.koin.test.check.checkModules

class ConfigsRepositoryTest : StringSpec(), KoinTest {
    @MockK
    lateinit var cache: ConfigsCacheDataSource
    @MockK
    lateinit var remote: ConfigsRemoteDataSource

    lateinit var repository: ConfigsRepository

    init {
        MockKAnnotations.init(this)

        "Cold. Should return default Configs with error Result" {
            coEvery { remote.getResult() } returns REMOTE_ERROR_RESULT
            coEvery { cache.getResult() } returns ResultData.Success<ConfigsEntity?>(null)

            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify { remote.getResult() }

            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.put(any<ConfigsEntity>()) }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Failure(get<Configs>(), ERROR_RESULT)
        }

        "should return success Configs from `remote`, put it into `cache` and memoryCache" {
            coEvery { remote.getResult() } returns ResultData.Success(CONFIGS_ENTITY)
            coEvery { cache.put(CONFIGS_ENTITY) } just Runs

            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify { remote.getResult() }

            coVerify { cache.put(CONFIGS_ENTITY) }
            coVerify(exactly = 0) { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Success(CONFIGS)
        }

        "should return success Configs from memoryCache" {
            val result = repository.getResult()

            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }
            verify(exactly = 0) { repository.clearMemoryCache() }

            coVerify(exactly = 0) { remote.getResult() }

            coVerify(exactly = 0) { cache.getResult() }
            coVerify(exactly = 0) { cache.put(any<ConfigsEntity>()) }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Success(CONFIGS)
        }

        "should return Configs from `cache` with error Result" {
            coEvery { remote.getResult() } returns REMOTE_ERROR_RESULT
            coEvery { cache.getResult() } returns ResultData.Success(CONFIGS_ENTITY)

            repository.clearMemoryCache()
            val result = repository.getResult()

            verify { repository.clearMemoryCache() }
            coVerify { repository.getResult() }
            coVerify(exactly = 0) { repository.clearCache() }

            coVerify { remote.getResult() }

            coVerify(exactly = 0) { cache.put(any<ConfigsEntity>()) }
            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(repository)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Failure(CONFIGS, ERROR_RESULT)
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
                        single<ConfigsRemoteDataSource> { remote }
                        single<ConfigsCacheDataSource> { cache }
                    },
                    systemData
                )
            )
        }
        repository = spyk(get<ConfigsRepository>())
    }

    override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
        stopKoin()
        // We should finish koin after test execution
        GlobalContext.getOrNull() shouldBe null
    }

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

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
        val CONFIGS_ENTITY =
            ConfigsEntity(
                emptyList(),
                emptyList(),
                1.1f,
                emptyList(),
                emptyList(),
                emptyList()
            )

        val REMOTE_ERROR_RESULT = ResultData.ApiError(-1, "", null)
        val ERROR_RESULT = Result.Error.Api(Result.Error.Api.Code.OTHER, "", null)
    }
}
