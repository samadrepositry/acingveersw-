package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.core.LocalAppStrings
import com.example.core.ProvideActorverseLocalization
import com.example.core.attemptAutoSignIn
import com.example.core.authStateFlow
import com.example.core.configureAppCheck
import com.example.core.signOutUser
import com.example.data.ai.AiCoachRepository
import com.example.data.catalog.CatalogDepartmentsFtoJ
import com.example.data.catalog.TrainingExercise
import com.example.data.repository.ActorverseRepository
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthLandingScreen
import com.example.ui.screens.CareerCenterStandaloneScreen
import com.example.ui.screens.ConnectHubScreen
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExerciseDetailScreen
import com.example.ui.screens.HelpAndLegalScreen
import com.example.ui.screens.MonologueLibraryScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.ProfileEditScreen
import com.example.ui.screens.StudiosHubScreen
import com.example.ui.screens.SubDestination
import com.example.ui.screens.TrainingLibraryScreen
import com.example.ui.screens.TrainingPlansScreen
import com.example.ui.theme.ActorverseTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.StageGold
import com.example.ui.viewmodel.ActorverseViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Must configure App Check before any ViewModel or Firebase call
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            val currentUser by remember { Firebase.auth.authStateFlow() }
                .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)
            var preAuthLanguage by rememberSaveable { mutableStateOf("en") }

            val context = LocalContext.current
            val credentialManager = remember { CredentialManager.create(context) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                if (Firebase.auth.currentUser == null) {
                    attemptAutoSignIn(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {},
                        onUnauthenticated = {},
                        scope = scope
                    )
                }
            }

            ActorverseTheme {
                if (currentUser == null) {
                    ProvideActorverseLocalization(languageCode = preAuthLanguage) {
                        AuthLandingScreen(
                            selectedLanguage = preAuthLanguage,
                            onLanguageChange = { preAuthLanguage = it },
                            onAuthSuccess = {}
                        )
                    }
                } else {
                    val user = currentUser!!
                    key(user.uid) {
                        ActorverseAuthenticatedShell(
                            currentUserId = user.uid,
                            userDisplayName = user.displayName ?: user.email?.substringBefore('@') ?: "Performer",
                            onSignOut = {
                                signOutUser(credentialManager, scope)
                            }
                        )
                    }
                }
            }
        }
    }
}

private enum class MainTab {
    DASHBOARD,
    TRAINING,
    STUDIOS,
    AI_COACH,
    CONNECT
}

