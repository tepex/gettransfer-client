package com.kg.gettransfer.core.remote

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.domain.repository.SessionRepository

import com.kg.gettransfer.remote.remoteModule

import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.specs.StringSpec

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.declareMock

import org.slf4j.Logger

class CheckModulesTest : StringSpec(), KoinTest {

    init {
        "dry run Koin modules" {
            startKoin {
                // printLogger(Level.DEBUG)
                modules(
                    listOf(
                        module {
                            single<PreferencesCache> { declareMock<PreferencesCache>()  }
                            single<SessionRepository> { declareMock<SessionRepository>() }
                        },
                        remoteModule
                    )
                )
            }.checkModules {
                create<Logger> { parametersOf("test") }
            }
        }
    }

    override fun beforeTest(testCase: TestCase) {}

    override fun afterTest(testCase: TestCase, result: TestResult) {
        stopKoin()
    }
}
