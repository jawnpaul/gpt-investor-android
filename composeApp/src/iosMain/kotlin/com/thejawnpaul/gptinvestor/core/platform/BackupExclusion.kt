package com.thejawnpaul.gptinvestor.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun excludeDocumentsFromBackup() {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    ) ?: return
    documentDirectory.setResourceValue(value = true, forKey = NSURLIsExcludedFromBackupKey, error = null)
}
