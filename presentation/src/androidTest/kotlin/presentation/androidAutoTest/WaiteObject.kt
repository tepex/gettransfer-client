package presentation.androidAutoTest

import android.view.View

import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction

import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId

import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables

import java.util.concurrent.TimeoutException

internal object WaiteObject {

    fun waitId(viewId: Int, millis: Long) = object : ViewAction {

        override fun getConstraints() = isRoot()

        override fun getDescription() = "wait for a specific view with id <$viewId> during $millis millis."

        override fun perform(uiController: UiController, view: View) {
            uiController.loopMainThreadUntilIdle()
            val startTime = System.currentTimeMillis()
            val endTime = startTime + millis
            val viewMatcher = withId(viewId)

            while (System.currentTimeMillis() < endTime) {
                TreeIterables.breadthFirstViewTraversal(view).forEach { child ->
                    if (viewMatcher.matches(child)) {
                        return
                    }
                }

                @Suppress("MagicNumber")
                uiController.loopMainThreadForAtLeast(50)
            }

            throw PerformException.Builder()
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(TimeoutException())
                .build()
        }
    }
}
