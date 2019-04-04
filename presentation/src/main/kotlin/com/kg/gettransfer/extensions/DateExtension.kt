package com.kg.gettransfer.extensions

import com.kg.gettransfer.presentation.ui.SystemUtils
import java.util.*

fun Date.simpleFormat(): String = SystemUtils.formatDateTime(this)