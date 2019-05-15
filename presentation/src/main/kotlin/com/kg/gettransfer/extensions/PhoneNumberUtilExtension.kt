package com.kg.gettransfer.extensions

import com.kg.gettransfer.presentation.ui.Utils
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import java.util.*

fun PhoneNumberUtil.internationalExample(country: Locale): String =
        Utils.phoneUtil.format(getNumber(country),
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)

fun getNumber(country: Locale): Phonenumber.PhoneNumber =
        Utils.phoneUtil.getExampleNumber(country.country)
                ?: Utils.phoneUtil.getExampleNumberForNonGeoEntity(
                        getCountryCode(country)
                )

fun getCountryCode(locale: Locale) =
        Utils.phoneUtil.getCountryCodeForRegion(locale.country)