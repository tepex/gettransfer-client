package com.kg.gettransfer.extensions

import com.kg.gettransfer.presentation.ui.Utils
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import java.util.Locale

fun PhoneNumberUtil.internationalExample(countryCode: String): String =
    Utils.phoneUtil.format(getNumber(countryCode.toUpperCase(Locale.US)),
        PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)

fun getNumber(countryCode: String): Phonenumber.PhoneNumber {
    return Utils.phoneUtil.getExampleNumber(countryCode)
        ?: Utils.phoneUtil.getExampleNumberForNonGeoEntity(
            getCountryCode(countryCode)
        )
}

fun getCountryCode(countryCode: String) =
    Utils.phoneUtil.getCountryCodeForRegion(countryCode)
