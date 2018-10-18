package com.kg.gettransfer


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kg.gettransfer.data.model.GTAddressEntity

class JsonParser {
    var gson: Gson = Gson()

    fun writeToJson(obj: List<Any>): String = gson.toJson(obj)

    fun getFromJson(data: String): List<GTAddressEntity>? = gson.fromJson(data, object : TypeToken<List<GTAddressEntity>>(){}.type)

}