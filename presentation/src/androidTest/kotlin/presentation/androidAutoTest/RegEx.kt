package presentation.androidAutoTest

import com.agoda.kakao.screen.Screen

class RegEx {

    fun parse(inputTextView: CharSequence, regEx: String, idle: Long): String? {
        Screen.idle(idle)
        val regexCode = Regex(regEx)
        val matcherCode = regexCode.find(inputTextView)
        return matcherCode?.value
    }

    fun checkDistanceHasValue(inputDistance: String, regEx: String, idle: Long) {
        val distanse = parse(inputDistance, regEx, idle)?.toInt()
        print(distanse)
    }

    fun checkDistanceHasUnit(inputDistance: String, inputDistanceUnit: Array<String>, regEx: String, idle: Long) {
        val distance = parse(inputDistance, regEx, idle)
        val distanceUnit = distance in inputDistanceUnit
        print(distanceUnit)
        check(distanceUnit)
    }
}
