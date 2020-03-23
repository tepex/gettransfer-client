package presentation.androidAutoTest

import android.icu.text.SimpleDateFormat
import com.kg.gettransfer.presentation.data.Constants.four
import com.kg.gettransfer.presentation.data.Constants.oneThousand
import com.kg.gettransfer.presentation.data.Constants.thirtyOne
import com.kg.gettransfer.presentation.data.Constants.dayInUnix
import java.util.Date

class DateHelper {
    val currentUnixTime = getCurrentUnixDateTime().toString()

    val secInOneDay = dayInUnix

    val fourDayFromCurrentUnixTime = (getCurrentUnixDateTime() + four * secInOneDay).toString()
    val oneMonthFromCurrentUnixTime = (getCurrentUnixDateTime() + thirtyOne * secInOneDay).toString()

    val dateFormattedYear = formatToDate(currentUnixTime, "yyyy")?.toInt()
    val dateFormattedMonth = formatToDate(oneMonthFromCurrentUnixTime, "MM")?.toInt()
    val dateFormatted4DayForward = formatToDate(fourDayFromCurrentUnixTime, "dd")?.toInt()

    fun getCurrentUnixDateTime(): Long {
        val currentDate = Date()
        return currentDate.time / oneThousand
    }

    private fun formatToDate(fromUnixtime: String, formatter: String): String? {
            val sdf = SimpleDateFormat(formatter)
            val netDate = Date(fromUnixtime.toLong() * oneThousand)
            return sdf.format(netDate)
    }
}
