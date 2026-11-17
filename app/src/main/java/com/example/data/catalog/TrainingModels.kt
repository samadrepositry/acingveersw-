package com.example.data.catalog

enum class DepartmentId(val key: String) {
    BREATHING("breathing"),
    BODY("body"),
    VOICE("voice"),
    DICTION("diction"),
    ACTING("acting"),
    EMOTION("emotion"),
    FACIAL("facial"),
    IMPROVISATION("improvisation"),
    CAMERA("camera"),
    MONOLOGUE("monologue")
}

data class Department(
    val id: String,
    val code: String,
    val title: String,
    val urduTitle: String,
    val description: String,
    val safetyNote: String? = null
)

data class TrainingExercise(
    val id: String,
    val departmentId: String,
    val title: String,
    val difficulty: String,
    val durationSeconds: Int,
    val objective: String,
    val instructions: String,
    val correctTechnique: String,
    val commonMistakes: String,
    val beginnerModification: String? = null,
    val safetyNote: String? = null,
    val languageTag: String = "Global"
)

data class EmotionExercisePack(
    val id: String,
    val emotion: String,
    val facialPrompt: String,
    val bodyPrompt: String,
    val voicePrompt: String,
    val dialoguePrompt: String,
    val silentPrompt: String,
    val cameraPrompt: String
)

data class MonologueItem(
    val id: String,
    val title: String,
    val genre: String,
    val medium: String,
    val difficulty: String,
    val characterDescription: String,
    val givenCircumstances: String,
    val sceneObjective: String,
    val obstacles: String,
    val tactics: String,
    val beats: List<String>,
    val subtext: String,
    val scriptLines: String,
    val estimatedSeconds: Int
)

data class TrainingPlan(
    val id: String,
    val title: String,
    val durationDays: Int,
    val focus: String,
    val tierRequired: String,
    val dailyMinutes: Int,
    val modules: List<String>
)

data class CourseModule(
    val id: String,
    val title: String,
    val level: String,
    val lessonsCount: Int,
    val departmentId: String,
    val summary: String,
    val lessons: List<String>
)

data class AchievementDef(
    val id: String,
    val title: String,
    val description: String,
    val xpBonus: Int
)

data class CountryCurrency(
    val country: String,
    val code: String,
    val symbol: String,
    val proMonthly: String,
    val premiumMonthly: String
)
