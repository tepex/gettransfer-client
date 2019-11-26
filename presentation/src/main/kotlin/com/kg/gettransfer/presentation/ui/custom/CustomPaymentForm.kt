package com.kg.gettransfer.presentation.ui.custom

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager

import com.android.volley.VolleyError
import com.checkout.android_sdk.Models.BillingModel
import com.checkout.android_sdk.Models.PhoneModel
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.Store.DataStore
import com.checkout.android_sdk.Utils.CardUtils
import com.checkout.android_sdk.Utils.CustomAdapter
import com.checkout.android_sdk.Utils.Environment
import com.checkout.android_sdk.Utils.PhoneUtils
import com.checkout.android_sdk.View.BillingDetailsView
import com.checkout.android_sdk.View.CardDetailsView
import com.kg.gettransfer.R

import java.util.Locale

/**
 * Contains helper methods dealing with the tokenisation or payment from customisation
 *
 *
 * Most of the methods that include interaction with the Checkout.com API rely on
 * callbacks to communicate outcomes. Please make sure you set the key/environment
 * and appropriate  callbacks to a ensure successful interaction
 */
class CustomPaymentForm @JvmOverloads constructor(
    val mContext: Context,
    val attrs: AttributeSet? = null
) : FrameLayout(mContext, attrs) {

    /**
     * This is a callback used to generate a payload with the user details and pass them to the
     * mSubmitFormListener so the user can act upon them. The next step will most likely include using
     * this payload to generate a token in  the CheckoutAPIClient
     */
    private val mDetailsCompletedListener = object : CardDetailsView.DetailsCompleted {
        override fun onFormSubmit() {
            mSubmitFormListener?.onFormSubmit()
        }

        override fun onTokeGenerated(reponse: CardTokenisationResponse) {
            mSubmitFormListener?.onTokenGenerated(reponse)
        }

        override fun onError(error: CardTokenisationFail) {
            mSubmitFormListener?.onError(error)
        }

        override fun onNetworkError(error: VolleyError) {
            mSubmitFormListener?.onNetworkError(error)
        }

        override fun onBackPressed() {
            mDataStore?.cleanState()
            mDataStore.lastCustomerNameState = null
            mDataStore.lastBillingValidState = null
            customAdapter?.clearFields()
            mSubmitFormListener?.onBackPressed()
        }
    }

    /**
     * This is a callback used to go back to the card details view from the billing page
     * and based on the action used decide is the billing spinner will be updated
     */
    private val mBillingListener = object : BillingDetailsView.Listener {
        override fun onBillingCompleted() {
            customAdapter?.updateBillingSpinner()
            mPager?.currentItem = CARD_DETAILS_PAGE_INDEX
        }

        override fun onBillingCanceled() {
            customAdapter?.clearBillingSpinner()
            mPager?.currentItem = CARD_DETAILS_PAGE_INDEX
        }
    }

    /**
     * This is a callback used to navigate to the billing details page
     */
    private val mCardListener = CardDetailsView.GoToBillingListener { mPager?.currentItem = BILLING_DETAILS_PAGE_INDEX }
    var m3DSecureListener: On3DSFinished? = null
    var mSubmitFormListener: PaymentFormCallback? = null

    private var customAdapter: CustomAdapter? = null
    private var mPager: ViewPager? = null
    private val mDataStore = DataStore.getInstance()

    /**
     * This is interface used as a callback for when the 3D secure functionality is used
     */
    interface On3DSFinished {
        fun onSuccess()

        fun onError()
    }

    /**
     * This is interface used as a callback for when the form is completed and the user pressed the
     * pay button. You can use this to potentially display a loader.
     */
    interface PaymentFormCallback {
        fun onFormSubmit()
        fun onTokenGenerated(response: CardTokenisationResponse)
        fun onError(response: CardTokenisationFail)
        fun onNetworkError(error: VolleyError)
        fun onBackPressed()
    }

    /**
     * This method is used to initialise the UI of the module
     */
    init {
        // Set up the layout
        View.inflate(mContext, R.layout.payment_form, this)

        mPager = findViewById(R.id.view_pager)
        // Use a custom adapter for the viewpager
        customAdapter = CustomAdapter(mContext)
        // Set up the callbacks
        customAdapter?.setCardDetailsListener(mCardListener)
        customAdapter?.setBillingListener(mBillingListener)
        customAdapter?.setTokenDetailsCompletedListener(mDetailsCompletedListener)
        mPager?.adapter = customAdapter
        mPager?.isEnabled = false
    }

    /**
     * This method is used set the accepted card schemes
     *
     * @param cards array of accepted cards
     */
    fun setAcceptedCard(cards: Array<CardUtils.Cards>): CustomPaymentForm {
        mDataStore?.acceptedCards = cards
        return this
    }

    /**
     * This method is used to handle 3D Secure URLs.
     *
     *
     * It wil programmatically generate a WebView and listen for when the url changes
     * in either the success url or the fail url.
     *
     * @param url        the 3D Secure url
     * @param successUrl the Redirection url set up in the Checkout.com HUB
     * @param failsUrl   the Redirection Fail url set up in the Checkout.com HUB
     */
    fun handle3DS(url: String, successUrl: String, failsUrl: String) {
        mPager?.visibility = View.GONE // dismiss the card form UI

        val web = WebView(mContext)
        web.loadUrl(url)
        web.settings.javaScriptEnabled = true
        web.webViewClient = object : WebViewClient() {

            // Listen for when teh URL changes and match t with either the success of fail url
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (view?.url == null) return true
                request?.url?.path?.let {
                    handleUri(it, successUrl, failsUrl)
                }
                return false
            }

            // for pre-lollipop
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (view == null || url == null) return true
                Uri.parse(url).path?.let {
                    handleUri(it, successUrl, failsUrl)
                }
                return false
            }
        }
        // Make WebView fill the layout
        web.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(web)
    }

    private fun handleUri(path: String, successUrl: String, failsUrl: String) {
        if (path == successUrl) m3DSecureListener?.onSuccess()
        else if (path == failsUrl) m3DSecureListener?.onError()
    }

    /**
     * This method used to decide if the billing details option will be
     * displayed in the payment form.
     *
     * @param include boolean showing if the billing should be used
     */
    fun includeBilling(include: Boolean) {
        if (!include) {
            mDataStore?.setShowBilling(false)
        } else {
            mDataStore?.setShowBilling(true)
        }
    }

    /**
     * This method used to set a default country for the country
     *
     * @param country Locale representing the default country for the Spinner
     */
    fun setDefaultBillingCountry(country: Locale): CustomPaymentForm {
        mDataStore?.customerCountry = country.country
        mDataStore.defaultCountry = country
        mDataStore.customerPhonePrefix = PhoneUtils.getPrefix(country.country)
        return this
    }

    /**
     * This method used to set a custom label for the accepted cards
     *
     * @param accepted String representing the value for the Label
     */
    fun setAcceptedCardsLabel(accepted: String): CustomPaymentForm {
        mDataStore?.acceptedLabel = accepted
        return this
    }

    /**
     * This method used to set a custom label for the CardInput
     *
     * @param card String representing the value for the Label
     */
    fun setCardLabel(card: String): CustomPaymentForm {
        mDataStore?.cardLabel = card
        return this
    }

    /**
     * This method used to set a custom label for the DateInput
     *
     * @param date String representing the value for the Label
     */
    fun setDateLabel(date: String): CustomPaymentForm {
        mDataStore?.dateLabel = date
        return this
    }

    /**
     * This method used to set a custom label for the CvvInput
     *
     * @param cvv String representing the value for the Label
     */
    fun setCvvLabel(cvv: String): CustomPaymentForm {
        mDataStore?.cvvLabel = cvv
        return this
    }

    /**
     * This method used to set a custom label for the CardholderInput
     *
     * @param label String representing the value for the Label
     */
    fun setCardHolderLabel(label: String): CustomPaymentForm {
        mDataStore?.cardHolderLabel = label
        return this
    }

    /**
     * This method used to set a custom label for the AddressLine 1 Input
     *
     * @param label String representing the value for the Label
     */
    fun setAddress1Label(label: String): CustomPaymentForm {
        mDataStore?.addressLine1Label = label
        return this
    }

    /**
     * This method used to set a custom label for the AddressLine 2 Input
     *
     * @param label String representing the value for the Label
     */
    fun setAddress2Label(label: String): CustomPaymentForm {
        mDataStore?.addressLine2Label = label
        return this
    }

    /**
     * This method used to set a custom label for the StateInput
     *
     * @param label String representing the value for the Label
     */
    fun setTownLabel(label: String): CustomPaymentForm {
        mDataStore?.townLabel = label
        return this
    }

    /**
     * This method used to set a custom label for the StateInput
     *
     * @param label String representing the value for the Label
     */
    fun setStateLabel(label: String): CustomPaymentForm {
        mDataStore?.stateLabel = label
        return this
    }

    /**
     * This method used to set a custom label for the PostcodeInput
     *
     * @param label String representing the value for the Label
     */
    fun setPostcodeLabel(label: String): CustomPaymentForm {
        mDataStore?.postCodeLabel = label
        return this
    }

    /**
     * This method used to set a custom label for the PhoneInput
     *
     * @param label String representing the value for the Label
     */
    fun setPhoneLabel(label: String): CustomPaymentForm {
        mDataStore?.phoneLabel = label
        return this
    }

    /**
     * This method used to set a custom text for the Pay button
     *
     * @param text String representing the text for the Button
     */
    fun setPayButtonText(text: String): CustomPaymentForm {
        mDataStore?.payButtonText = text
        return this
    }

    /**
     * This method used to set a custom text for the Done button
     *
     * @param text String representing the text for the Button
     */
    fun setDoneButtonText(text: String): CustomPaymentForm {
        mDataStore?.doneButtonText = text
        return this
    }

    /**
     * This method used to set a custom text for the Clear button
     *
     * @param text String representing the text for the Button
     */
    fun setClearButtonText(text: String): CustomPaymentForm {
        mDataStore?.clearButtonText = text
        return this
    }

    /**
     * This method used to set a custom LayoutParameters for the Pay button
     *
     * @param layout LayoutParameters representing the style for the Button
     */
    fun setPayButtonLayout(layout: LinearLayout.LayoutParams): CustomPaymentForm {
        mDataStore?.payButtonLayout = layout
        return this
    }

    /**
     * This method used to set a custom LayoutParameters for the Done button
     *
     * @param layout LayoutParameters representing the style for the Button
     */
    fun setDoneButtonLayout(layout: LinearLayout.LayoutParams): CustomPaymentForm {
        mDataStore?.doneButtonLayout = layout
        return this
    }

    /**
     * This method used to set a custom LayoutParameters for the Clear button
     *
     * @param layout LayoutParameters representing the style for the Button
     */
    fun setClearButtonLayout(layout: LinearLayout.LayoutParams): CustomPaymentForm {
        mDataStore?.clearButtonLayout = layout
        return this
    }

    /**
     * This method used to inject address details if they have already been collected
     *
     * @param billing BillingModel representing the value for the billing details
     */
    fun injectBilling(billing: BillingModel): CustomPaymentForm {
        mDataStore?.isBillingCompleted = true
        mDataStore.lastBillingValidState = billing
        mDataStore.defaultBillingDetails = billing
        mDataStore.customerAddress1 = billing.address_line1
        mDataStore.customerAddress2 = billing.address_line2
        mDataStore.customerZipcode = billing.zip
        mDataStore.customerCountry = billing.country
        mDataStore.customerCity = billing.city
        mDataStore.customerState = billing.state
        return this
    }

    /**
     * This method used to inject phone details if they have already been collected
     *
     * @param phone PhoneModel representing the value for the phone details
     */
    fun injectPhone(phone: PhoneModel): CustomPaymentForm {
        mDataStore?.customerPhone = phone.number
        mDataStore.customerPhonePrefix = phone.country_code
        mDataStore.defaultPhoneDetails = phone
        return this
    }

    fun setEnvironment(env: Environment): CustomPaymentForm {
        mDataStore?.environment = env
        return this
    }

    fun setKey(key: String): CustomPaymentForm {
        mDataStore?.key = key
        return this
    }

    /**
     * This method used to inject the cardholder name if it has already been collected
     *
     * @param name String representing the value for the cardholder name
     */
    fun injectCardHolderName(name: String): CustomPaymentForm {
        mDataStore?.customerName = name
        mDataStore.defaultCustomerName = name
        mDataStore.lastCustomerNameState = name
        return this
    }

    /**
     * This method used to clear the state and fields of the Payment Form
     */
    fun clearForm() {
        mDataStore?.cleanState()
        if (mDataStore != null && mDataStore.defaultBillingDetails != null) {
            mDataStore.isBillingCompleted = true
            mDataStore.lastBillingValidState = mDataStore.defaultBillingDetails
            mDataStore.customerAddress1 = mDataStore.defaultBillingDetails.address_line1
            mDataStore.customerAddress2 = mDataStore.defaultBillingDetails.address_line2
            mDataStore.customerZipcode = mDataStore.defaultBillingDetails.zip
            mDataStore.customerCountry = mDataStore.defaultBillingDetails.country
            mDataStore.customerCity = mDataStore.defaultBillingDetails.city
            mDataStore.customerState = mDataStore.defaultBillingDetails.state
            mDataStore.customerPhone = mDataStore.defaultPhoneDetails.number
            mDataStore.customerPhonePrefix = mDataStore.defaultPhoneDetails.country_code
        }
        if (mDataStore != null && mDataStore.defaultCustomerName != null) {
            mDataStore.customerName = mDataStore.defaultCustomerName
        } else {
            mDataStore.lastCustomerNameState = null
        }
        if (mDataStore != null && mDataStore.defaultCountry != null) {
            mDataStore.defaultCountry = mDataStore.defaultCountry
        }
        customAdapter?.clearFields()
        if (mDataStore != null && mDataStore.defaultBillingDetails != null) {
            mDataStore.isBillingCompleted = true
            mDataStore.lastBillingValidState = mDataStore.defaultBillingDetails
        } else {
            mDataStore.lastBillingValidState = null
        }
    }

    /**
     * Returns a String without any spaces
     *
     *
     * This method used to take a card number input String and return a
     * String that simply removed all whitespace, keeping only digits.
     *
     * @param entry the String value of a card number
     */
    private fun sanitizeEntry(entry: String): String {
        return entry.replace("\\D".toRegex(), "")
    }

    /**
     * This method used to set a callback for when the 3D Secure handling.
     */
    fun set3DSListener(listener: On3DSFinished): CustomPaymentForm {
        this.m3DSecureListener = listener
        return this
    }

    /**
     * This method used to set a callback for when the form is submitted
     */
    fun setFormListener(listener: PaymentFormCallback): CustomPaymentForm {
        this.mSubmitFormListener = listener
        return this
    }

    companion object {

        // Indexes for the pages
        private const val CARD_DETAILS_PAGE_INDEX = 0
        private const val BILLING_DETAILS_PAGE_INDEX = 1
    }

}
/**
 * This is the constructor used when the module is used without the UI.
 */
