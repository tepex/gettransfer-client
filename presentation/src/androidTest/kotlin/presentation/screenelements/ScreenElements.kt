package com.kg.gettransfer.presentation.screenelements

import android.view.View

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*

import com.kg.gettransfer.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher

data class Onboarding(
    val btnNext: ViewInteraction = onView(withId(R.id.btnNext))
)

data class NavBar(
    val settingsItem: ViewInteraction = onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_settings))),
    val orderItem: ViewInteraction = onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_order)))
)

data class SettingsScreen(
    val profileCell: ViewInteraction = onView(Matchers.allOf(ViewMatchers.withId(R.id.settingsProfile)))
)

data class OrderScreen(
    val searchFrom: ViewInteraction =
        onView(Matchers.allOf(ViewMatchers.withId(R.id.sub_title), withText("Pickup location"), isDisplayed()))
)

data class Profile(
    val logout: ViewInteraction = onView(withId(R.id.btnLogout)),
    val login: ViewInteraction = onView(Matchers.allOf(ViewMatchers.withId(R.id.btnLogin))),
    val email: ViewInteraction =
        onView(allOf(withId(R.id.fieldText), isDescendantOfA(withId(R.id.fieldLayout)), isDisplayed())),
    val pwd: ViewInteraction = onView(allOf(withId(R.id.etPassword))),
    val btnBack: ViewInteraction = onView(Matchers.allOf(withContentDescription("Navigate up"), isDisplayed()))
)

data class SearchForm(
    val addressFrom: ViewInteraction = onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.addressField),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.searchFrom))
        )
    ),
    val mskAddressItem: ViewInteraction = onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.addressItem),
            ViewMatchers.withText("Moscow, Russia"),
            ViewMatchers.isDisplayed()
        )
    ),
    val addressTo: ViewInteraction = onView(
        Matchers.allOf(
            ViewMatchers.withId(R.id.addressField),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.searchTo))
        )
    ),
    val spbAddressItem: ViewInteraction =
        onView(allOf(withId(R.id.addressItem), withText("Saint Petersburg, Russia"), isDisplayed()))
)

data class OrdersDetails(
    val content: ViewInteraction = onView(withId(R.id.rvTransferType)),
    val plusPassenger: ViewInteraction = onView(withId(R.id.img_plus_seat)),
    val btnGetOffers: ViewInteraction = onView(withId(R.id.btnGetOffers)),
    val transportType: ViewInteraction = onView(withId(R.id.rvTransferType)),
    val bottomContent: ViewInteraction = onView(withId(R.id.scrollContent)),
    val termsOfUse: ViewInteraction = onView(withId(R.id.switchAgreement))
)

data class DialogWindow(
    val btnOk: ViewInteraction = onView(Matchers.allOf(ViewMatchers.withId(android.R.id.button1), withText("OK"))),
    val msgNoTransport: ViewInteraction = onView(withText("Please select transport type")),
    val msgNoTerms: ViewInteraction = onView(withText("You should accept terms of use"))
)

data class SatisfactionBox(
    val satisfaction: ViewInteraction = onView(withId(R.id.design_bottom_sheet)),
    val btnClose: ViewInteraction = onView(withId(R.id.ivClose))
)

data class Calendar(
    val btnOk: ViewInteraction = onView(withId(android.R.id.button1)),
    val transferDate: ViewInteraction = onView(withId(R.id.transfer_date_time_field))
)

data class OffersScreen(
    val topOffer: ViewInteraction = onView(withIndex(withId(R.id.tv_car_model_tiny), 0)),
    val secondOffer: ViewInteraction = onView(withIndex(withId(R.id.tv_car_model_tiny), 1)),
    val thirdOffer: ViewInteraction = onView(withIndex(withId(R.id.tv_car_model_tiny), 2)),
    val btnBook: ViewInteraction = onView(withId(R.id.btn_book)),
    val btnBack: ViewInteraction = onView(withId(R.id.btnBack))
)

fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        internal var currentIndex = 0

        override fun describeTo(description: Description) {
            description.appendText("with index: ")
            description.appendValue(index)
            matcher.describeTo(description)
        }

        public override fun matchesSafely(view: View) = matcher.matches(view) && currentIndex++ == index
    }
}
