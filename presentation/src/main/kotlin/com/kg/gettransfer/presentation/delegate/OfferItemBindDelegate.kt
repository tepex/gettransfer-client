package com.kg.gettransfer.presentation.delegate

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.strikeText
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.RatingsModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.MoneyModel
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import kotlinx.android.synthetic.main.offer_expanded.view.*
import kotlinx.android.synthetic.main.offer_expanded_no_photo.view.*
import kotlinx.android.synthetic.main.offer_tiny.view.*
import kotlinx.android.synthetic.main.vehicle_items.view.*
import kotlinx.android.synthetic.main.view_offer_bottom.view.*
import kotlinx.android.synthetic.main.view_offer_conditions.view.*
import kotlinx.android.synthetic.main.view_offer_driver_info.view.*
import kotlinx.android.synthetic.main.view_offer_header.view.*
import kotlinx.android.synthetic.main.view_offer_rating.view.*
import kotlinx.android.synthetic.main.view_transport_capacity.view.*

object OfferItemBindDelegate {

    fun bindOfferExpanded(view: View, offer: OfferItemModel) =
            when (offer) {
                is OfferModel         -> bindOfferModel(offer, view)
                is BookNowOfferModel  -> bindBookNow(offer, view)
    }

    private fun bindOfferModel(offer: OfferModel, view: View) =
        with(view) {
            tv_car_model.text = offer.vehicle.name
            ivCarColor.isVisible = true
            ivCarColor.setImageDrawable(offer.vehicle.color?.let { Utils.getCarColorFormRes(view.context, it) })
            tv_car_class.text = offer.vehicle.transportType.nameId?.let { context.getString(it) } ?: ""
            bindCapacity(offer_conditions.view_capacity, offer.vehicle.transportType)
            bindConveniences(offer_conditions.vehicle_conveniences, offer)
            bindRating(view_offer_rate, offer.carrier.ratings, offer.carrier.approved).also { offer_rating_bg.isVisible = it }
            bindLanguages(singleLineContainer = driver_abilities.languages_container, languages = offer.carrier.languages)
            bindPrice(offer_bottom, offer.price.base, offer.price.withoutDiscount)
            Utils.bindMainOfferPhoto(imgOffer_mainPhoto, view, path = offer.vehicle.photos.first())
            return@with
        }


    private fun bindBookNow(offer: BookNowOfferModel, view: View) {
        with(view) {
            tv_car_model.text = context.getString(TransportTypeMapper.getModelsById(offer.transportType.id))
            tv_car_class.text = offer.transportType.nameId?.let { context.getString(it) } ?: ""
            bindLanguages(singleLineContainer = driver_abilities.languages_container, languages = listOf(LocaleModel.BOOK_NOW_LOCALE_DEFAULT))
            bindRating(view_offer_rate, RatingsModel.BOOK_NOW_RATING).also { offer_rating_bg.isVisible = true }
            bindPrice(offer_bottom, offer.base)
            Utils.bindMainOfferPhoto(imgOffer_mainPhoto, view, resource = TransportTypeMapper.getImageById(offer.transportType.id))
        }
    }

    fun bindOfferNoPhoto(view: View, offer: OfferModel) {
        with(view) {
            offer_header_noPhoto.tv_car_model_.text = offer.vehicle.name
            tv_transport_class.text = offer.vehicle.transportType.nameId?.let { context.getString(it) } ?: ""
            bindCapacity(offer_conditions_noPhoto.view_capacity, offer.vehicle.transportType)
            bindConveniences(offer_conditions_noPhoto.vehicle_conveniences, offer)
            bindRating(view_offer_rate_noPhoto, offer.carrier.ratings, offer.carrier.approved)
            bindLanguages(singleLineContainer = driver_abilities_noPhoto.languages_container, languages = offer.carrier.languages)
            bindPrice(offer_bottom_noPhoto, offer.price.base, offer.price.withoutDiscount)
        }

    }


    fun bindOfferTiny(view: View, offer: OfferItemModel) {
        when (offer) {
            is OfferModel -> bindOfferModelTiny(view, offer)
            is BookNowOfferModel -> bindBookNowTiny(view, offer)
        }
    }

