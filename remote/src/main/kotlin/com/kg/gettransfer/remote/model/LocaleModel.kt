package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LocaleModel(@SerializedName("code") @Expose val code: String,
                  @SerializedName("title") @Expose val title: String)
