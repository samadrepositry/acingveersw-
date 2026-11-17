package com.example.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

data class AppStrings(
    val languageCode: String,
    val languageName: String,
    val isRtl: Boolean,
    val brandTitle: String,
    val tagline: String,
    val heroHeadline: String,
    val heroSubheadline: String,
    val startTraining: String,
    val exploreExercises: String,
    val meetAiCoach: String,
    val signInWithGoogle: String,
    val navDashboard: String,
    val navLibrary: String,
    val navStudios: String,
    val navAiCoach: String,
    val navCommunity: String,
    val dailyChecklist: String,
    val morning: String,
    val afternoon: String,
    val evening: String,
    val streakLabel: String,
    val practiceTimeLabel: String,
    val completedLabel: String,
    val voiceScoreLabel: String,
    val actingScoreLabel: String,
    val movementScoreLabel: String,
    val startExercise: String,
    val pause: String,
    val resume: String,
    val reset: String,
    val completeSession: String,
    val safetyNoteLabel: String,
    val correctTechniqueLabel: String,
    val commonMistakesLabel: String,
    val objectiveLabel: String,
    val voiceStudioTitle: String,
    val cameraStudioTitle: String,
    val monologueLibraryTitle: String,
    val trainingPlansTitle: String,
    val analyticsTitle: String,
    val careerCenterTitle: String,
    val pricingTitle: String,
    val adminPanelTitle: String,
    val helpCenterTitle: String
)

object ActorverseStrings {
    val English = AppStrings(
        languageCode = "en",
        languageName = "English",
        isRtl = false,
        brandTitle = "ACTORVERSE",
        tagline = "Train Like an Athlete. Perform Like a Legend.",
        heroHeadline = "Your Journey to Becoming an Extraordinary Actor Starts Here.",
        heroSubheadline = "Train your voice, body, emotions, creativity, and screen presence with your personal AI-powered acting academy.",
        startTraining = "Start Training",
        exploreExercises = "Explore Exercises",
        meetAiCoach = "Meet Your AI Coach",
        signInWithGoogle = "Sign in with Google",
        navDashboard = "Dashboard",
        navLibrary = "Training",
        navStudios = "Studios",
        navAiCoach = "AI Coach",
        navCommunity = "Connect",
        dailyChecklist = "Daily Training Checklist",
        morning = "Morning",
        afternoon = "Afternoon",
        evening = "Evening",
        streakLabel = "Streak",
        practiceTimeLabel = "Practice",
        completedLabel = "Completed",
        voiceScoreLabel = "Voice",
        actingScoreLabel = "Acting",
        movementScoreLabel = "Movement",
        startExercise = "Start Exercise",
        pause = "Pause",
        resume = "Resume",
        reset = "Reset",
        completeSession = "Complete & Save",
        safetyNoteLabel = "Safety & Wellness Note",
        correctTechniqueLabel = "Correct Technique",
        commonMistakesLabel = "Common Mistakes",
        objectiveLabel = "Training Objective",
        voiceStudioTitle = "Voice Studio",
        cameraStudioTitle = "Camera Studio",
        monologueLibraryTitle = "Monologues & Scripts",
        trainingPlansTitle = "Training Plans",
        analyticsTitle = "Performance Analytics",
        careerCenterTitle = "Audition & Career Center",
        pricingTitle = "Membership Plans",
        adminPanelTitle = "Administrator Dashboard",
        helpCenterTitle = "Help Center & Policies"
    )

    val Urdu = AppStrings(
        languageCode = "ur",
        languageName = "اردو",
        isRtl = true,
        brandTitle = "ACTORVERSE",
        tagline = "کھلاڑی کی طرح مشق کریں۔ لیجنڈ کی طرح پرفارم کریں۔",
        heroHeadline = "غیر معمولی اداکار بننے کا آپ کا سفر یہاں سے شروع ہوتا ہے۔",
        heroSubheadline = "اپنی آواز، جسم، جذبات، تخلیقی صلاحیت اور اسکرین پریزنس کو ذاتی AI ایکٹنگ اکیڈمی کے ساتھ نکھاریں۔",
        startTraining = "تربیت شروع کریں",
        exploreExercises = "مشقیں دیکھیں",
        meetAiCoach = "AI کوچ سے ملیں",
        signInWithGoogle = "Google سے سائن ان کریں",
        navDashboard = "ڈیش بورڈ",
        navLibrary = "تربیت",
        navStudios = "اسٹوڈیوز",
        navAiCoach = "AI کوچ",
        navCommunity = "کمیونٹی",
        dailyChecklist = "روزانہ تربیتی چیک لسٹ",
        morning = "صبح",
        afternoon = "دوپہر",
        evening = "شام",
        streakLabel = "تسلسل",
        practiceTimeLabel = "مشق کا وقت",
        completedLabel = "مکمل",
        voiceScoreLabel = "آواز",
        actingScoreLabel = "اداکاری",
        movementScoreLabel = "حرکت",
        startExercise = "مشق شروع کریں",
        pause = "وقفہ",
        resume = "جاری رکھیں",
        reset = "ری سیٹ",
        completeSession = "مکمل اور محفوظ کریں",
        safetyNoteLabel = "حفاظتی ہدایت",
        correctTechniqueLabel = "درست تکنیک",
        commonMistakesLabel = "عام غلطیاں",
        objectiveLabel = "تربیتی مقصد",
        voiceStudioTitle = "وائس اسٹوڈیو",
        cameraStudioTitle = "کیمرہ اسٹوڈیو",
        monologueLibraryTitle = "مونولاگ اور اسکرپٹس",
        trainingPlansTitle = "تربیتی منصوبے",
        analyticsTitle = "کارکردگی کا تجزیہ",
        careerCenterTitle = "آڈیشن اور کیریئر سینٹر",
        pricingTitle = "ممبرشپ پلانز",
        adminPanelTitle = "ایڈمن ڈیش بورڈ",
        helpCenterTitle = "مدد اور پالیسیاں"
    )

    val Hindi = English.copy(
        languageCode = "hi",
        languageName = "हिन्दी",
        isRtl = false,
        tagline = "एक एथलीट की तरह अभ्यास करें। एक लीजेंड की तरह अभिनय करें।",
        startTraining = "प्रशिक्षण शुरू करें",
        exploreExercises = "अभ्यास देखें",
        meetAiCoach = "AI कोच से मिलें"
    )

    fun forCode(code: String): AppStrings = when (code) {
        "ur" -> Urdu
        "hi" -> Hindi
        else -> English
    }
}

val LocalAppStrings = staticCompositionLocalOf { ActorverseStrings.English }

@Composable
fun ProvideActorverseLocalization(
    languageCode: String,
    content: @Composable () -> Unit
) {
    val strings = ActorverseStrings.forCode(languageCode)
    val layoutDirection = if (strings.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalLayoutDirection provides layoutDirection,
        content = content
    )
}
