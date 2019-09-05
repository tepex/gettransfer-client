package com.kg.gettransfer.presentation.screenelements

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.kg.gettransfer.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher

 class Onboarding {
    val btnnext = onView(withId(R.id.btnNext))
//    val btnclose
}
class NavBar {
    val settingsItem =  onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_settings)))
    val orderItem = onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_order)))
}

class SettingsScreen {
    val profileCell =   onView(Matchers.allOf(ViewMatchers.withId(R.id.settingsProfile)))

}
class  OrderScreen{
    val searchFrom = onView(Matchers.allOf(ViewMatchers.withId(R.id.sub_title),withText("Pickup location"),isDisplayed()))
}
class Profile{
    val logout = onView(withId(R.id.btnLogout))
    val login =  onView(Matchers.allOf(ViewMatchers.withId(R.id.btnLogin)))
    val email = onView(allOf(withId(R.id.fieldText), isDescendantOfA(withId(R.id.fieldLayout)), isDisplayed()))
    val pwd = onView(allOf(withId(R.id.etPassword)))
    val btnback = onView(Matchers.allOf(withContentDescription("Navigate up"), isDisplayed()))
}
class SearchForm {

    val addressFrom = onView(Matchers.allOf(ViewMatchers.withId(R.id.addressField), ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.searchFrom))))
    val mskAddressItem =   onView(Matchers.allOf(ViewMatchers.withId(R.id.addressItem), ViewMatchers.withText("Moscow, Russia"), ViewMatchers.isDisplayed()))
    val addressTo = onView(Matchers.allOf(ViewMatchers.withId(R.id.addressField), ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.searchTo))))
    val spbAddressItem =   onView(allOf(withId(R.id.addressItem),withText("Saint Petersburg, Russia"),isDisplayed()))
}
class OrdersDetails {
    val content = onView(withId(R.id.rvTransferType))
    val pluspassenger = onView(withId(R.id.img_plus_seat))
    val btngetoffers = onView(withId(R.id.btnGetOffers))
    val transporttype = onView(withId(R.id.rvTransferType))
    val bottomcontent = onView(withId(R.id.scrollContent))
    val termsofuse = onView(withId(R.id.switchAgreement))
}
class DialogWindow{
    val okbtn = onView(Matchers.allOf(ViewMatchers.withId(android.R.id.button1), withText("OK")))
    val msgnotransport = onView(withText("Please select transport type"))
    val msgnoterms = onView(withText("You should accept terms of use"))
}
class SatisfactionBox {
    val satisfaction = onView(withId(R.id.design_bottom_sheet))
    val closebtn =  onView(withId(R.id.ivClose))
}

class Calendar {
    val okButton = onView(withId(android.R.id.button1))
    val transferdate = onView(withId(R.id.transfer_date_time_field))
}
class  OffersScreen {
    val topoffer = onView(withIndex(withId(R.id.tv_car_model_tiny), 0))
    val secondoffer = onView(withIndex(withId(R.id.tv_car_model_tiny), 1))
    val thirdoffer = onView(withIndex(withId(R.id.tv_car_model_tiny), 2))
    val btnbook = onView(withId(R.id.btn_book))
    val btnback = onView(withId(R.id.btnBack))
}

fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        internal var currentIndex = 0

        override fun describeTo(description: Description) {
            description.appendText("with index: ")
            description.appendValue(index)
            matcher.describeTo(description)
        }

        public override fun matchesSafely(view: View): Boolean {
            return matcher.matches(view) && currentIndex++ == index
        }
    }
}


