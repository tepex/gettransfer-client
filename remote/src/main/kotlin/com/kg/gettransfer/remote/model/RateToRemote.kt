package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.RateEntity

class RateToRemote(@SerializedName ("rating") @Expose val rating: Int)