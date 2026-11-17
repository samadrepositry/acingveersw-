package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class ActorProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val stageName: String = "",
    val country: String = "United States",
    val city: String = "",
    val preferredLanguage: String = "en",
    val experienceLevel: String = "Beginner",
    val interests: List<String> = emptyList(),
    val actingGoals: String = "",
    val isAdultConfirmed: Boolean = true,
    val hidePublicRanking: Boolean = false,
    val dailyGoalMinutes: Long = 30L,
    val onboardingComplete: Boolean = false,
    val xp: Long = 0L,
    val streakDays: Long = 0L,
    val totalPracticeSeconds: Long = 0L,
    val exercisesCompleted: Long = 0L,
    val voiceScore: Long = 10L,
    val actingScore: Long = 10L,
    val movementScore: Long = 10L,
    val subscriptionTier: String = "free",
    val isSuspended: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "fullName" to fullName.ifBlank { "Actor" },
        "stageName" to stageName.ifBlank { fullName.ifBlank { "Performer" } },
        "country" to country.ifBlank { "United States" },
        "city" to city,
        "preferredLanguage" to preferredLanguage.ifBlank { "en" },
        "experienceLevel" to experienceLevel.ifBlank { "Beginner" },
        "interests" to interests.take(10),
        "actingGoals" to actingGoals,
        "isAdultConfirmed" to isAdultConfirmed,
        "hidePublicRanking" to hidePublicRanking,
        "dailyGoalMinutes" to dailyGoalMinutes.coerceIn(5L, 300L),
        "onboardingComplete" to onboardingComplete,
        "xp" to xp.coerceAtLeast(0L),
        "streakDays" to streakDays.coerceAtLeast(0L),
        "totalPracticeSeconds" to totalPracticeSeconds.coerceAtLeast(0L),
        "exercisesCompleted" to exercisesCompleted.coerceAtLeast(0L),
        "voiceScore" to voiceScore.coerceIn(0L, 100L),
        "actingScore" to actingScore.coerceIn(0L, 100L),
        "movementScore" to movementScore.coerceIn(0L, 100L),
        "subscriptionTier" to "free",
        "isSuspended" to false,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class ExerciseSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val exerciseId: String = "",
    val exerciseTitle: String = "",
    val departmentId: String = "",
    val durationSeconds: Long = 0L,
    val repetitions: Long = 1L,
    val xpEarned: Long = 0L,
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "exerciseId" to exerciseId,
        "exerciseTitle" to exerciseTitle.take(120),
        "departmentId" to departmentId,
        "durationSeconds" to durationSeconds.coerceIn(1L, 14400L),
        "repetitions" to repetitions.coerceIn(0L, 500L),
        "xpEarned" to xpEarned.coerceIn(0L, 5000L),
        "notes" to notes.take(1000),
        "createdAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class ChecklistItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val timeOfDay: String = "morning",
    val departmentId: String = "breathing",
    val durationMinutes: Long = 5L,
    val isCompleted: Boolean = false,
    val sortOrder: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "title" to title.take(100),
        "timeOfDay" to if (timeOfDay in listOf("morning", "afternoon", "evening")) timeOfDay else "morning",
        "departmentId" to departmentId,
        "durationMinutes" to durationMinutes.coerceIn(1L, 180L),
        "isCompleted" to isCompleted,
        "sortOrder" to sortOrder.coerceIn(0L, 500L),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class StudioRecording(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val studioType: String = "voice",
    val localFilePath: String = "",
    val durationSeconds: Long = 0L,
    val estimatedWpm: Long = 0L,
    val fillerWordsCount: Long = 0L,
    val framingPreset: String = "Close-Up",
    val aiFeedback: String = "",
    val personalNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "title" to title.take(120),
        "studioType" to if (studioType == "camera") "camera" else "voice",
        "localFilePath" to localFilePath.take(500),
        "durationSeconds" to durationSeconds.coerceIn(0L, 3600L),
        "estimatedWpm" to estimatedWpm.coerceIn(0L, 500L),
        "fillerWordsCount" to fillerWordsCount.coerceIn(0L, 500L),
        "framingPreset" to framingPreset.take(40),
        "aiFeedback" to aiFeedback.take(4000),
        "personalNotes" to personalNotes.take(1000),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class CareerItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val itemType: String = "audition",
    val title: String = "",
    val subtitle: String = "",
    val status: String = "Preparing",
    val notes: String = "",
    val mediaUri: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "itemType" to if (itemType in listOf("credit", "audition", "goal", "headshot")) itemType else "audition",
        "title" to title.take(120),
        "subtitle" to subtitle.take(120),
        "status" to status.take(40).ifBlank { "Active" },
        "notes" to notes.take(1000),
        "mediaUri" to mediaUri.take(500),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class CommunityPost(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorStageName: String = "",
    val country: String = "",
    val language: String = "en",
    val category: String = "discussion",
    val title: String = "",
    val body: String = "",
    val likesCount: Long = 0L,
    val reportCount: Long = 0L,
    val isHidden: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "authorStageName" to authorStageName.take(60).ifBlank { "Actor" },
        "country" to country.take(60).ifBlank { "Global" },
        "language" to language.take(20).ifBlank { "en" },
        "category" to if (category in listOf("discussion", "challenge", "collaboration", "feedback")) category else "discussion",
        "title" to title.take(140),
        "body" to body.take(2000),
        "likesCount" to 0L,
        "reportCount" to 0L,
        "isHidden" to false,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class ModerationReport(
    @DocumentId val id: String = "",
    val reporterId: String = "",
    val postId: String = "",
    val reason: String = "",
    val status: String = "open",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomExercise(
    @DocumentId val id: String = "",
    val title: String = "",
    val departmentId: String = "voice",
    val difficulty: String = "Beginner",
    val durationSeconds: Long = 120L,
    val instructions: String = "",
    val correctTechnique: String = "",
    val commonMistakes: String = "",
    val objective: String = "",
    val isPublished: Boolean = true,
    val createdBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AdminRole(
    @DocumentId val id: String = "",
    val email: String = "",
    val role: String = "moderator",
    @ServerTimestamp val createdAt: Timestamp? = null
)
