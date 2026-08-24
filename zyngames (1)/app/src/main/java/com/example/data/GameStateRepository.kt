package com.example.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Basic repository to interact with Firebase Firestore for game session state,
 * multiplayer lobby management, real-time score synchronization, and chat.
 */
class GameStateRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    companion object {
        private const val COLLECTION_SESSIONS = "game_sessions"
        private const val COLLECTION_ROOMS = "party_rooms"
        private const val COLLECTION_LEADERBOARD = "leaderboards"
        private const val COLLECTION_MESSAGES = "chat_messages"
    }

    /**
     * Create or initialize a new game session in Firestore.
     */
    suspend fun createGameSession(
        sessionId: String,
        gameType: String,
        hostPlayerId: String,
        initialData: Map<String, Any> = emptyMap()
    ): Result<Unit> = runCatching {
        val sessionData = hashMapOf<String, Any>(
            "sessionId" to sessionId,
            "gameType" to gameType,
            "hostPlayerId" to hostPlayerId,
            "createdAt" to System.currentTimeMillis(),
            "status" to "IN_PROGRESS",
            "currentRound" to 1,
            "myTeamScore" to 0,
            "oppTeamScore" to 0
        ).apply {
            putAll(initialData)
        }

        firestore.collection(COLLECTION_SESSIONS)
            .document(sessionId)
            .set(sessionData, SetOptions.merge())
            .await()
    }

    /**
     * Listen to real-time updates for a specific game session.
     */
    fun observeGameSession(sessionId: String): Flow<Map<String, Any>?> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null
        try {
            val docRef = firestore.collection(COLLECTION_SESSIONS).document(sessionId)
            listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.data)
                } else {
                    trySend(null)
                }
            }
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    /**
     * Update game state fields such as current round, scores, buzz status, or timer.
     */
    suspend fun updateGameState(
        sessionId: String,
        updates: Map<String, Any>
    ): Result<Unit> = runCatching {
        val updatedPayload = updates.toMutableMap().apply {
            put("lastUpdatedAt", System.currentTimeMillis())
        }
        firestore.collection(COLLECTION_SESSIONS)
            .document(sessionId)
            .set(updatedPayload, SetOptions.merge())
            .await()
    }

    /**
     * Update team score in a live match session.
     */
    suspend fun updateTeamScore(
        sessionId: String,
        teamKey: String, // e.g., "myTeamScore" or "oppTeamScore"
        scoreDelta: Int
    ): Result<Unit> = runCatching {
        val docRef = firestore.collection(COLLECTION_SESSIONS).document(sessionId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentScore = (snapshot.getLong(teamKey) ?: 0L).toInt()
            transaction.update(docRef, teamKey, currentScore + scoreDelta)
            transaction.update(docRef, "lastUpdatedAt", System.currentTimeMillis())
        }.await()
    }

    /**
     * Send a real-time team or arena chat message under a session.
     */
    suspend fun sendChatMessage(
        sessionId: String,
        messageId: String,
        teamId: String,
        senderName: String,
        senderEmoji: String,
        message: String,
        isSuggestion: Boolean = false
    ): Result<Unit> = runCatching {
        val chatData = hashMapOf(
            "id" to messageId,
            "teamId" to teamId,
            "senderName" to senderName,
            "senderEmoji" to senderEmoji,
            "message" to message,
            "isSuggestion" to isSuggestion,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection(COLLECTION_SESSIONS)
            .document(sessionId)
            .collection(COLLECTION_MESSAGES)
            .document(messageId)
            .set(chatData)
            .await()
    }

    /**
     * Observe real-time chat messages for a session.
     */
    fun observeChatMessages(sessionId: String): Flow<List<Map<String, Any>>> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null
        try {
            val messagesRef = firestore.collection(COLLECTION_SESSIONS)
                .document(sessionId)
                .collection(COLLECTION_MESSAGES)
                .orderBy("timestamp")

            listenerRegistration = messagesRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.data } ?: emptyList()
                trySend(messages)
            }
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    /**
     * Create or update a party room with a join PIN code.
     */
    suspend fun createOrUpdatePartyRoom(
        roomCode: String,
        hostPlayerName: String,
        gameType: String,
        members: List<Map<String, Any>>
    ): Result<Unit> = runCatching {
        val roomData = hashMapOf(
            "roomCode" to roomCode,
            "hostPlayerName" to hostPlayerName,
            "gameType" to gameType,
            "members" to members,
            "lastActive" to System.currentTimeMillis()
        )

        firestore.collection(COLLECTION_ROOMS)
            .document(roomCode)
            .set(roomData, SetOptions.merge())
            .await()
    }

    /**
     * Observe a party room in real time by room code.
     */
    fun observePartyRoom(roomCode: String): Flow<Map<String, Any>?> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null
        try {
            val roomRef = firestore.collection(COLLECTION_ROOMS).document(roomCode)
            listenerRegistration = roomRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.data)
                } else {
                    trySend(null)
                }
            }
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }

    /**
     * Save a completed game record to the leaderboard collection.
     */
    suspend fun recordGameResult(
        userId: String,
        userName: String,
        gameType: String,
        score: Int,
        prizeWon: Int,
        isWin: Boolean
    ): Result<Unit> = runCatching {
        val record = hashMapOf(
            "userId" to userId,
            "userName" to userName,
            "gameType" to gameType,
            "score" to score,
            "prizeWon" to prizeWon,
            "isWin" to isWin,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection(COLLECTION_LEADERBOARD)
            .document()
            .set(record)
            .await()
    }
}
