package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.core.LocalAppStrings
import com.example.data.catalog.CatalogDepartmentsFtoJ
import com.example.data.model.ActorProfile
import com.example.data.model.ChecklistItem
import com.example.data.model.ExerciseSession
import com.example.data.model.StudioRecording
import com.example.ui.components.CinematicCard
import com.example.ui.components.SkillProgressRing
import com.example.ui.components.WeeklyPracticeChart
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmberOrange
import com.example.ui.theme.EmeraldStage
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold
import java.util.Calendar

enum class SubDestination {
    ANALYTICS,
    TRAINING_PLANS,
    COURSES,
    CAREER_CENTER,
    PRICING,
    PROFILE_EDIT,
    HELP_LEGAL,
    ADMIN_PANEL
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    profile: ActorProfile?,
    sessions: List<ExerciseSession>,
    checklist: List<ChecklistItem>,
    recordings: List<StudioRecording>,
    isAdmin: Boolean,
    onToggleChecklist: (ChecklistItem) -> Unit,
    onAddChecklistItem: (String, String, String, Long) -> Unit,
    onDeleteChecklistItem: (String) -> Unit,
    onOpenExercise: (String) -> Unit,
    onOpenAiCoach: () -> Unit,
    onOpenSubDestination: (SubDestination) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    var showAddScheduleDialog by remember { mutableStateOf(false) }

    val weeklyData = remember(sessions) { computeWeeklyMinutes(sessions) }
    val totalMinutes = ((profile?.totalPracticeSeconds ?: 0L) / 60L).toInt()
    val xp = profile?.xp ?: 0L
    val level = (xp / 250L) + 1L

