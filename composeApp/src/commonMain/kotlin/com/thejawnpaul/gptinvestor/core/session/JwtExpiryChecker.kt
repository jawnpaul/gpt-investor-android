package com.thejawnpaul.gptinvestor.core.session

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class JwtPayload(val exp: Long? = null)

private val lenientJwtJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

object JwtExpiryChecker {
    fun isExpired(token: String, nowEpochSeconds: Long): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return false
            val payloadJson = base64UrlDecode(parts[1])
            val payload = lenientJwtJson.decodeFromString<JwtPayload>(payloadJson)
            val exp = payload.exp ?: return false
            nowEpochSeconds >= exp
        } catch (_: Exception) {
            false
        }
    }

    private fun base64UrlDecode(input: String): String {
        val normalized = input.replace('-', '+').replace('_', '/')
        val padded = when (normalized.length % 4) {
            2 -> "$normalized=="
            3 -> "$normalized="
            else -> normalized
        }
        return decodeBase64Bytes(padded).decodeToString()
    }

    private fun decodeBase64Bytes(input: String): ByteArray {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val bytes = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0
        for (c in input) {
            if (c == '=') break
            val idx = alphabet.indexOf(c)
            if (idx < 0) continue
            buffer = (buffer shl 6) or idx
            bitsLeft += 6
            if (bitsLeft >= 8) {
                bitsLeft -= 8
                bytes.add((buffer shr bitsLeft).toByte())
                buffer = buffer and ((1 shl bitsLeft) - 1)
            }
        }
        return bytes.toByteArray()
    }
}
