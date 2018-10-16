package com.kg.gettransfer.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kg.gettransfer.data.model.GTAddressEntity

class JsonParser {
    var gson: Gson = Gson()

//     fun <T: Any>getFromJson(data: String, clazz: Class<List<T>>): List<Any> {
//        return gson.fromJson<List<T>>(data, clazz)
//
//    }
    fun writeToJson(obj: List<Any>): String{
        return gson.toJson(obj)
    }

    fun getFromJson(data: String): List<GTAddressEntity>? {
        if(data == null) return null
        return gson.fromJson(data, object : TypeToken<List<GTAddressEntity>>(){}.type)
    }
}