    val unlockedCount = remember(profile, sessions, recordings) {
        var count = 0
        if ((profile?.exercisesCompleted ?: 0L) >= 1L) count++
        if (recordings.isNotEmpty()) count++
        if ((profile?.streakDays ?: 0L) >= 7L) count++
        if ((profile?.streakDays ?: 0L) >= 30L) count++
        if (sessions.count { it.departmentId == "voice" } >= 5) count++
        if (sessions.count { it.departmentId == "breathing" } >= 3) count++
        if (sessions.count { it.departmentId == "diction" } >= 5) count++
        if (sessions.count { it.departmentId == "monologue" } >= 3) count++
        if (recordings.count { it.studioType == "camera" } >= 3) count++
        if (sessions.count { it.departmentId == "improvisation" } >= 5) count++
        if ((profile?.exercisesCompleted ?: 0L) >= 10L) count++
        if (xp >= 1000L) count++
        count
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Welcome Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF2B1B54), Color(0xFF1A1438), Color(0xFF0E243B))
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome back, ${profile?.stageName?.ifBlank { "Performer" } ?: "Performer"}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                            Text(
                                text = "${profile?.experienceLevel ?: "Beginner"} • Level $level • $xp XP",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StageGold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = (profile?.preferredLanguage ?: "en") == "en",
                                onClick = { onLanguageChange("en") },
                                label = { Text("EN") }
                            )
                            FilterChip(
                                selected = profile?.preferredLanguage == "ur",
                                onClick = { onLanguageChange("ur") },
                                label = { Text("اردو") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBadge(
                            icon = Icons.Filled.LocalFireDepartment,
                            label = strings.streakLabel,
                            value = "${profile?.streakDays ?: 0}d",
                            tint = EmberOrange,
                            modifier = Modifier.weight(1f)
                        )
                        StatBadge(
                            icon = Icons.Filled.CalendarMonth,
                            label = strings.practiceTimeLabel,
                            value = "${totalMinutes}m",
                            tint = ElectricBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatBadge(
                            icon = Icons.Filled.EmojiEvents,
                            label = strings.completedLabel,
                            value = "${profile?.exercisesCompleted ?: 0}",
                            tint = StageGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (profile != null && !profile.onboardingComplete) {
            item {
                CinematicCard(elevated = true, accentBorder = StageGold.copy(alpha = 0.6f)) {
                    Text("Complete Your Actor Profile", style = MaterialTheme.typography.titleMedium, color = StageGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Set your country, language, acting goals, and experience level so AI Coach tailors your workouts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onOpenSubDestination(SubDestination.PROFILE_EDIT) },
                        colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg),
                        modifier = Modifier.testTag("complete_profile_button")
                    ) {
                        Text("Customize Actor Profile", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Skill-Specific Progress Rings
            CinematicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Skill-Specific Progress", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Tracked individually from your completed sessions—no single misleading score.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SkillProgressRing(strings.voiceScoreLabel, (profile?.voiceScore ?: 10L).toInt(), ElectricViolet)
                    SkillProgressRing(strings.actingScoreLabel, (profile?.actingScore ?: 10L).toInt(), StageGold)
                    SkillProgressRing(strings.movementScoreLabel, (profile?.movementScore ?: 10L).toInt(), EmeraldStage)
                }
            }
        }

        item {
            // Quick Action Hub (Courses, Plans, Analytics, Career, Pricing, Profile, Help, Admin)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickChipTile(Icons.Filled.CalendarMonth, "Plans", ElectricViolet) {
                    onOpenSubDestination(SubDestination.TRAINING_PLANS)
                }
                QuickChipTile(Icons.Filled.School, "Courses", ElectricBlue) {
                    onOpenSubDestination(SubDestination.COURSES)
                }
                QuickChipTile(Icons.Filled.Analytics, "Analytics", StageGold) {
                    onOpenSubDestination(SubDestination.ANALYTICS)
                }
                QuickChipTile(Icons.Filled.WorkOutline, "Career", EmberOrange) {
                    onOpenSubDestination(SubDestination.CAREER_CENTER)
                }
                QuickChipTile(Icons.Filled.Payments, "Pricing", EmeraldStage) {
                    onOpenSubDestination(SubDestination.PRICING)
                }
                QuickChipTile(Icons.Filled.Person, "Profile", Color(0xFFA78BFA)) {
                    onOpenSubDestination(SubDestination.PROFILE_EDIT)
                }
                QuickChipTile(Icons.Filled.HelpOutline, "Help & Legal", Color(0xFF94A3B8)) {
                    onOpenSubDestination(SubDestination.HELP_LEGAL)
                }
                if (isAdmin) {
                    QuickChipTile(Icons.Filled.AdminPanelSettings, "Admin Panel", Color(0xFFF43F5E)) {
                        onOpenSubDestination(SubDestination.ADMIN_PANEL)
                    }
                }
            }
        }

        item {
            // AI Coach Recommendation Banner
            CinematicCard(elevated = true, accentBorder = ElectricViolet.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = StageGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("AI Acting Coach", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Get a custom 20-minute workout, monologue analysis, or Urdu/English coaching.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpenAiCoach,
                        modifier = Modifier.testTag("dashboard_ai_coach_button")
                    ) {
                        Text("Open AI Coach")
                    }
                    OutlinedButton(onClick = { onOpenExercise("a1") }) {
                        Text("30s Quick Start: Breath")
                    }
                }
            }
        }

        item {
            WeeklyPracticeChart(dailyMinutes = weeklyData)
        }

        item {
            // Daily Training Checklist Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.dailyChecklist, style = MaterialTheme.typography.headlineMedium)
                    val done = checklist.count { it.isCompleted }
                    Text(
                        "$done of ${checklist.size} completed today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = { showAddScheduleDialog = true },
                    modifier = Modifier.testTag("add_checklist_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Customize")
                }
            }
        }

        val groups = listOf("morning" to strings.morning, "afternoon" to strings.afternoon, "evening" to strings.evening)
        groups.forEach { (key, label) ->
            val groupItems = checklist.filter { it.timeOfDay == key }
            if (groupItems.isNotEmpty()) {
                item {
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = StageGold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                items(groupItems, key = { it.id }) { item ->
                    CinematicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleChecklist(item) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onToggleChecklist(item) }) {
                                Icon(
                                    imageVector = if (item.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = if (item.isCompleted) "Completed" else "Incomplete",
                                    tint = if (item.isCompleted) EmeraldStage else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${item.durationMinutes} min • ${item.departmentId.replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onDeleteChecklistItem(item.id) }) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = "Remove checklist item",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Achievements Summary Strip
            Text("Achievements ($unlockedCount / ${CatalogDepartmentsFtoJ.achievements.size})", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(CatalogDepartmentsFtoJ.achievements.take(6)) { badge ->
                    val unlocked = when (badge.id) {
                        "first_exercise" -> (profile?.exercisesCompleted ?: 0L) >= 1L
                        "first_recording" -> recordings.isNotEmpty()
                        "streak_7" -> (profile?.streakDays ?: 0L) >= 7L
                        "consistent_actor" -> (profile?.exercisesCompleted ?: 0L) >= 10L
                        "pro_milestone" -> xp >= 1000L
                        else -> false
                    }
                    CinematicCard(
                        modifier = Modifier.width(170.dp),
                        accentBorder = if (unlocked) StageGold else null
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = if (unlocked) StageGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(badge.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (unlocked) "Unlocked (+${badge.xpBonus} XP)" else badge.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (unlocked) StageGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddScheduleDialog) {
        AddScheduleItemDialog(
            onDismiss = { showAddScheduleDialog = false },
            onConfirm = { title, timeOfDay, dept, mins ->
                onAddChecklistItem(title, timeOfDay, dept, mins)
                showAddScheduleDialog = false
            }
        )
    }
}

@Composable
private fun StatBadge(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.72f))
            }
        }
    }
}

@Composable
private fun QuickChipTile(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = tint.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun AddScheduleItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("morning") }
    var department by remember { mutableStateOf("voice") }
    var minutesText by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Daily Practice Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Activity Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("morning", "afternoon", "evening").forEach { slot ->
                        FilterChip(
                            selected = timeOfDay == slot,
                            onClick = { timeOfDay = slot },
                            label = { Text(slot.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Minutes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mins = minutesText.toLongOrNull()?.coerceIn(1L, 180L) ?: 10L
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), timeOfDay, department, mins)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun computeWeeklyMinutes(sessions: List<ExerciseSession>): List<Pair<String, Int>> {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val buckets = IntArray(7)
    val cal = Calendar.getInstance()
    val nowMillis = System.currentTimeMillis()
    val sevenDaysMs = 7L * 24L * 60L * 60L * 1000L

    sessions.forEach { s ->
        val ts = s.createdAt?.toDate()?.time ?: return@forEach
        if (nowMillis - ts <= sevenDaysMs) {
            cal.timeInMillis = ts
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val idx = when (dow) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                else -> 6
            }
            buckets[idx] += (s.durationSeconds / 60L).toInt().coerceAtLeast(1)
        }
    }
    return dayNames.mapIndexed { i, name -> name to buckets[i] }
}
