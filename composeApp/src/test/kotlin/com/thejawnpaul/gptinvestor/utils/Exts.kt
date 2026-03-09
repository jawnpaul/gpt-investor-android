package com.thejawnpaul.gptinvestor.utils

import com.google.common.io.Resources
import java.io.File
import java.net.URL

internal fun getJson(path: String): String {
    val uri: URL = Resources.getResource(path)
    val file = File(uri.path)
    return String(file.readBytes())
}
