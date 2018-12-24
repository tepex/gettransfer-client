package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RateToRemote(@SerializedName ("rating") @Expose val rating: Int)