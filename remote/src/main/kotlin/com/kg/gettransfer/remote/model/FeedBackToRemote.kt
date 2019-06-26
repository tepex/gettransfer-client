package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FeedBackToRemote(@SerializedName("passenger_feedback") @Expose val comment: String)
