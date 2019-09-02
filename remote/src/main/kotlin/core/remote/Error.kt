package com.kg.gettransfer.core.remote

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.remote.model.ResponseModel

import retrofit2.HttpException

fun processError(gson: Gson, exception: Throwable): ResultData<Nothing> =
    if (exception !is HttpException) {
        ResultData.NetworkError(exception)
    } else {
        val body = exception.response()?.errorBody()?.string() ?: error("Exception response is null")
        val msg = exception.message ?: ""
        try {
            val errorModel = gson.fromJson(body, ResponseModel::class.java).error
            if (errorModel != null) {
                ResultData.ApiError(
                    exception.code(),
                    errorModel.details?.toString() ?: msg,
                    errorModel.type
                )
            } else {
                error("Expecting error field in server response [body]:\n$body")
            }
        } catch (js: JsonSyntaxException) {
            /* Some times server can return error as a simple HTML stuff */
            val matchResult = body?.let { Regex("^<h1>(.+)</h1>$").find(it)?.groupValues }
            ResultData.ApiError(exception.code(), matchResult?.getOrNull(1) ?: msg, null)
        }
    }
