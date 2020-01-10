package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlayerIdModel(@SerializedName("player_id") @Expose val playerId: String)
