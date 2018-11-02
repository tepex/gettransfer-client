package com.kg.gettransfer.prefs.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.kg.gettransfer.data.model.GTAddressEntity

open class GTAddressMapper(private val gson: Gson): Mapper<String, List<GTAddressEntity>> {
    override fun fromJson(json: String): List<GTAddressEntity> = 
        gson.fromJson(json, object: TypeToken<List<GTAddressEntity>>(){}.type)
    override fun toJson(type: List<GTAddressEntity>) = gson.toJson(type)
}
