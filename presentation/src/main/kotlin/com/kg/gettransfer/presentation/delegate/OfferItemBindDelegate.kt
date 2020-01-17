package com.kg.gettransfer.presentation.delegate

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView

import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.kg.gettransfer.R

import androidx.core.view.isVisible
import com.kg.gettransfer.extensions.strikeText

import com.kg.gettransfer.domain.model.Ratings
import com.kg.gettransfer.extensions.toHalfEvenRoundedFloat

import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.getEmptyImageRes
import com.kg.gettransfer.presentation.model.getImageRes
import com.kg.gettransfer.presentation.model.getModelsRes

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer

import kotlinx.android.synthetic.main.offer_tiny.view.*
import kotlinx.android.synthetic.main.vehicle_items.view.*
import kotlinx.android.synthetic.main.view_offer_rating.view.*
import kotlinx.android.synthetic.main.view_transport_capacity.view.*

@Suppress("TooManyFunctions")
object OfferItemBindDelegate {

    private const val RATE_SHOWN = true

    fun bindOfferTiny(view: View, offer: OfferItemModel, isNameSignPresent: Boolean) = when (offer) {
        is OfferModel        -> bindOfferModelTiny(view, offer, isNameSignPresent)
        is BookNowOfferModel -> bindBookNowTiny(view, offer)
    }

    @SuppressLint("SetTextI18n")
    @Suppress("NestedBlockDepth", "ComplexMethod")
    private fun bindOfferModelTiny(view: View, offer: OfferModel, isNameSignPresent: Boolean) = with(view) {
        with(offer.vehicle) {
            tv_car_model_tiny.text = "$model $year"
            tv_car_class_tiny.text = context.getString(transportType.nameId)
            bindCapacity(view_capacity, transportType)

            with(iv_car_color_tiny) {
                if (color != null && photos.isEmpty()) {
                    isVisible = true
                    setImageDrawable(Utils.getCarColorFormRes(view.context, color))
                } else {
                    isVisible = false
                }
            }

            photos.firstOrNull().also { photo ->
                Utils.bindMainOfferPhoto(
                    img_car_photo_tiny,
                    view,
                    path = photo,
                    resource = offer.vehicle.transportType.id.getEmptyImageRes()
                )
            }
        }

        with(offer.carrier) {
            bindRating(view_rating_tiny, ratings, approved)
            bindLanguages(Either.Single(languages_container_tiny), languages,
                layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_ITEM)
        }

        with(offer.price) {
            setStrikePriceText(tv_price_no_discount, withoutDiscount?.preferred ?: withoutDiscount?.def)
            tv_price_final.text = base.preferred ?: base.def
        }
        bindNameSignPlate(context, imgNameSign, null, isNameSignPresent, offer.isWithNameSign)

        OfferItemBindDelegate.setVehicleConveniences(offer, vehicle_conveniences)
    }

    private fun bindBookNowTiny(view: View, offer: BookNowOfferModel) = with(view) {
        with(offer.transportType) {
            tv_car_model_tiny.text = context.getString(id.getModelsRes())
            tv_car_class_tiny.text = context.getString(nameId)
            bindCapacity(view_capacity, this)
            Utils.bindMainOfferPhoto(img_car_photo_tiny, view, resource = id.getImageRes())
        }
        iv_car_color_tiny.isVisible = false
        bindRating(view_rating_tiny, Ratings.BOOK_NOW_RATING, true)
        bindLanguages(Either.Single(languages_container_tiny), listOf(LocaleModel.BOOK_NOW_LOCALE_DEFAULT),
            layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_ITEM)

        offer.withoutDiscount.let { setStrikePriceText(tv_price_no_discount, it?.preferred ?: it?.def) }
        offer.base.let { tv_price_final.text = it.preferred ?: it.def }
        vehicle_conveniences.isVisible = false
    }

    private fun setStrikePriceText(textView: TextView, price: String?) = with(textView) {
        price?.let { strikeText = context.getString(R.string.text_in_parentheses, it) }
        isVisible = price != null
    }

    /* Common layouts */
    @SuppressLint("SetTextI18n")
    private fun bindCapacity(capacityView: View, transportTypeModel: TransportTypeModel) = with(capacityView) {
        transportType_сountPassengers.text = "x ${transportTypeModel.paxMax}"
        transportType_сountBaggage.text    = "x ${transportTypeModel.luggageMax}"
    }

    internal fun bindRating(rateView: View, rating: Ratings, approved: Boolean = false) = with(rateView) {
        imgApproved.isVisible = approved
        if (rating.average != Ratings.NO_RATING) {
            tv_drivers_rate.text  = rating.average.toHalfEvenRoundedFloat().toString().replace(".", ",")
            tv_drivers_rate.isVisible = true
            imgStar.isVisible = true
            isVisible = true
            return@with RATE_SHOWN
        }
        isVisible = approved
        return@with approved
    }

    internal fun bindLanguages(
        container: Either,
        languages: List<LocaleModel>,
        colNumber: Int = LanguageDrawer.DEFAULT_ITEM_COLUMNS,
        layoutParamsRes: LanguageDrawer.LanguageLayoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_ITEM
    ) {
        when (container) {
            is Either.Single -> LanguageDrawer.drawSingleLine(container.layout, languages, layoutParamsRes)
            is Either.Multi  -> LanguageDrawer.drawMultipleLine(container.layout, languages, colNumber, layoutParamsRes)
        }
    }

    internal fun bindNameSignPlate(
        context: Context,
        iconNameSign: ImageView,
        textMissingNameSign: TextView?,
        isNameSignPresent: Boolean,
        isWithNameSign: Boolean
    ) {
        iconNameSign.isVisible = if (isNameSignPresent) {
            val imgResId = if (isWithNameSign) R.drawable.ic_with_name_sign else R.drawable.ic_missing_name_sign
            iconNameSign.setImageDrawable(ContextCompat.getDrawable(context, imgResId))
            true
        } else false
        textMissingNameSign?.isVisible = isNameSignPresent && !isWithNameSign
    }

    /**
     * @param viewConveniences layout for vehicle conveniences
     */
    internal fun setVehicleConveniences(offer: OfferModel, viewConveniences: View) {
        with(viewConveniences) {
            imgFreeWater.isVisible = offer.refreshments
            imgFreeWiFi.isVisible = offer.wifi
            imgCharge.isVisible = offer.charger
            ivWheelchair.isVisible = offer.wheelchair
            ivArmor.isVisible = offer.armored
            isVisible = offer.refreshments || offer.wifi
                || offer.charger || offer.wheelchair || offer.armored
        }
    }

}

sealed class Either {
    class Single(val layout: LinearLayout) : Either()
    class Multi(val layout: LinearLayout) : Either()
}
