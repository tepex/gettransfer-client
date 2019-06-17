package com.kg.gettransfer.extensions

import android.os.Bundle

fun Bundle.getIntOrNull(key: String) =
        this.getInt(key, -666)
                .let {
                    if (it == -666) null
                    else it
                }