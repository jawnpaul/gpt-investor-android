package com.thejawnpaul.gptinvestor.core.platform

import com.thejawnpaul.gptinvestor.remote.ClientInfo
import org.koin.core.annotation.Singleton

@Singleton(binds = [ClientInfo::class])
class ClientInfoImpl(appInfo: AppInfo) : ClientInfo {
    override val platform: String = when (Platform.type) {
        PlatformType.Android -> "android"
        PlatformType.IOS -> "ios"
    }
    override val versionName: String = appInfo.versionName
}
