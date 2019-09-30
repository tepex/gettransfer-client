package presentation.screenelements

import com.agoda.kakao.picker.date.KDatePickerDialog
import com.agoda.kakao.picker.time.KTimePickerDialog

import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R

object Calendar : Screen<Calendar>() {
    val transferDate = KTextView { withId(R.id.transfer_date_time_field) }
    val datePickerDialog = KDatePickerDialog()
    val timePickerDialog = KTimePickerDialog()
}
