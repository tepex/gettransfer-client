package com.kg.gettransfer.extensions

import com.kg.gettransfer.presentation.ui.Utils
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

fun PhoneNumberUtil.internationalExample(country: String): String =
        Utils.phoneUtil.format(Utils.phoneUtil.getExampleNumber(country),
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)