package com.thejawnpaul.gptinvestor.features.conversation.data.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirestoreConversationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val userId: String?
        get() = auth.currentUser?.uid

    // Conversations
    suspend fun saveConversation(conversation: ConversationEntity): Result<String> = try {
        val userId = this.userId ?: return Result.failure(Exception("User not authenticated"))
        val firestoreConversation = FirestoreConversation(conversation)

        firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .document(firestoreConversation.conversationId)
            .set(firestoreConversation)
            .await()

        Result.success(firestoreConversation.conversationId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAllConversations(): Result<List<FirestoreConversation>> = try {
        val userId = this.userId ?: return Result.failure(Exception("User not authenticated"))

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        val conversations = snapshot.documents.mapNotNull {
            it.toObject(FirestoreConversation::class.java)
        }
        Result.success(conversations)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteConversation(conversationId: String): Result<Unit> = try {
        val userId = this.userId ?: return Result.failure(Exception("User not authenticated"))

        // Delete all messages in the conversation
        val messagesSnapshot = firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .get()
            .await()

        val batch = firestore.batch()
        messagesSnapshot.documents.forEach { doc ->
            batch.delete(doc.reference)
        }

        // Delete the conversation
        batch.delete(
            firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)
        )

        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Messages
    suspend fun saveMessage(message: MessageEntity): Result<String> = try {
        val userId = this.userId ?: return Result.failure(Exception("User not authenticated"))
        val firestoreMessage = FirestoreMessage(message)

        firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .document(firestoreMessage.conversationId)
            .collection("messages")
            .document(firestoreMessage.messageId)
            .set(firestoreMessage)
            .await()

        Result.success(firestoreMessage.messageId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getMessagesForConversation(conversationId: String): Result<List<FirestoreMessage>> = try {
        val userId = this.userId ?: return Result.failure(Exception("User not authenticated"))

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .get()
            .await()

        val messages = snapshot.documents.mapNotNull {
            it.toObject(FirestoreMessage::class.java)
        }
        Result.success(messages)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
