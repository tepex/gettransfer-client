package com.kg.gettransfer.presentation

import android.widget.DatePicker
import android.widget.TimePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.contrib.PickerActions
import com.kg.gettransfer.presentation.screenelements.*
import com.kg.gettransfer.presentation.ui.SplashActivity

@RunWith(AndroidJUnit4::class)
class passenger {
    val satisfactionBox = SatisfactionBox()
    val onboarding = Onboarding()
    val navBar = NavBar()
    val settingsScr = SettingsScreen()
    val orderSrc = OrderScreen()
    val searchForm = SearchForm()
    val ordersDetails = OrdersDetails()
    val calendar = Calendar()
    val offersScreen = OffersScreen()
    val profile = Profile()
    val dialogWindow = DialogWindow()
    @Test
    fun passengerCreateTrasfer (){

        //Launch app
        ActivityScenario.launch(SplashActivity::class.java)


        //Assertion onboarging exist
        passOnboardingAnyway()

        Thread.sleep(27000)

        checkform()

        navBar.settingsItem.perform(ViewActions.click())
        settingsScr.profileCell.perform(click())

        //Assertion user exist
        loginAnyway()

        Thread.sleep(1000)

        orderSrc.searchFrom.perform(ViewActions.click())

        Thread.sleep(700)

        searchForm.addressFrom.perform(ViewActions.replaceText("Moscow"), ViewActions.closeSoftKeyboard())

        Thread.sleep(700)

        searchForm.mskAddressItem.perform(click())

        Thread.sleep(700)

        searchForm.addressTo.perform(ViewActions.replaceText("Saint Petersburg"), ViewActions.closeSoftKeyboard())

        Thread.sleep(2000)

        searchForm.spbAddressItem.perform(click())

        Thread.sleep(2000)

        ordersDetails.content.perform(ViewActions.swipeUp())

        Thread.sleep(700)

        //Select date
        calendar.transferdate.perform(ViewActions.click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
        .perform(PickerActions.setDate(2019, 9,15))

        Thread.sleep(700)

        calendar.okButton.perform(click())
        // The end code select date

        Thread.sleep(700)

        // Select time
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
                .perform(PickerActions.setTime(15,15))

        Thread.sleep(700)

        calendar.okButton.perform(click())
        // The end code select time

        Thread.sleep(700)

        ordersDetails.pluspassenger.perform(ViewActions.click())

        Thread.sleep(700)

        ordersDetails.btngetoffers.perform(ViewActions.click())

        //Assertion dialog-window exist
        carTypeEnableAnyway ()

        Thread.sleep(700)

        //Assertion dialog-window exist
        switchEnableAnyway()

        Thread.sleep(700)

        offersScreen.topoffer.perform(click());

        Thread.sleep(700)

        offersScreen.btnbook.perform(ViewActions.click())

        Thread.sleep(700)

        offersScreen.btnback.perform(ViewActions.click())

        Thread.sleep(700)



    }

    fun ViewInteraction.isDisplayed(): Boolean {
        try {
            check(matches(ViewMatchers.isDisplayed()))
            return true
        } catch (e: NoMatchingViewException) {
            return false
        }
    }

    fun passOnboardingAnyway () {
        if(onboarding.btnnext.isDisplayed()) {

            //Go next onboarding
            onboarding.btnnext.perform(ViewActions.click())
            //Click next and go to order's screen
            onboarding.btnnext.perform(ViewActions.click())

        } else {

        }
    }

    fun loginAnyway () {
        if(profile.logout.isDisplayed()) {


            //Go to settings screen
            profile.btnback.perform(ViewActions.click())

            //Go to  order's create
            navBar.orderItem.perform(ViewActions.click())

        } else {
            //Input email
            profile.email.perform(ViewActions.replaceText("mygtracc1@gmail.com"), ViewActions.closeSoftKeyboard())

            Thread.sleep(700)

            //Input password
            profile.pwd.perform(typeText("PassRR11"))
            //Sign in
            profile.login.perform(ViewActions.click())

            Thread.sleep(700)

            //Go to  order's create
            navBar.orderItem.perform(ViewActions.click())

        }
    }

    fun carTypeEnableAnyway () {
        if(dialogWindow.msgnotransport.isDisplayed()) {

            dialogWindow.okbtn.perform(ViewActions.click())

            Thread.sleep(700)

            //Select car
            ordersDetails.transporttype.perform (ViewActions.click())

            Thread.sleep(700)

            //Click btn
            ordersDetails.btngetoffers.perform (ViewActions.click())

            Thread.sleep(700)

        }  else {

        }
    }

    fun switchEnableAnyway() {
           if (dialogWindow.msgnoterms.isDisplayed()) {

            dialogWindow.okbtn.perform(ViewActions.click())

            Thread.sleep(700)

            ordersDetails.bottomcontent.perform(ViewActions.swipeUp())

            Thread.sleep(700)

            //Activate switcher
            ordersDetails.termsofuse.perform(ViewActions.click())

            Thread.sleep(700)

            ordersDetails.btngetoffers.perform (ViewActions.click())

        } else {

        }

    }
    fun checkform() {
        if( satisfactionBox.satisfaction.isDisplayed()){
            satisfactionBox.closebtn.perform(click())
        }
        else{

        }

    }
}
