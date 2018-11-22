package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel
import com.kg.gettransfer.presentation.presenter.CreateOrderSheetPresenter
import com.kg.gettransfer.presentation.view.CreateOrderSheetView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.bottom_sheet_create_order_new.view.*
import kotlinx.android.synthetic.main.search_address.*
import kotlinx.android.synthetic.main.view_create_order_field.view.*

class CreateOrderSheet @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): RelativeLayout(context, attrs, defStyleAttr), CreateOrderSheetView, LayoutContainer {

    @InjectPresenter
    lateinit var presenter: CreateOrderSheetPresenter
    @ProvidePresenter
    fun createCreateOrderSheetPresenter() = CreateOrderSheetPresenter()

    private lateinit var mActivity: CreateOrderActivity
    var parentDelegate: MvpDelegate<Any>? = null
    private val mvpDelegate by lazy { MvpDelegate<CreateOrderSheet>(this).apply { setParentDelegate(parentDelegate, id.toString()) }}

    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_create_order_new, this, true)
//
//        if(attrs != null) {
//            val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchAddress)
//            addressField.hint = ta.getString(R.styleable.SearchAddress_hint)
//            ta.recycle()
//        }

    }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setFairPrice(price: String?, time: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setUser(user: UserModel, isLoggedIn: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPassengers(count: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setChildren(count: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrency(currency: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDateTimeTransfer(dateTimeString: String, isAfter4Hours: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setComment(comment: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setGetTransferEnabled(enabled: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPromoResult(discountInfo: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resetPromoView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setError(e: ApiException) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}