package com.kg.gettransfer.utilities

import android.text.Editable
import android.text.TextUtils
import com.checkout.android_sdk.Utils.AfterTextChangedListener
import java.util.Calendar

class CardDateFormatter : AfterTextChangedListener() {
    override fun afterTextChanged(date: Editable) {
        if (date.isEmpty()) return
        when (date.length) {
            FIRST_MONTH_CHAR_POS  -> checkFirstMonthChar(date)
            SECOND_MONTH_CHAR_POS -> checkSecondMonthChar(date)
            DELIMITER_POS         -> checkDelimiter(date)
            FIRST_YEAR_CHAR_POS   -> checkFirstYearChar(date)
            SECOND_YEAR_CHAR_POS  -> checkSecondYearChar(date)
        }
    }

    private fun checkFirstMonthChar(date: Editable) {
        val firstMonthChar = date[FIRST_MONTH_CHAR_INDEX]
        if (isFirstMonthCharInvalid(firstMonthChar)) deleteLastChar(date)
    }

    private fun isFirstMonthCharInvalid(firstMonthChar: Char) = !FIRST_MONTH_CHARS.contains(firstMonthChar)

    private fun checkSecondMonthChar(date: Editable) {
        val firstMonthChar = date[FIRST_MONTH_CHAR_INDEX]
        val secondMonthChar = date[SECOND_MONTH_CHAR_INDEX]
        if (isSecondMonthCharInvalid(firstMonthChar, secondMonthChar)) deleteLastChar(date)
    }

    private fun isSecondMonthCharInvalid(firstMonthChar: Char, secondMonthChar: Char) =
        when (firstMonthChar) {
            CHAR_ZERO -> secondMonthChar == CHAR_ZERO
            CHAR_ONE  -> !SECOND_CHARS_TO_DECIMAL_MONTH.contains(secondMonthChar)
            else      -> false
        }

    private fun checkDelimiter(date: Editable) {
        val c = date[date.length - 1]
        if (c == DELIMITER.first()) {
            deleteLastChar(date)
        }
        if (Character.isDigit(c) && TextUtils.split(date.toString(), DELIMITER).size <= 2) {
            date.insert(date.length - 1, DELIMITER)
        }
    }

    private fun checkFirstYearChar(date: Editable) {
        val firstYearChar = date[FIRST_YEAR_CHAR_INDEX]
        if (isFirstYearCharInvalid(firstYearChar)) deleteLastChar(date)
    }

    private fun isFirstYearCharInvalid(firstYearChar: Char): Boolean {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return firstYearChar.toString().toInt() < currentYear / DECIMAL % DECIMAL
    }

    private fun checkSecondYearChar(date: Editable) {
        val year = date.substring(FIRST_YEAR_CHAR_INDEX, date.length)
        if (isSecondYearCharInvalid(year)) deleteLastChar(date)
    }

    private fun isSecondYearCharInvalid(year: String): Boolean {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return year.toInt() < currentYear % YEARS_IN_CENTURY
    }

    private fun deleteLastChar(date: Editable) {
        date.delete(date.length - 1, date.length)
    }

    companion object {
        const val FIRST_MONTH_CHAR_POS  = 1
        const val SECOND_MONTH_CHAR_POS = 2
        const val DELIMITER_POS         = 3
        const val FIRST_YEAR_CHAR_POS   = 4
        const val SECOND_YEAR_CHAR_POS  = 5

        const val FIRST_MONTH_CHAR_INDEX  = 0
        const val SECOND_MONTH_CHAR_INDEX = 1
        const val FIRST_YEAR_CHAR_INDEX   = 3

        const val DELIMITER = "/"
        const val CHAR_ZERO = '0'
        const val CHAR_ONE  = '1'
        private const val CHAR_TWO = '2'

        val FIRST_MONTH_CHARS = arrayOf(CHAR_ZERO, CHAR_ONE)
        val SECOND_CHARS_TO_DECIMAL_MONTH = arrayOf(CHAR_ZERO, CHAR_ONE, CHAR_TWO)

        const val YEARS_IN_CENTURY = 100
        const val DECIMAL = 10
    }
}