@Composable
private fun ActorverseAuthenticatedShell(
    currentUserId: String,
    userDisplayName: String,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ActorverseViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val appContext = context.applicationContext
                ActorverseViewModel(
                    repository = ActorverseRepository(appContext),
                    aiRepository = AiCoachRepository(),
                    currentUserId = currentUserId,
                    userDisplayName = userDisplayName
                )
            }
        }
    )

    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val checklist by viewModel.checklist.collectAsStateWithLifecycle()
    val recordings by viewModel.recordings.collectAsStateWithLifecycle()
    val careerItems by viewModel.careerItems.collectAsStateWithLifecycle()
    val communityPosts by viewModel.communityPosts.collectAsStateWithLifecycle()
    val customExercises by viewModel.customExercises.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val streamingReply by viewModel.streamingReply.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val chatError by viewModel.chatError.collectAsStateWithLifecycle()
    val aiPlanState by viewModel.aiPlanState.collectAsStateWithLifecycle()
    val scriptAnalysisState by viewModel.scriptAnalysisState.collectAsStateWithLifecycle()
    val studioFeedbackState by viewModel.studioFeedbackState.collectAsStateWithLifecycle()
    val adminUsers by viewModel.adminUsers.collectAsStateWithLifecycle()
    val adminReports by viewModel.adminReports.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableStateOf(MainTab.DASHBOARD) }
    var studioInitialTab by rememberSaveable { mutableIntStateOf(0) }
    var activeExercise by remember { mutableStateOf<TrainingExercise?>(null) }
    var activeSubDestination by rememberSaveable { mutableStateOf<SubDestination?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusMessage) {
        val msg = statusMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    val activeLanguage = profile?.preferredLanguage ?: "en"

    ProvideActorverseLocalization(languageCode = activeLanguage) {
        val strings = LocalAppStrings.current

        when {
            activeExercise != null -> {
                val ex = activeExercise!!
                ExerciseDetailScreen(
                    exercise = ex,
                    onBack = { activeExercise = null },
                    onCompleteSession = { durationSec, reps, notes ->
                        viewModel.logCompletedSession(
                            exerciseId = ex.id,
                            exerciseTitle = ex.title,
                            departmentId = ex.departmentId,
                            durationSeconds = durationSec,
                            repetitions = reps,
                            notes = notes
                        )
                    },
                    onNextExercise = {
                        val all = CatalogDepartmentsFtoJ.allExercises
                        val currentIndex = all.indexOfFirst { it.id == ex.id }
                        if (currentIndex >= 0 && all.isNotEmpty()) {
                            activeExercise = all[(currentIndex + 1) % all.size]
                        } else {
                            activeExercise = all.firstOrNull()
                        }
                    }
                )
            }

            activeSubDestination != null -> {
                when (activeSubDestination!!) {
                    SubDestination.ANALYTICS -> AnalyticsScreen(
                        profile = profile,
                        sessions = sessions,
                        recordings = recordings,
                        onBack = { activeSubDestination = null }
                    )
                    SubDestination.TRAINING_PLANS -> TrainingPlansScreen(
                        onBack = { activeSubDestination = null },
                        onOpenAiCoachPlan = {
                            activeSubDestination = null
                            selectedTab = MainTab.AI_COACH
                        },
                        onStartStarterDrill = {
                            activeSubDestination = null
                            activeExercise = CatalogDepartmentsFtoJ.allExercises.firstOrNull()
                        }
                    )
                    SubDestination.COURSES -> CoursesScreen(
                        onBack = { activeSubDestination = null },
                        onOpenDepartmentLibrary = {
                            activeSubDestination = null
                            selectedTab = MainTab.TRAINING
                        }
                    )
                    SubDestination.CAREER_CENTER -> CareerCenterStandaloneScreen(
                        careerItems = careerItems,
                        onBack = { activeSubDestination = null },
                        onAddCareerItem = viewModel::addCareerItem,
                        onDeleteCareerItem = viewModel::deleteCareerItem
                    )
                    SubDestination.PRICING -> PricingScreen(
                        currentTier = profile?.subscriptionTier ?: "free",
                        onBack = { activeSubDestination = null }
                    )
                    SubDestination.PROFILE_EDIT -> ProfileEditScreen(
                        profile = profile,
                        onBack = { activeSubDestination = null },
                        onSaveProfile = viewModel::updateProfile,
                        onSignOut = {
                            activeSubDestination = null
                            onSignOut()
                        }
                    )
                    SubDestination.HELP_LEGAL -> HelpAndLegalScreen(
                        onBack = { activeSubDestination = null }
                    )
                    SubDestination.ADMIN_PANEL -> {
                        if (isAdmin) {
                            AdminDashboardScreen(
                                users = adminUsers,
                                reports = adminReports,
                                onBack = { activeSubDestination = null },
                                onRefresh = viewModel::loadAdminData,
                                onUpdateUser = viewModel::adminUpdateUser,
                                onCreateExercise = viewModel::adminCreateCustomExercise,
                                onResolveReport = viewModel::adminResolveReport
                            )
                        } else {
                            BackHandler { activeSubDestination = null }
                            LaunchedEffect(Unit) { activeSubDestination = null }
                        }
                    }
                }
            }

            else -> {
                if (selectedTab != MainTab.DASHBOARD) {
                    BackHandler { selectedTab = MainTab.DASHBOARD }
                }

                val navItems = listOf(
                    Triple(MainTab.DASHBOARD, strings.navDashboard, Icons.Filled.Dashboard),
                    Triple(MainTab.TRAINING, strings.navLibrary, Icons.Filled.FitnessCenter),
                    Triple(MainTab.STUDIOS, strings.navStudios, Icons.Filled.Mic),
                    Triple(MainTab.AI_COACH, strings.navAiCoach, Icons.Filled.AutoAwesome),
                    Triple(MainTab.CONNECT, strings.navCommunity, Icons.Filled.Groups)
                )

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val useRail = maxWidth >= 600.dp
                    Scaffold(
                        containerColor = ObsidianBg,
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        bottomBar = {
                            if (!useRail) {
                                NavigationBar(containerColor = ObsidianSurface) {
                                    navItems.forEach { (tab, label, icon) ->
                                        NavigationBarItem(
                                            selected = selectedTab == tab,
                                            onClick = { selectedTab = tab },
                                            icon = { Icon(icon, contentDescription = label) },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (useRail) {
                                NavigationRail(
                                    containerColor = ObsidianSurface,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    navItems.forEach { (tab, label, icon) ->
                                        NavigationRailItem(
                                            selected = selectedTab == tab,
                                            onClick = { selectedTab = tab },
                                            icon = { Icon(icon, contentDescription = label) },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("rail_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }

                            when (selectedTab) {
                                MainTab.DASHBOARD -> DashboardScreen(
                                    profile = profile,
                                    sessions = sessions,
                                    checklist = checklist,
                                    recordings = recordings,
                                    isAdmin = isAdmin,
                                    onToggleChecklist = viewModel::toggleChecklist,
                                    onAddChecklistItem = viewModel::addChecklistItem,
                                    onDeleteChecklistItem = viewModel::deleteChecklistItem,
                                    onOpenExercise = { exId ->
                                        activeExercise = CatalogDepartmentsFtoJ.allExercises.find { it.id == exId }
                                            ?: CatalogDepartmentsFtoJ.allExercises.firstOrNull()
                                    },
                                    onOpenAiCoach = { selectedTab = MainTab.AI_COACH },
                                    onOpenSubDestination = { dest -> activeSubDestination = dest },
                                    onLanguageChange = viewModel::updateLanguage
                                )

                                MainTab.TRAINING -> TrainingLibraryScreen(
                                    favoriteIds = favoriteIds,
                                    customExercises = customExercises,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onSelectExercise = { ex -> activeExercise = ex },
                                    onOpenMonologues = {
                                        studioInitialTab = 2
                                        selectedTab = MainTab.STUDIOS
                                    },
                                    onOpenVoiceStudio = {
                                        studioInitialTab = 0
                                        selectedTab = MainTab.STUDIOS
                                    },
                                    onOpenCameraStudio = {
                                        studioInitialTab = 1
                                        selectedTab = MainTab.STUDIOS
                                    }
                                )

                                MainTab.STUDIOS -> StudiosHubScreen(
                                    initialTab = studioInitialTab,
                                    recordings = recordings,
                                    studioFeedbackState = studioFeedbackState,
                                    onSaveRecording = viewModel::saveRecording,
                                    onRequestAiFeedback = viewModel::requestStudioFeedback,
                                    onDeleteRecording = viewModel::deleteRecording,
                                    monologueTabContent = {
                                        MonologueLibraryScreen(
                                            scriptAnalysisState = scriptAnalysisState,
                                            onAnalyzeScript = viewModel::analyzeScriptWithAi,
                                            onLogMonologuePractice = { mono, secs ->
                                                viewModel.logCompletedSession(
                                                    exerciseId = mono.id,
                                                    exerciseTitle = mono.title,
                                                    departmentId = "monologue",
                                                    durationSeconds = secs,
                                                    repetitions = 1L,
                                                    notes = "Monologue rehearsal: ${mono.genre}"
                                                )
                                            }
                                        )
                                    }
                                )

                                MainTab.AI_COACH -> AiCoachScreen(
                                    messages = chatMessages,
                                    streamingReply = streamingReply,
                                    isChatLoading = isChatLoading,
                                    chatError = chatError,
                                    aiPlanState = aiPlanState,
                                    preferredLanguage = activeLanguage,
                                    onLanguageChange = viewModel::updateLanguage,
                                    onSendMessage = viewModel::sendCoachMessage,
                                    onGeneratePlan = viewModel::generateAiTrainingPlan
                                )

                                MainTab.CONNECT -> ConnectHubScreen(
                                    profile = profile,
                                    posts = communityPosts,
                                    careerItems = careerItems,
                                    onCreatePost = viewModel::createCommunityPost,
                                    onLikePost = viewModel::likePost,
                                    onReportPost = viewModel::reportPost,
                                    onAddCareerItem = viewModel::addCareerItem,
                                    onDeleteCareerItem = viewModel::deleteCareerItem,
                                    onToggleHideRanking = { hide ->
                                        profile?.let { viewModel.updateProfile(it.copy(hidePublicRanking = hide)) }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
