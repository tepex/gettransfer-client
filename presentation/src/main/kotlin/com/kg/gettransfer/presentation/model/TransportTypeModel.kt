package com.kg.gettransfer.presentation.model

import android.content.res.Resources

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.TransportType

import kotlin.reflect.*

import timber.log.Timber

class TransportTypeModel(private val resources: Resources,
                         val delegate: TransportType,
                         var checked: Boolean = false) {

    @DrawableRes
    val imageId: Int
    @StringRes
    val nameId: Int
    
    init {
    	val nameRes = R.string::class.members.find( { it.name == "transport_type_${delegate.id}" } )
        nameId = (nameRes?.call() as? Int) ?: R.string.transport_type_unknown
        val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_${delegate.id}" } )
        imageId = (imageRes?.call() as? Int) ?: R.drawable.ic_transport_type_unknown
        /*
        when(delegate.id) {
            "economy" -> {
                imageId = R.drawable.economy
                nameId = R.string.transport_type_economy
            }
            "business" -> {
                imageId = R.drawable.business
                nameId = R.string.transport_type_business
            }
            "premium" -> {
                imageId = R.drawable.premium
                nameId = R.string.transport_type_premium
            }
            "van" -> {
                imageId = R.drawable.van
                nameId = R.string.transport_type_van
            }
            "minibus" -> {
                imageId = R.drawable.minibus
                nameId = R.string.transport_type_minibus
            }
            "bus" -> {
                imageId = R.drawable.bus
                nameId = R.string.transport_type_bus
            }
            "limousine" -> {
                imageId = R.drawable.limousine
                nameId = R.string.transport_type_limousine
            }
            "helicopter" -> {
                imageId = R.drawable.helicopter
                nameId = R.string.transport_type_helicopter
            }
        }
        */
    }
}
