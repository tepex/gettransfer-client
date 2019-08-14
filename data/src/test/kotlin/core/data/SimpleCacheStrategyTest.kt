package com.kg.gettransfer.core.data

import com.kg.gettransfer.core.domain.Result

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.koin.KoinListener
import io.kotlintest.specs.StringSpec

import io.mockk.*
import io.mockk.impl.annotations.MockK

import org.koin.dsl.module

import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.check.checkModules

class SimpleCacheStrategyTest : StringSpec() {

    val default = Test("")
    val m: (TestEntity) -> Test = { it.map() }
    @MockK
    lateinit var cache: MutableDataSource<TestEntity?>
    @MockK
    lateinit var remote: ReadableDataSource<TestEntity>
    lateinit var cacheStrategy: SimpleCacheStrategy<TestEntity, Test>

    init {
        MockKAnnotations.init(this)
        "Cold. Should return default Test with error Result" {
            coEvery { remote.getResult() } returns REMOTE_ERROR_RESULT
            coEvery { cache.getResult() } returns ResultData.Success<TestEntity?>(null)

            val result = cacheStrategy.getAndCache(default, m)

            coVerify { cacheStrategy.getAndCache(default, m) }
            coVerify(exactly = 0) { cacheStrategy.clearCache() }
            verify(exactly = 0) { cacheStrategy.clearMemoryCache() }
            coVerify { remote.getResult() }
            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.put(any<TestEntity>()) }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(cacheStrategy)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Failure(default, ERROR_RESULT)
            cacheStrategy.memoryCache shouldBe null
        }

        "From remote. Should return success Test from `remote`, put it into `cache` and local variable" {
            coEvery { remote.getResult() } returns ResultData.Success(TEST_ENTITY)
            coEvery { cache.put(TEST_ENTITY) } just Runs

            val result = cacheStrategy.getAndCache(default, m)

            coVerify { cacheStrategy.getAndCache(default, m) }
            coVerify(exactly = 0) { cacheStrategy.clearCache() }
            verify(exactly = 0) { cacheStrategy.clearMemoryCache() }
            coVerify { remote.getResult() }
            coVerify { cache.put(TEST_ENTITY) }
            coVerify(exactly = 0) { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(cacheStrategy)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Success(TEST)
            cacheStrategy.memoryCache shouldBe TEST
        }

        "From cache. Should return Test from `cache` with error Result and don't put it into local variable" {
            coEvery { remote.getResult() } returns REMOTE_ERROR_RESULT
            coEvery { cache.getResult() } returns ResultData.Success(TEST_ENTITY)

            val result = cacheStrategy.getAndCache(default, m)

            coVerify { cacheStrategy.getAndCache(default, m) }
            coVerify(exactly = 0) { cacheStrategy.clearCache() }
            verify(exactly = 0) { cacheStrategy.clearMemoryCache() }
            coVerify { remote.getResult() }
            coVerify(exactly = 0) { cache.put(any<TestEntity>()) }
            coVerify { cache.getResult() }
            coVerify(exactly = 0) { cache.clear() }

            confirmVerified(cacheStrategy)
            confirmVerified(remote)
            confirmVerified(cache)

            result shouldBe Result.Failure(TEST, ERROR_RESULT)
            cacheStrategy.memoryCache shouldBe null
        }
    }

    override fun beforeTest(testCase: TestCase) {
        clearAllMocks()
        cacheStrategy = spyk(SimpleCacheStrategy<TestEntity, Test>(cache, remote))
    }

    companion object {
        val TEST = Test("qqq")
        val TEST_ENTITY = TestEntity("qqq")
        val REMOTE_ERROR_RESULT = ResultData.ApiError(-1, "", null)
        val ERROR_RESULT = Result.Error.Api(Result.Error.Api.Code.OTHER, "", null)
    }
}

data class TestEntity(val s: String)

data class Test(val s: String)

fun TestEntity.map() = Test(s)
