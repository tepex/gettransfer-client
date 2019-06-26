package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.MessageEntity

class MessageNewWrapperModel(@SerializedName(MessageEntity.ENTITY_NAME) @Expose val message: MessageNewModel)

class MessageNewModel(@SerializedName(MessageEntity.TEXT) @Expose val text: String)
