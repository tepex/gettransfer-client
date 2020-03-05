package presentation.androidAutoTest

import android.view.View
import android.widget.TextView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions

import com.agoda.kakao.text.KTextView

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class Capture<T : View>(val klass: Class<T>) : TypeSafeMatcher<View>(View::class.java) {
    var view: T? = null

    override fun describeTo(desc: Description) {
    }
    override fun matchesSafely(v: View): Boolean {
        if (!klass.isAssignableFrom(v.javaClass)) {
            return false
        }
        this.view = v as T
        return true
    }
    companion object {
        inline operator fun <reified T : View> invoke() = Capture(T::class.java)
        fun getText(textView: KTextView): String {
            return getText(textView.view.interaction)
        }

        fun getText(view: ViewInteraction): String {
            val capture = Capture<TextView>()
            view.check(ViewAssertions.matches(capture))
            return capture.view?.text?.toString() ?: ""
        }
    }
}
