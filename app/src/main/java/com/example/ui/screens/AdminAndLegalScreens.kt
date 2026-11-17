package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ModerationReport
import com.example.ui.components.CinematicCard
import com.example.ui.components.DepartmentBadge
import com.example.ui.theme.CrimsonCue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldStage
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndLegalScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text(strings.helpCenterTitle) },
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
                CinematicCard(elevated = true) {
                    Text("About ACTORVERSE", style = MaterialTheme.typography.titleLarge, color = StageGold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "ACTORVERSE is a global actor training platform uniting Hollywood, Bollywood, Lollywood, UK, European, and Middle Eastern performing traditions with breath wellness, vocal mastery, emotional craft, and supportive Gemini AI coaching.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item { Text("Frequently Asked Questions", style = MaterialTheme.typography.headlineMedium) }
            items(CatalogDepartmentsFtoJ.faqs) { (question, answer) ->
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Text(question, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(answer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            item {
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Health, Emotional Safety & Craft Ethics", style = MaterialTheme.typography.titleMedium, color = EmeraldStage)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Stop any breathing or physical warm-up immediately if you experience dizziness or discomfort. Emotional expression drills rely on imagination, sensory given circumstances, and clear after-session grounding—never unsafe trauma recall. AI feedback offers supportive craft observations and never guarantees casting.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Privacy Policy & Data Protection", style = MaterialTheme.typography.titleMedium, color = ElectricBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Your actor profile, training logs, checklist, career tracker, and studio recording metadata live inside your private Firestore account protected by zero-trust ownership rules and Firebase App Check. Audio and video takes remain stored locally on your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Terms & Contact Support", style = MaterialTheme.typography.titleMedium, color = ElectricViolet)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Community members agree to constructive, respectful peer engagement. Use the Flag icon on any post to notify moderators. Platform inquiries: support@actorverse.studio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    users: List<ActorProfile>,
    reports: List<ModerationReport>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onUpdateUser: (targetUserId: String, tier: String, suspended: Boolean) -> Unit,
    onCreateExercise: (title: String, dept: String, difficulty: String, durationSec: Long, instructions: String, objective: String) -> Unit,
    onResolveReport: (report: ModerationReport, hidePost: Boolean) -> Unit
) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchUserQuery by remember { mutableStateOf("") }

    // Exercise Creator Form
    var exTitle by remember { mutableStateOf("") }
    var exDept by remember { mutableStateOf("voice") }
    var exDiff by remember { mutableStateOf("Intermediate") }
    var exDurationText by remember { mutableStateOf("120") }
    var exInstructions by remember { mutableStateOf("") }
    var exObjective by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        onRefresh()
    }

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text(strings.adminPanelTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab, containerColor = ObsidianBg, contentColor = StageGold) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Users (${users.size})") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Publish Drill") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Reports (${reports.count { it.status == "open" }})") })
            }

            when (selectedTab) {
                0 -> {
                    val filteredUsers = remember(users, searchUserQuery) {
                        val q = searchUserQuery.trim().lowercase()
                        if (q.isBlank()) users else users.filter {
                            it.stageName.lowercase().contains(q) ||
                                it.fullName.lowercase().contains(q) ||
                                it.country.lowercase().contains(q)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            CinematicCard(elevated = true, accentBorder = ElectricBlue.copy(alpha = 0.45f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Security, contentDescription = null, tint = ElectricBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Role-Gated Firestore Administration", style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Total Actors: ${users.size} • Open Reports: ${reports.count { it.status == "open" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = searchUserQuery,
                                onValueChange = { searchUserQuery = it },
                                label = { Text("Filter by actor name or country") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        items(filteredUsers, key = { it.userId.ifBlank { it.id } }) { user ->
                            val uid = user.userId.ifBlank { user.id }
                            CinematicCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${user.stageName} (${user.fullName})", style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            "${user.country} • ${user.experienceLevel} • ${user.xp} XP",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    DepartmentBadge(
                                        if (user.isSuspended) "SUSPENDED" else user.subscriptionTier.uppercase(),
                                        if (user.isSuspended) CrimsonCue else StageGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("free", "pro", "premium").forEach { tier ->
                                        item {
                                            FilterChip(
                                                selected = user.subscriptionTier == tier,
                                                onClick = { onUpdateUser(uid, tier, user.isSuspended) },
                                                label = { Text(tier.uppercase()) }
                                            )
                                        }
                                    }
                                    item {
                                        OutlinedButton(onClick = { onUpdateUser(uid, user.subscriptionTier, !user.isSuspended) }) {
                                            Text(if (user.isSuspended) "Restore" else "Suspend")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Publish Global Custom Exercise", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Published drills appear immediately inside the 10-Department Training Library for all actors.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = exTitle,
                                onValueChange = { exTitle = it },
                                label = { Text("Exercise Title") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(CatalogDepartmentsFtoJ.allDepartments) { dept ->
                                    FilterChip(
                                        selected = exDept == dept.id,
                                        onClick = { exDept = dept.id },
                                        label = { Text(dept.code) }
                                    )
                                }
                            }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Beginner", "Intermediate", "Advanced").forEach { d ->
                                    FilterChip(selected = exDiff == d, onClick = { exDiff = d }, label = { Text(d) })
                                }
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = exDurationText,
                                onValueChange = { exDurationText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Duration (seconds)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = exObjective,
                                onValueChange = { exObjective = it },
                                label = { Text("Training Objective") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = exInstructions,
                                onValueChange = { exInstructions = it },
                                label = { Text("Step-by-Step Instructions") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            Button(
                                onClick = {
                                    if (exTitle.isNotBlank() && exInstructions.isNotBlank()) {
                                        onCreateExercise(
                                            exTitle.trim(),
                                            exDept,
                                            exDiff,
                                            exDurationText.toLongOrNull()?.coerceIn(15L, 3600L) ?: 120L,
                                            exInstructions.trim(),
                                            exObjective.trim().ifBlank { "Build actor discipline and clarity." }
                                        )
                                        exTitle = ""
                                        exInstructions = ""
                                        exObjective = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("admin_publish_exercise_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg)
                            ) {
                                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Publish Exercise Globally", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (reports.isEmpty()) {
                            item {
                                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                                    Text("No Moderation Reports", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Flagged community posts will appear here for moderator resolution.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(reports, key = { it.id }) { report ->
                                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Post ID: ${report.postId.take(12)}...", style = MaterialTheme.typography.titleMedium)
                                        DepartmentBadge(
                                            report.status.uppercase(),
                                            if (report.status == "open") CrimsonCue else EmeraldStage
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Reason: ${report.reason}", style = MaterialTheme.typography.bodyMedium)
                                    if (report.status == "open") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { onResolveReport(report, true) },
                                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonCue)
                                            ) { Text("Hide Post & Resolve") }
                                            OutlinedButton(onClick = { onResolveReport(report, false) }) {
                                                Text("Dismiss")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
