package presentation.screenelements

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription

import com.kg.gettransfer.R

import org.hamcrest.*
import org.hamcrest.Matchers.allOf

class Onboarding {
    val btnNext = onView(withId(R.id.btnNext))
}

class NavBar {
    val settingsItem = onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_settings)))
    val orderItem = onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_order)))
    val tripsItem = onView(allOf(withId(R.id.nav_trips)))
}

class TripsScreen {
    val upcomingTripCell = onView(allOf(withIndex(withId(R.id.requestInfo), 0), isDisplayed()))
    val nxt = onView(
        Matchers.allOf(withId(R.id.chevron), isDescendantOfA(withIndex(withId(R.id.requestInfo), 0)), isDisplayed())
    )
    val past = onView(Matchers.allOf(withContentDescription("Past"), isDisplayed()))
    val btnInfo = onView(allOf(withId(R.id.btn_request_info)))
    val content = onView(Matchers.allOf(withId(R.id.transfer_details_header)))
    val btnBack = onView(allOf(withId(R.id.btnBack), isDisplayed()))
    val btnBackToTrips = onView(Matchers.allOf(withId(R.id.btn_back), isDisplayed()))
    val distanceOnTransferInfo = onView(CoreMatchers.allOf(withId(R.id.tv_distance),  isDisplayed()))
    val contentList = onView(Matchers.allOf(withId(R.id.vpRequests)))
    val distance: ViewInteraction
        get() = onView(
            CoreMatchers.allOf(
                withIndex(withId(R.id.tvDistance), 0),
                isDescendantOfA(withIndex(withId(R.id.requestInfo), 0)),
                isDisplayed()
            )
        )
}

class SettingsScreen {
    val profileCell = onView(Matchers.allOf(ViewMatchers.withId(R.id.settingsProfile)))
    val distanceSwitcher = onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.switch_button),
            isDescendantOfA(ViewMatchers.withId(R.id.settingsDistanceUnit)),
            isDisplayed()
        )
    )
}

class OrderScreen {
    val searchFrom =
        onView(Matchers.allOf(ViewMatchers.withId(R.id.sub_title), withText("Pickup location"), isDisplayed()))
}

class Profile {
    val logout = onView(withId(R.id.btnLogout))
    val login = onView(Matchers.allOf(ViewMatchers.withId(R.id.btnLogin)))
    val email = onView(allOf(withId(R.id.fieldText), isDescendantOfA(withId(R.id.fieldLayout)), isDisplayed()))
    val pwd = onView(allOf(withId(R.id.etPassword)))
    val btnBack = onView(Matchers.allOf(withContentDescription("Navigate up"), isDisplayed()))
}

class SearchForm {
    val addressFrom = onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.addressField),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.searchFrom))
        )
    )
    val mskAddressItem: ViewInteraction = onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.addressItem),
            ViewMatchers.withText("Moscow, Russia"),
            ViewMatchers.isDisplayed())
    )
    val addressTo = onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.addressField),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.searchTo))
        )
    )
    val spbAddressItem = onView(allOf(withId(R.id.addressItem), withText("Saint Petersburg, Russia"), isDisplayed()))
}

class OrdersDetails {
    val content = onView(withId(R.id.rvTransferType))
    val plusPassenger = onView(withId(R.id.img_plus_seat))
    val btnGetOffers = onView(withId(R.id.btnGetOffers))
    val transportType = onView(withId(R.id.rvTransferType))
    val bottomContent = onView(withId(R.id.scrollContent))
    val termsOfUse = onView(withId(R.id.switchAgreement))
    val btnBack = onView(Matchers.allOf(withId(R.id.btnBack), isDisplayed()))
}

class DialogWindow {
    val btnOk = onView(Matchers.allOf(ViewMatchers.withId(android.R.id.button1), withText("OK")))
    val msgNoTransport = onView(withText("Please select transport type"))
    val msgNoTerms = onView(withText("You should accept terms of use"))
}

class SatisfactionBox {
    val satisfaction = onView(withId(R.id.design_bottom_sheet))
    val btnClose = onView(withId(R.id.ivClose))
}

class Calendar {
    val btnOk = onView(withId(android.R.id.button1))
    val transferDate = onView(withId(R.id.transfer_date_time_field))
}

class OffersScreen {
    val topOffer = onView(withIndex(withId(R.id.tv_car_model_tiny), 0))
    val secondOffer = onView(withIndex(withId(R.id.tv_car_model_tiny), 1))
    val thirdoffer = onView(withIndex(withId(R.id.tv_car_model_tiny), 2))
    val btnBook = onView(withId(R.id.btn_book))
    val btnBack = onView(withId(R.id.btnBack))
}

fun withIndex(matcher: Matcher<View>, index: Int) = object : TypeSafeMatcher<View>() {
    internal var currentIndex = 0

    override fun describeTo(description: Description) {
        description.appendText("with index: ")
        description.appendValue(index)
        matcher.describeTo(description)
    }

    public override fun matchesSafely(view: View) = matcher.matches(view) && currentIndex++ == index
}

fun ViewInteraction.waitElementClick(wait: Long) {
    Thread.sleep(wait)
    perform(ViewActions.click())
}

fun waitElementClick(whatToClick: ViewInteraction, wait: Long ) {
    Thread.sleep(wait)

    whatToClick.perform(ViewActions.click())
}
