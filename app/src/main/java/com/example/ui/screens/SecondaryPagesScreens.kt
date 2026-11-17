package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.LocalAppStrings
import com.example.data.catalog.CatalogDepartmentsFtoJ
import com.example.data.model.ActorProfile
import com.example.data.model.ExerciseSession
import com.example.data.model.StudioRecording
import com.example.ui.components.CinematicCard
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.SkillProgressRing
import com.example.ui.components.WeeklyPracticeChart
import com.example.ui.components.departmentAccentColor
import com.example.ui.theme.CrimsonCue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldStage
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    profile: ActorProfile?,
    sessions: List<ExerciseSession>,
    recordings: List<StudioRecording>,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current
    val weeklyData = remember(sessions) { computeWeeklyMinutes(sessions) }
    val totalMinutes = ((profile?.totalPracticeSeconds ?: 0L) / 60L).toInt()
    val deptCounts = remember(sessions) {
        sessions.groupingBy { it.departmentId }.eachCount().entries.sortedByDescending { it.value }
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text(strings.analyticsTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CinematicCard(elevated = true) {
                    Text("Cumulative Craft Metrics", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("${totalMinutes}m", style = MaterialTheme.typography.displayMedium, color = StageGold)
                            Text("Practice Time", style = MaterialTheme.typography.bodyMedium)
                        }
                        Column {
                            Text("${profile?.exercisesCompleted ?: 0}", style = MaterialTheme.typography.displayMedium, color = ElectricBlue)
                            Text("Drills Completed", style = MaterialTheme.typography.bodyMedium)
                        }
                        Column {
                            Text("${recordings.size}", style = MaterialTheme.typography.displayMedium, color = ElectricViolet)
                            Text("Studio Takes", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Department Skill Rings", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        SkillProgressRing(strings.voiceScoreLabel, (profile?.voiceScore ?: 10L).toInt(), ElectricViolet)
                        SkillProgressRing(strings.actingScoreLabel, (profile?.actingScore ?: 10L).toInt(), StageGold)
                        SkillProgressRing(strings.movementScoreLabel, (profile?.movementScore ?: 10L).toInt(), EmeraldStage)
                    }
                }
            }

            item { WeeklyPracticeChart(dailyMinutes = weeklyData) }

            item {
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Training Volume by Department", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (deptCounts.isEmpty()) {
                        Text(
                            "Complete your first timed exercise to build your department breakdown.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val maxCount = deptCounts.maxOf { it.value }.coerceAtLeast(1)
                        deptCounts.forEach { (dept, count) ->
                            val color = departmentAccentColor(dept)
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(dept.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
                                    Text("$count sessions", style = MaterialTheme.typography.labelMedium, color = color)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { count.toFloat() / maxCount.toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = color
                                )
                            }
                        }
                    }
                }
            }

            item { Text("All 12 Actor Achievements", style = MaterialTheme.typography.headlineMedium) }

            items(CatalogDepartmentsFtoJ.achievements, key = { it.id }) { badge ->
                val xp = profile?.xp ?: 0L
                val unlocked = when (badge.id) {
                    "first_exercise" -> (profile?.exercisesCompleted ?: 0L) >= 1L
                    "first_recording" -> recordings.isNotEmpty()
                    "streak_7" -> (profile?.streakDays ?: 0L) >= 7L
                    "streak_30" -> (profile?.streakDays ?: 0L) >= 30L
                    "voice_explorer" -> sessions.count { it.departmentId == "voice" } >= 5
                    "breath_beginner" -> sessions.count { it.departmentId == "breathing" } >= 3
                    "diction_master" -> sessions.count { it.departmentId == "diction" } >= 5
                    "monologue_performer" -> sessions.count { it.departmentId == "monologue" } >= 3
                    "camera_confidence" -> recordings.count { it.studioType == "camera" } >= 3
                    "improv_champion" -> sessions.count { it.departmentId == "improvisation" } >= 5
                    "consistent_actor" -> (profile?.exercisesCompleted ?: 0L) >= 10L
                    "pro_milestone" -> xp >= 1000L
                    else -> false
                }
                CinematicCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentBorder = if (unlocked) StageGold else null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = if (unlocked) StageGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(badge.title, style = MaterialTheme.typography.titleMedium)
                            Text(badge.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        DepartmentBadge(if (unlocked) "UNLOCKED" else "+${badge.xpBonus} XP", if (unlocked) StageGold else ElectricBlue)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlansScreen(
    onBack: () -> Unit,
    onOpenAiCoachPlan: () -> Unit,
    onStartStarterDrill: () -> Unit
) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current
    var expandedPlanId by remember { mutableStateOf<String?>("plan_7") }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text(strings.trainingPlansTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CinematicCard(elevated = true, accentBorder = ElectricViolet.copy(alpha = 0.5f)) {
                    Text("Want an AI-Tailored Schedule?", style = MaterialTheme.typography.titleMedium, color = ElectricViolet)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Ask AI Coach to build a custom 7-day plan matched to your exact daily minutes and acting goals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onOpenAiCoachPlan) { Text("Generate Custom AI Plan") }
                }
            }

            items(CatalogDepartmentsFtoJ.trainingPlans, key = { it.id }) { plan ->
                val isOpen = expandedPlanId == plan.id
                CinematicCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentBorder = if (plan.tierRequired == "free") EmeraldStage.copy(alpha = 0.45f) else StageGold.copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DepartmentBadge("${plan.durationDays} DAYS • ${plan.dailyMinutes} MIN/DAY", ElectricBlue)
                        DepartmentBadge(plan.tierRequired.uppercase(), if (plan.tierRequired == "free") EmeraldStage else StageGold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(plan.title, style = MaterialTheme.typography.titleLarge)
                            Text(plan.focus, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { expandedPlanId = if (isOpen) null else plan.id }) {
                            Icon(if (isOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = "Toggle curriculum")
                        }
                    }
                    if (isOpen) {
                        Spacer(modifier = Modifier.height(8.dp))
                        plan.modules.forEach { mod ->
                            Text("• $mod", style = MaterialTheme.typography.bodyMedium, color = StageGold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = onStartStarterDrill) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Begin Day 1 Drill")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    onBack: () -> Unit,
    onOpenDepartmentLibrary: () -> Unit
) {
    BackHandler { onBack() }
    var expandedCourseId by remember { mutableStateOf<String?>("course_1") }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text("Academy Courses") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(CatalogDepartmentsFtoJ.courses, key = { it.id }) { course ->
                val isOpen = expandedCourseId == course.id
                val accent = departmentAccentColor(course.departmentId)
                CinematicCard(modifier = Modifier.fillMaxWidth(), accentBorder = accent.copy(alpha = 0.45f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DepartmentBadge("${course.level.uppercase()} • ${course.lessonsCount} LESSONS", accent)
                        IconButton(onClick = { expandedCourseId = if (isOpen) null else course.id }) {
                            Icon(if (isOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = "Toggle lessons")
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.School, contentDescription = null, tint = accent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(course.title, style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(course.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (isOpen) {
                        Spacer(modifier = Modifier.height(10.dp))
                        course.lessons.forEach { lesson ->
                            Text(lesson, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(onClick = onOpenDepartmentLibrary) {
                            Text("Practice Related Drills")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingScreen(
    currentTier: String,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current
    var selectedCountryIndex by remember { mutableStateOf(0) }
    val currency = CatalogDepartmentsFtoJ.currencies[selectedCountryIndex]

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text(strings.pricingTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Select Your Region & Currency", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CatalogDepartmentsFtoJ.currencies.indices.toList()) { idx ->
                        val item = CatalogDepartmentsFtoJ.currencies[idx]
                        FilterChip(
                            selected = selectedCountryIndex == idx,
                            onClick = { selectedCountryIndex = idx },
                            label = { Text("${item.country} (${item.code})") }
                        )
                    }
                }
            }

            item {
                CinematicCard(elevated = true, accentBorder = ElectricBlue.copy(alpha = 0.45f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Active Plan: ${currentTier.uppercase()}", style = MaterialTheme.typography.titleMedium, color = ElectricBlue)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Payment gateway checkout is intentionally paused until merchant billing credentials are configured—no simulated charges occur.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                TierCard(
                    title = "Free Academy",
                    price = "${currency.symbol}0 / mo",
                    features = listOf(
                        "Core 10-Department Exercise Library & Smart Timer",
                        "Daily Checklist, Streaks & XP Tracking",
                        "Voice & Camera Studio Recording + Playback",
                        "7-Day & 14-Day Starter Plans"
                    ),
                    accent = EmeraldStage,
                    isCurrent = currentTier == "free"
                )
            }
            item {
                TierCard(
                    title = "ACTORVERSE Pro",
                    price = "${currency.symbol}${currency.proMonthly} / mo",
                    features = listOf(
                        "Expanded 21-Day & 30-Day Screen/Voice Programs",
                        "Deep AI Script Analysis & Personalized 7-Day Plans",
                        "Unlimited Studio AI Clarity Feedback",
                        "Audition & Credit Portfolio Tools"
                    ),
                    accent = ElectricViolet,
                    isCurrent = currentTier == "pro"
                )
            }
            item {
                TierCard(
                    title = "ACTORVERSE Premium",
                    price = "${currency.symbol}${currency.premiumMonthly} / mo",
                    features = listOf(
                        "Full 60-Day & 90-Day Conservatory Tracks",
                        "Priority Multilingual AI Coach (EN / Urdu / Hindi)",
                        "Advanced Career Readiness & Self-Tape Suites"
                    ),
                    accent = StageGold,
                    isCurrent = currentTier == "premium"
                )
            }
        }
    }
}

@Composable
private fun TierCard(
    title: String,
    price: String,
    features: List<String>,
    accent: androidx.compose.ui.graphics.Color,
    isCurrent: Boolean
) {
    CinematicCard(modifier = Modifier.fillMaxWidth(), accentBorder = accent.copy(alpha = 0.6f)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = accent)
            if (isCurrent) DepartmentBadge("CURRENT", EmeraldStage)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(price, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        features.forEach { feat ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(feat, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileEditScreen(
    profile: ActorProfile?,
    onBack: () -> Unit,
    onSaveProfile: (ActorProfile) -> Unit,
    onSignOut: () -> Unit
) {
    BackHandler { onBack() }
    var fullName by remember(profile?.userId) { mutableStateOf(profile?.fullName ?: "") }
    var stageName by remember(profile?.userId) { mutableStateOf(profile?.stageName ?: "") }
    var country by remember(profile?.userId) { mutableStateOf(profile?.country ?: "United States") }
    var city by remember(profile?.userId) { mutableStateOf(profile?.city ?: "") }
    var language by remember(profile?.userId) { mutableStateOf(profile?.preferredLanguage ?: "en") }
    var experience by remember(profile?.userId) { mutableStateOf(profile?.experienceLevel ?: "Beginner") }
    var goals by remember(profile?.userId) { mutableStateOf(profile?.actingGoals ?: "") }
    var dailyGoalText by remember(profile?.userId) { mutableStateOf((profile?.dailyGoalMinutes ?: 30L).toString()) }
    var isAdult by remember(profile?.userId) { mutableStateOf(profile?.isAdultConfirmed ?: true) }
    var hideRanking by remember(profile?.userId) { mutableStateOf(profile?.hidePublicRanking ?: false) }
    var selectedInterests by remember(profile?.userId) { mutableStateOf(profile?.interests?.toSet() ?: setOf("Film acting")) }

    val experienceOptions = listOf("Complete Beginner", "Beginner", "Intermediate", "Advanced", "Professional")
    val interestOptions = listOf(
        "Hollywood acting", "Bollywood acting", "Lollywood acting",
        "Theatre acting", "Television acting", "Film acting",
        "Commercial acting", "Voice acting", "Screen acting"
    )
    val countryOptions = listOf("United States", "Pakistan", "India", "United Kingdom", "Canada", "UAE", "Germany", "Other")

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text("Actor Profile & Preferences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Legal Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = stageName,
                    onValueChange = { stageName = it },
                    label = { Text("Professional Stage Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text("Country / Industry Region", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    countryOptions.forEach { c ->
                        FilterChip(selected = country == c, onClick = { country = c }, label = { Text(c) })
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City (e.g. Los Angeles, Karachi, Mumbai, London)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text("Preferred Language", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en" to "English", "ur" to "اردو (Urdu)", "hi" to "हिन्दी (Hindi)").forEach { (code, label) ->
                        FilterChip(selected = language == code, onClick = { language = code }, label = { Text(label) })
                    }
                }
            }
            item {
                Text("Acting Experience", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    experienceOptions.forEach { lvl ->
                        FilterChip(selected = experience == lvl, onClick = { experience = lvl }, label = { Text(lvl) })
                    }
                }
            }
            item {
                Text("Acting Interests", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    interestOptions.forEach { interest ->
                        val active = interest in selectedInterests
                        FilterChip(
                            selected = active,
                            onClick = {
                                selectedInterests = if (active) selectedInterests - interest else selectedInterests + interest
                            },
                            label = { Text(interest) }
                        )
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = goals,
                    onValueChange = { goals = it },
                    label = { Text("Acting Goals (e.g. Dramatic depth, voice-over clarity)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = dailyGoalText,
                    onValueChange = { dailyGoalText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Daily Practice Goal (Minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("I confirm I am 18+ (or have parental consent)", modifier = Modifier.weight(1f))
                        Switch(checked = isAdult, onCheckedChange = { isAdult = it })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Hide me from public community rankings", modifier = Modifier.weight(1f))
                        Switch(checked = hideRanking, onCheckedChange = { hideRanking = it })
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val base = profile ?: ActorProfile()
                        onSaveProfile(
                            base.copy(
                                fullName = fullName.trim().ifBlank { "Actor" },
                                stageName = stageName.trim().ifBlank { fullName.trim().ifBlank { "Performer" } },
                                country = country,
                                city = city.trim(),
                                preferredLanguage = language,
                                experienceLevel = experience,
                                interests = selectedInterests.toList(),
                                actingGoals = goals.trim(),
                                dailyGoalMinutes = dailyGoalText.toLongOrNull()?.coerceIn(5L, 300L) ?: 30L,
                                isAdultConfirmed = isAdult,
                                hidePublicRanking = hideRanking,
                                onboardingComplete = true
                            )
                        )
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_profile_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg)
                ) {
                    Text("Save Actor Profile", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sign_out_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonCue)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
