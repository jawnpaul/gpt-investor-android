package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import kotlin.math.roundToLong
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

object WholeNumberSerializer : KSerializer<Long> {
    override val descriptor = PrimitiveSerialDescriptor("WholeNumber", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long {
        val json = decoder as? JsonDecoder ?: return decoder.decodeLong()
        val element = json.decodeJsonElement()
        val content = (element as? JsonPrimitive)?.content
            ?: throw SerializationException("Expected a number but got $element")
        return content.toLongOrNull()
            ?: content.toDoubleOrNull()?.takeIf { it.isFinite() }?.roundToLong()
            ?: throw SerializationException("Expected a number but got $content")
    }

    override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)
}
