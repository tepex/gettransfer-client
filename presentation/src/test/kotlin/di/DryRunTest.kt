package com.kg.gettransfer.di

import android.app.Application
import android.content.Context

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.presenter.*

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.checkModules

import org.mockito.Mockito.mock

/**
 * Dry run configuration
 */
class DryRunTest : KoinTest {
    val mockedAndroidContext = module {
        single { mock(Application::class.java) }
        single { mock(Context::class.java) }
    }

    val mockedDomainModule = module {
        single { mock(SystemInteractor::class.java) }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `check Koin configuration`() {
        checkModules(listOf(mockedAndroidContext) + androidModule + mappersModule + mockedDomainModule)
    }
}
