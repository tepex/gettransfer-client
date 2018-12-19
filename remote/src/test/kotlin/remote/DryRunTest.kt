package com.kg.gettransfer.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

import com.kg.gettransfer.data.model.AccountEntity

import com.kg.gettransfer.remote.mapper.*
import com.kg.gettransfer.remote.model.*

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import java.io.FileInputStream
import java.io.Reader

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.checkModules

import org.mockito.Mockito.mock

/**
 * Dry run configuration
 */
class DryRunTest : KoinTest {
    /*
    val mockedAndroidContext = module {
        single { mock(Application::class.java) }
        single { mock(Context::class.java) }
    }
    */
    @Before
    fun before() {
        startKoin(listOf(remoteMappersModule))
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `check Koin configuration`() {
        checkModules(listOf(remoteMappersModule))
    }

    @Test
    fun `check Account serialization`() {
        val reader = getReader("account.json")
        val account = gson.fromJson(reader, AccountModel::class.java)
        println("account: $account")
        assertEquals(account, ACCOUNT_TEST)
    }

    @Test
    fun `check Offer model`() {
        val offer = OfferModel(
            1,
            "new",
            true,
            true,
            "created",
            null,
            PriceModel(
                base = MoneyModel("def", null),
                withoutDiscount = null,
                percentage30 = "30",
                percentage70 = "70",
                amount = 0.3
            ),
            null,
            null,
            CarrierModel(
                id = 2,
                title = null,
                email = null,
                phone = null,

                approved = true,
                completedTransfers = 3,
                languages = emptyList<LocaleModel>(),
                ratings = RatingsModel(0.5f, 0.5f, 0.5f, 0.5f),
                canUpdateOffers = null
            ),
            VehicleModel(
                name = "v name",
                registrationNumber = "edfsd",
                id = 3,
                year = 2000,
                color = null,
                transportTypeId = "economy",
                paxMax = 3,
                luggageMax = 4,
                photos = emptyList<String>()
            ),
            null
        )
    }

    companion object {
        val gson = GsonBuilder()
            .registerTypeAdapter(TransportTypesWrapperModel::class.java, TransportTypesDeserializer())
            .excludeFieldsWithoutExposeAnnotation()
            .create()

        val ACCOUNT_TEST = AccountModel(
            fullName = "John Smith",
            email = "test@email.com",
            phone = "+79255898040",
            locale = "ru",
            currency = "RUB",
            distanceUnit = "km",
            groups = listOf("gr1","gr2"),
            termsAccepted = true,
            carrierId = 33
        )

        fun getReader(filename: String) =
            InputStreamReader(FileInputStream(File(DryRunTest::class.java.classLoader.getResource(filename).path)))
    }
}
