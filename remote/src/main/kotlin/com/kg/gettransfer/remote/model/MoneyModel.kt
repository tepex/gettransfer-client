package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.MoneyEntity

class MoneyModel(@SerializedName(MoneyEntity.DEFAULT) @Expose val def: String,
                 @SerializedName(MoneyEntity.PREFERRED) @Expose val preferred: String?)
