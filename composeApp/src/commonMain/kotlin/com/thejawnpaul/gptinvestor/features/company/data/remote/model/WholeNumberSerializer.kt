package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import kotlin.math.roundToLong
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive

object WholeNumberSerializer : KSerializer<Long> {
    override val descriptor = PrimitiveSerialDescriptor("WholeNumber", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long {
        val content = (decoder as JsonDecoder).decodeJsonElement().jsonPrimitive.content
        return content.toLongOrNull()
            ?: content.toDoubleOrNull()?.roundToLong()
            ?: throw SerializationException("Expected a number but got $content")
    }

    override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)
}
