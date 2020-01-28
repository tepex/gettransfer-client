package com.kg.gettransfer.encrypt

import com.kg.gettransfer.prefs.EncryptPass

class EncryptPassImpl : EncryptPass {

    override fun encrypt(input: String) = encryptDecrypt(input)

    override fun decrypt(input: String) = encryptDecrypt(input)

    private fun encryptDecrypt(input: String): String {
        // Can be any chars, and any length array
        val key = charArrayOf('G', 'T', 'R')
        val output = StringBuilder()

        for (i in 0 until input.length) {
            val a: Int = input[i].toInt()
            val b: Int = key[i % key.size].toInt()
            output.append((a xor b).toChar())
        }
        return output.toString()
    }
}
