package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.core.OperationType
import com.example.core.handleFirestoreError
import com.example.data.model.ActorProfile
import com.example.data.model.CareerItem
import com.example.data.model.ChecklistItem
import com.example.data.model.CommunityPost
import com.example.data.model.CustomExercise
import com.example.data.model.ExerciseSession
import com.example.data.model.ModerationReport
import com.example.data.model.StudioRecording
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ActorverseRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")

    private fun <T> wrapFlow(
        path: String,
        opType: OperationType,
        uid: String,
        source: Flow<T>
    ): Flow<T> = source.catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, opType, path)
        }
        throw error
    }

    // --- Profile ---

    fun observeProfile(userId: String): Flow<ActorProfile?> = flow {
        val docRef = db.collection("users").document(userId)
        emitAll(
            wrapFlow(docRef.path, OperationType.GET, userId,
                docRef.snapshots().map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(ActorProfile::class.java, ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
            )
        )
    }

    suspend fun ensureProfileInitialized(
        displayName: String,
        preferredLanguage: String = "en"
    ): ActorProfile {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                return snap.toObject(ActorProfile::class.java, ServerTimestampBehavior.ESTIMATE)
                    ?: ActorProfile(id = uid, userId = uid)
            }
            val initial = ActorProfile(
                id = uid,
                userId = uid,
                fullName = displayName.ifBlank { "Actor" },
                stageName = displayName.ifBlank { "Performer" },
                country = "United States",
                preferredLanguage = preferredLanguage,
                experienceLevel = "Beginner",
                interests = listOf("Film acting", "Voice acting"),
                actingGoals = "Build daily breath, voice, and screen confidence.",
                isAdultConfirmed = true,
                onboardingComplete = false
            )
            docRef.set(initial.toCreateMap(uid)).await()
            seedDefaultChecklist(uid)
            return initial
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun updateProfile(profile: ActorProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val updates = mapOf(
            "fullName" to profile.fullName.take(80).ifBlank { "Actor" },
            "stageName" to profile.stageName.take(60).ifBlank { "Performer" },
            "country" to profile.country.take(60).ifBlank { "United States" },
            "city" to profile.city.take(60),
            "preferredLanguage" to profile.preferredLanguage.ifBlank { "en" },
            "experienceLevel" to profile.experienceLevel.take(40).ifBlank { "Beginner" },
            "interests" to profile.interests.take(10),
            "actingGoals" to profile.actingGoals.take(300),
            "isAdultConfirmed" to profile.isAdultConfirmed,
            "hidePublicRanking" to profile.hidePublicRanking,
            "dailyGoalMinutes" to profile.dailyGoalMinutes.coerceIn(5L, 300L),
            "onboardingComplete" to profile.onboardingComplete,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- Sessions & Progress ---

    fun observeSessions(userId: String): Flow<List<ExerciseSession>> = flow {
        val colRef = db.collection("users").document(userId).collection("sessions")
        emitAll(
            wrapFlow(colRef.path, OperationType.LIST, userId,
                colRef.snapshots().map { snap ->
                    snap.toObjects(ExerciseSession::class.java, ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
            )
        )
    }

    suspend fun logCompletedSession(
        exerciseId: String,
        exerciseTitle: String,
        departmentId: String,
        durationSeconds: Long,
        repetitions: Long,
        notes: String
    ): String {
        val uid = requireUserId()
        val sessionId = UUID.randomUUID().toString()
        val xpEarned = ((durationSeconds / 15L).coerceAtLeast(1L) * 10L + repetitions * 5L).coerceIn(10L, 5000L)
        val session = ExerciseSession(
            userId = uid,
            exerciseId = exerciseId,
            exerciseTitle = exerciseTitle,
            departmentId = departmentId,
            durationSeconds = durationSeconds.coerceIn(1L, 14400L),
            repetitions = repetitions.coerceIn(0L, 500L),
            xpEarned = xpEarned,
            notes = notes
        )
        val sessionRef = db.collection("users").document(uid).collection("sessions").document(sessionId)
        val userRef = db.collection("users").document(uid)
        try {
            val batch = db.batch()
            batch.set(sessionRef, session.toCreateMap(uid))
            val scoreField = when (departmentId) {
                "breathing", "voice", "diction" -> "voiceScore"
                "body", "facial" -> "movementScore"
                else -> "actingScore"
            }
            val userSnap = userRef.get().await()
            val currentScore = (userSnap.getLong(scoreField) ?: 10L)
            val newScore = (currentScore + 2L).coerceAtMost(100L)
            val currentStreak = (userSnap.getLong("streakDays") ?: 0L)
            batch.update(
                userRef,
                mapOf(
                    "xp" to FieldValue.increment(xpEarned),
                    "totalPracticeSeconds" to FieldValue.increment(session.durationSeconds),
                    "exercisesCompleted" to FieldValue.increment(1L),
                    "streakDays" to if (currentStreak == 0L) 1L else currentStreak,
                    scoreField to newScore,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            return sessionId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, sessionRef.path)
            throw e
        }
    }

    // --- Daily Checklist ---

    fun observeChecklist(userId: String): Flow<List<ChecklistItem>> = flow {
        val colRef = db.collection("users").document(userId).collection("checklist")
        emitAll(
            wrapFlow(colRef.path, OperationType.LIST, userId,
                colRef.snapshots().map { snap ->
                    snap.toObjects(ChecklistItem::class.java, ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.sortOrder }
                }
            )
        )
    }

    private suspend fun seedDefaultChecklist(uid: String) {
        val colRef = db.collection("users").document(uid).collection("checklist")
        val existing = colRef.limit(1).get().await()
        if (!existing.isEmpty) return

        val defaults = listOf(
            ChecklistItem(title = "Breathing", timeOfDay = "morning", departmentId = "breathing", durationMinutes = 5, sortOrder = 1),
            ChecklistItem(title = "Body Warm-Up", timeOfDay = "morning", departmentId = "body", durationMinutes = 5, sortOrder = 2),
            ChecklistItem(title = "Voice Warm-Up", timeOfDay = "morning", departmentId = "voice", durationMinutes = 10, sortOrder = 3),
            ChecklistItem(title = "Diction Drill", timeOfDay = "morning", departmentId = "diction", durationMinutes = 10, sortOrder = 4),
            ChecklistItem(title = "Acting Exercises", timeOfDay = "afternoon", departmentId = "acting", durationMinutes = 15, sortOrder = 5),
            ChecklistItem(title = "Improvisation", timeOfDay = "afternoon", departmentId = "improvisation", durationMinutes = 10, sortOrder = 6),
            ChecklistItem(title = "Camera Practice", timeOfDay = "afternoon", departmentId = "camera", durationMinutes = 15, sortOrder = 7),
            ChecklistItem(title = "Emotional Expression", timeOfDay = "evening", departmentId = "emotion", durationMinutes = 10, sortOrder = 8),
            ChecklistItem(title = "Monologue Practice", timeOfDay = "evening", departmentId = "monologue", durationMinutes = 15, sortOrder = 9),
            ChecklistItem(title = "Reflection & Notes", timeOfDay = "evening", departmentId = "acting", durationMinutes = 5, sortOrder = 10)
        )
        val batch = db.batch()
        defaults.forEach { item ->
            val ref = colRef.document(UUID.randomUUID().toString())
            batch.set(ref, item.toCreateMap(uid))
        }
        batch.commit().await()
    }

    suspend fun toggleChecklistItem(item: ChecklistItem) {
        val uid = requireUserId()
        require(item.id.isNotBlank())
        val docRef = db.collection("users").document(uid).collection("checklist").document(item.id)
        try {
            docRef.update(
                mapOf(
                    "isCompleted" to !item.isCompleted,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun addChecklistItem(title: String, timeOfDay: String, departmentId: String, durationMinutes: Long) {
        val uid = requireUserId()
        val id = UUID.randomUUID().toString()
        val docRef = db.collection("users").document(uid).collection("checklist").document(id)
        val item = ChecklistItem(
            userId = uid,
            title = title,
            timeOfDay = timeOfDay,
            departmentId = departmentId,
            durationMinutes = durationMinutes,
            isCompleted = false,
            sortOrder = 99
        )
        try {
            docRef.set(item.toCreateMap(uid)).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteChecklistItem(itemId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("checklist").document(itemId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Studio Recordings (Voice & Camera) ---

    fun observeRecordings(userId: String): Flow<List<StudioRecording>> = flow {
        val colRef = db.collection("users").document(userId).collection("recordings")
        emitAll(
            wrapFlow(colRef.path, OperationType.LIST, userId,
                colRef.snapshots().map { snap ->
                    snap.toObjects(StudioRecording::class.java, ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
            )
        )
    }

    suspend fun saveRecording(recording: StudioRecording): String {
        val uid = requireUserId()
        val id = UUID.randomUUID().toString()
        val docRef = db.collection("users").document(uid).collection("recordings").document(id)
        try {
            docRef.set(recording.toCreateMap(uid)).await()
            return id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateRecordingFeedback(recordingId: String, aiFeedback: String, personalNotes: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("recordings").document(recordingId)
        try {
            docRef.update(
                mapOf(
                    "aiFeedback" to aiFeedback.take(4000),
                    "personalNotes" to personalNotes.take(1000),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteRecording(recordingId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("recordings").document(recordingId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Career Center ---

    fun observeCareerItems(userId: String): Flow<List<CareerItem>> = flow {
        val colRef = db.collection("users").document(userId).collection("career_items")
        emitAll(
            wrapFlow(colRef.path, OperationType.LIST, userId,
                colRef.snapshots().map { snap ->
                    snap.toObjects(CareerItem::class.java, ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
            )
        )
    }

    suspend fun addCareerItem(item: CareerItem): String {
        val uid = requireUserId()
        val id = UUID.randomUUID().toString()
        val docRef = db.collection("users").document(uid).collection("career_items").document(id)
        try {
            docRef.set(item.toCreateMap(uid)).await()
            return id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteCareerItem(itemId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("career_items").document(itemId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- Community Posts (Aligned Query: isHidden == false) ---

    fun observeCommunityPosts(userId: String): Flow<List<CommunityPost>> = flow {
        val query = db.collection("community_posts").whereEqualTo("isHidden", false)
        emitAll(
            wrapFlow("community_posts", OperationType.LIST, userId,
                query.snapshots().map { snap ->
                    snap.toObjects(CommunityPost::class.java, ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                }
            )
        )
    }

    suspend fun createCommunityPost(
        authorStageName: String,
        country: String,
        language: String,
        category: String,
        title: String,
        body: String
    ): String {
        val uid = requireUserId()
        val id = UUID.randomUUID().toString()
        val docRef = db.collection("community_posts").document(id)
        val post = CommunityPost(
            userId = uid,
            authorStageName = authorStageName,
            country = country,
            language = language,
            category = category,
            title = title,
            body = body
        )
        try {
            docRef.set(post.toCreateMap(uid)).await()
            return id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun likeCommunityPost(postId: String) {
        requireUserId()
        val docRef = db.collection("community_posts").document(postId)
        try {
            docRef.update(
                mapOf(
                    "likesCount" to FieldValue.increment(1L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun reportCommunityPost(postId: String, reason: String) {
        val uid = requireUserId()
        val reportId = UUID.randomUUID().toString()
        val reportRef = db.collection("reports").document(reportId)
        val postRef = db.collection("community_posts").document(postId)
        try {
            val batch = db.batch()
            batch.set(
                reportRef,
                mapOf(
                    "reporterId" to uid,
                    "postId" to postId,
                    "reason" to reason.take(500).ifBlank { "Inappropriate content" },
                    "status" to "open",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.update(
                postRef,
                mapOf(
                    "reportCount" to FieldValue.increment(1L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, reportRef.path)
            throw e
        }
    }

    // --- Custom Published Exercises (Aligned Query: isPublished == true) ---

    fun observePublishedCustomExercises(userId: String): Flow<List<CustomExercise>> = flow {
        val query = db.collection("custom_exercises").whereEqualTo("isPublished", true)
        emitAll(
            wrapFlow("custom_exercises", OperationType.LIST, userId,
                query.snapshots().map { snap ->
                    snap.toObjects(CustomExercise::class.java, ServerTimestampBehavior.ESTIMATE)
                }
            )
        )
    }

    // --- Admin Operations ---

    suspend fun checkIsAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        if (user.isEmailVerified && user.email == "abdulsamadrind350@gmail.com") return true
        return try {
            val doc = db.collection("admins").document(user.uid).get().await()
            doc.exists()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun adminFetchAllUsers(): List<ActorProfile> {
        requireUserId()
        return try {
            db.collection("users").get().await()
                .toObjects(ActorProfile::class.java, ServerTimestampBehavior.ESTIMATE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "users")
            throw e
        }
    }

    suspend fun adminUpdateUserAccount(
        targetUserId: String,
        subscriptionTier: String,
        isSuspended: Boolean
    ) {
        requireUserId()
        val docRef = db.collection("users").document(targetUserId)
        try {
            docRef.update(
                mapOf(
                    "subscriptionTier" to subscriptionTier,
                    "isSuspended" to isSuspended,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun adminCreateExercise(exercise: CustomExercise): String {
        val uid = requireUserId()
        val id = UUID.randomUUID().toString()
        val docRef = db.collection("custom_exercises").document(id)
        try {
            docRef.set(
                mapOf(
                    "title" to exercise.title.take(120),
                    "departmentId" to exercise.departmentId,
                    "difficulty" to exercise.difficulty,
                    "durationSeconds" to exercise.durationSeconds.coerceIn(15L, 7200L),
                    "instructions" to exercise.instructions.take(3000),
                    "correctTechnique" to exercise.correctTechnique.take(1500),
                    "commonMistakes" to exercise.commonMistakes.take(1500),
                    "objective" to exercise.objective.take(500),
                    "isPublished" to exercise.isPublished,
                    "createdBy" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            return id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun adminFetchReports(): List<ModerationReport> {
        requireUserId()
        return try {
            db.collection("reports").get().await()
                .toObjects(ModerationReport::class.java, ServerTimestampBehavior.ESTIMATE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "reports")
            throw e
        }
    }

    suspend fun adminResolveReport(reportId: String, postId: String, hidePost: Boolean) {
        requireUserId()
        val reportRef = db.collection("reports").document(reportId)
        val postRef = db.collection("community_posts").document(postId)
        try {
            val batch = db.batch()
            batch.update(
                reportRef,
                mapOf(
                    "status" to if (hidePost) "resolved" else "dismissed",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            if (hidePost) {
                batch.update(
                    postRef,
                    mapOf(
                        "isHidden" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, reportRef.path)
            throw e
        }
    }
}
