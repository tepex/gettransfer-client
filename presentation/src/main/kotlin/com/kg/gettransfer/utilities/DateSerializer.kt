package com.kg.gettransfer.utilities

import kotlinx.serialization.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Encoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.internal.StringDescriptor

import java.text.DateFormat
import java.text.SimpleDateFormat

import java.util.Date

@Serializer(forClass = Date::class)
object DateSerializer: KSerializer<Date> {
    override val descriptor: SerialDescriptor = StringDescriptor
    
    private val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")

    override fun serialize(output: Encoder, obj: Date) { output.encodeString(df.format(obj)) }
    override fun deserialize(input: Decoder): Date = df.parse(input.decodeString())
}
