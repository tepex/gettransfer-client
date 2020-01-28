package com.kg.gettransfer.prefs

interface EncryptPass {
    fun encrypt(input: String): String
    fun decrypt(input: String): String
}
