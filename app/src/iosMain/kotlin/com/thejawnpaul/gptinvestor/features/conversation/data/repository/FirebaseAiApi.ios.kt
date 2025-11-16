package com.thejawnpaul.gptinvestor.features.conversation.data.repository

import com.thejawnpaul.gptinvestor.core.firebase.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.HistoryContent
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IFirebaseAiApi
import kotlinx.coroutines.flow.Flow

class FirebaseAiApi(remoteConfig: RemoteConfig) : IFirebaseAiApi {

    override suspend fun sendMessage(
        history: List<HistoryContent>,
        prompt: String
    ): String? {
        TODO("Not yet implemented")
    }

    override fun sendMessageStream(
        history: List<HistoryContent>,
        prompt: String
    ): Flow<String?> {
        TODO("Not yet implemented")
    }
}