    private fun bindOfferModelTiny(view: View, offer: OfferModel) {
        with(view) {
            tv_car_model_tiny.text = offer.vehicle.name
            //ivCarColorTiny.isVisible = offer.vehicle.color != null
            //ivCarColorTiny.setImageDrawable(offer.vehicle.color?.let { Utils.getCarColorFormRes(view.context, it) })
            tv_car_class_tiny.text = offer.vehicle.transportType.nameId?.let { context.getString(it) ?: "" }
            offer.vehicle.photos.firstOrNull()
                .also {
                    Utils.bindMainOfferPhoto(
                        img_car_photo_tiny,
                        view,
                        path = it,
                        resource = TransportTypeMapper.getEmptyImageById(offer.vehicle.transportType.id)
                    )
                }
            bindRating(view_rating_tiny, offer.carrier.ratings, offer.carrier.approved)
            bindLanguages(multiLineContainer = languages_container_tiny, languages = offer.carrier.languages)
            offer.price.withoutDiscount?.let { setStrikePriceText(tv_price_no_discount, it.preferred ?: it.def) }
            tv_price_final.text = offer.price.base.preferred ?: offer.price.base.def
        }
    }

    private fun bindBookNowTiny(view: View, offer: BookNowOfferModel) {
        with(view) {
            tv_car_model_tiny.text = context.getString(TransportTypeMapper.getModelsById(offer.transportType.id))
            //ivCarColorTiny.isVisible = false
            tv_car_class_tiny.text = offer.transportType.nameId?.let { context.getString(it) } ?: ""
            Utils.bindMainOfferPhoto(img_car_photo_tiny, view, resource = TransportTypeMapper.getImageById(offer.transportType.id))
            bindRating(view_rating_tiny, RatingsModel.BOOK_NOW_RATING, true)
            bindLanguages(multiLineContainer = languages_container_tiny, languages = listOf(LocaleModel.BOOK_NOW_LOCALE_DEFAULT))
            offer.withoutDiscount?.let { setStrikePriceText(tv_price_no_discount, it.preferred ?: it.def) }
            tv_price_final.text = offer.base.preferred ?: offer.base.def
        }
    }

    private fun setStrikePriceText(textView: TextView, price: String) {
        textView.apply {
            strikeText = context.getString(R.string.text_in_parentheses, price)
            isVisible = true
        }
    }

    /* Common layouts */
    private fun bindCapacity(capacityView: View, transportTypeModel: TransportTypeModel) =
            with(capacityView) {
                transportType_сountPassengers.text = "x".plus(transportTypeModel.paxMax)
                transportType_сountBaggage.text    = "x".plus(transportTypeModel.luggageMax)
            }

    private fun bindConveniences(conveniencesView: View, offer: OfferModel) =
            with(conveniencesView) {
                imgFreeWiFi.isVisible  = offer.wifi
                imgCharge.isVisible    = offer.charger
                imgFreeWater.isVisible = offer.refreshments
                /* imgGreen.isVisible = offer.green */
            }

    internal fun bindRating(rateView: View, rating: RatingsModel, approved: Boolean = false): Boolean =
            with(rateView) {
                imgApproved.isVisible = approved
                if (rating.average != null && rating.average != NO_RATING) {
                    tv_drivers_rate.text  = rating.average.toString().replace(".", ",")
                    tv_drivers_rate.isVisible = true
                    imgStar.isVisible = true
                    isVisible = true
                    return@with RATE_SHOWN
                }
                isVisible = approved
                return@with approved
            }

    internal fun bindLanguages(singleLineContainer: LinearLayout? = null, multiLineContainer: LinearLayout? = null,
                               languages: List<LocaleModel>, rowNumber: Int = LanguageDrawer.ITEM_COLUMNS) {

        if (singleLineContainer == null && multiLineContainer == null)
            throw IllegalArgumentException("One of containers must not be null in ${this::class.java.name}")

        if (singleLineContainer != null) LanguageDrawer.drawSingleLine(singleLineContainer, languages = languages)
        else LanguageDrawer.drawMultipleLine(multiLineContainer!!, languages = languages, rowNumber = rowNumber)

    }

    private fun bindPrice(viewWithPrice: View, base: MoneyModel, withoutDiscount: MoneyModel? = null) =
            with(viewWithPrice) {
                tv_current_price.text = base.preferred ?: base.def
                withoutDiscount?.let {
                    tv_old_price.strikeText = it.preferred ?: it.def
                    tv_old_price.isVisible = true
                }
            }

    private const val NO_RATING  = 0.0F
    private const val RATE_SHOWN = true
}