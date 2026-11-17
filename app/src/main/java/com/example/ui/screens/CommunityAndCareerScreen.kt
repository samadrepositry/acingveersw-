package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.LocalAppStrings
import com.example.data.model.ActorProfile
import com.example.data.model.CareerItem
import com.example.data.model.CommunityPost
import com.example.ui.components.CinematicCard
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CrimsonCue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldStage
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold

@Composable
fun ConnectHubScreen(
    profile: ActorProfile?,
    posts: List<CommunityPost>,
    careerItems: List<CareerItem>,
    onCreatePost: (category: String, title: String, body: String) -> Unit,
    onLikePost: (String) -> Unit,
    onReportPost: (postId: String, reason: String) -> Unit,
    onAddCareerItem: (itemType: String, title: String, subtitle: String, status: String, notes: String, mediaUri: String) -> Unit,
    onDeleteCareerItem: (String) -> Unit,
    onToggleHideRanking: (Boolean) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ObsidianBg,
            contentColor = StageGold
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Community") },
                icon = { Icon(Icons.Filled.Groups, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Career Center") },
                icon = { Icon(Icons.Filled.WorkOutline, contentDescription = null) }
            )
        }

        if (selectedTab == 0) {
            CommunityTabContent(
                profile = profile,
                posts = posts,
                onCreatePost = onCreatePost,
                onLikePost = onLikePost,
                onReportPost = onReportPost,
                onToggleHideRanking = onToggleHideRanking
            )
        } else {
            CareerCenterContent(
                careerItems = careerItems,
                onAddCareerItem = onAddCareerItem,
                onDeleteCareerItem = onDeleteCareerItem
            )
        }
    }
}

@Composable
private fun CommunityTabContent(
    profile: ActorProfile?,
    posts: List<CommunityPost>,
    onCreatePost: (String, String, String) -> Unit,
    onLikePost: (String) -> Unit,
    onReportPost: (String, String) -> Unit,
    onToggleHideRanking: (Boolean) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var reportingPostId by remember { mutableStateOf<String?>(null) }

    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == "all") posts else posts.filter { it.category == selectedCategory }
    }

    Scaffold(
        containerColor = ObsidianBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = StageGold,
                contentColor = ObsidianBg,
                modifier = Modifier.testTag("create_community_post_fab")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create Community Post")
            }
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
                SectionHeader(
                    title = "Global Actor Community",
                    subtitle = "Supportive craft discussions, monologue challenges, and scene partners worldwide."
                )
                Spacer(modifier = Modifier.height(8.dp))
                CinematicCard(elevated = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Community Privacy", style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (profile?.hidePublicRanking == true) {
                                    "Public leaderboard ranking hidden"
                                } else {
                                    "Stage name visible on community posts"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilterChip(
                            selected = profile?.hidePublicRanking == true,
                            onClick = { onToggleHideRanking(!(profile?.hidePublicRanking ?: false)) },
                            label = { Text(if (profile?.hidePublicRanking == true) "Private" else "Public") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categories = listOf("all", "discussion", "challenge", "collaboration", "feedback")
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            if (filteredPosts.isEmpty()) {
                item {
                    CinematicCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Start the Conversation", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Share a vocal breakthrough, ask a craft question, or invite scene partners across Hollywood, Bollywood, Lollywood, UK, and Europe.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = { showCreateDialog = true }) {
                            Text("Write First Post")
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
                    CinematicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DepartmentBadge(post.category.uppercase(), ElectricViolet)
                            Text(
                                text = "${post.country} • ${post.language.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(post.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            post.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "By ${post.authorStageName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = StageGold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { onLikePost(post.id) }) {
                                    Icon(Icons.Filled.ThumbUp, contentDescription = "Like post", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${post.likesCount}")
                                }
                                IconButton(onClick = { reportingPostId = post.id }) {
                                    Icon(
                                        Icons.Filled.Flag,
                                        contentDescription = "Report post",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(64.dp)) }
        }
    }

    if (showCreateDialog) {
        CreateCommunityPostDialog(
            onDismiss = { showCreateDialog = false },
            onSubmit = { cat, title, body ->
                onCreatePost(cat, title, body)
                showCreateDialog = false
            }
        )
    }

    reportingPostId?.let { targetId ->
        ReportPostDialog(
            onDismiss = { reportingPostId = null },
            onSubmit = { reason ->
                onReportPost(targetId, reason)
                reportingPostId = null
            }
        )
    }
}

@Composable
fun CareerCenterContent(
    careerItems: List<CareerItem>,
    onAddCareerItem: (String, String, String, String, String, String) -> Unit,
    onDeleteCareerItem: (String) -> Unit
) {
    var activeFilter by remember { mutableStateOf("all") }
    var showAddDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onAddCareerItem(
                "headshot",
                "Actor Headshot",
                "Portfolio Headshot",
                "Ready",
                "Selected securely via Android Photo Picker",
                uri.toString()
            )
        }
    }

    val filtered = remember(careerItems, activeFilter) {
        if (activeFilter == "all") careerItems else careerItems.filter { it.itemType == activeFilter }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Audition & Career Center",
                subtitle = "Organize credits, track auditions, set career goals, and store headshots."
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg),
                    modifier = Modifier.testTag("add_career_entry_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Entry", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("pick_headshot_button")
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Headshot")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("all", "audition", "credit", "goal", "headshot").forEach { type ->
                    item {
                        FilterChip(
                            selected = activeFilter == type,
                            onClick = { activeFilter = type },
                            label = { Text(type.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }
        }

        item {
            CinematicCard(elevated = true, accentBorder = ElectricBlue.copy(alpha = 0.45f)) {
                Text("Self-Tape & Audition Readiness Checklist", style = MaterialTheme.typography.titleMedium, color = ElectricBlue)
                Spacer(modifier = Modifier.height(6.dp))
                val tips = listOf(
                    "Clean neutral background & soft key light at eye level",
                    "Landscape framing for Film/TV unless casting requests vertical",
                    "Quiet room audio check & crisp 5-second slate",
                    "Clear objective & strong eye-line anchor off-camera"
                )
                tips.forEach { tip ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldStage, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(tip, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("No Career Entries Yet", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Log upcoming auditions, theatre/film credits, career milestones, or add headshots using Photo Picker.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filtered, key = { it.id }) { item ->
                CinematicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DepartmentBadge(item.itemType.uppercase(), StageGold)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricViolet.copy(alpha = 0.16f)
                        ) {
                            Text(
                                item.status,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricViolet
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (item.mediaUri.isNotBlank()) {
                        AsyncImage(
                            model = item.mediaUri,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            if (item.subtitle.isNotBlank()) {
                                Text(item.subtitle, style = MaterialTheme.typography.bodyMedium, color = StageGold)
                            }
                            if (item.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { onDeleteCareerItem(item.id) }) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete entry", tint = CrimsonCue)
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showAddDialog) {
        AddCareerItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, title, sub, status, notes ->
                onAddCareerItem(type, title, sub, status, notes, "")
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerCenterStandaloneScreen(
    careerItems: List<CareerItem>,
    onBack: () -> Unit,
    onAddCareerItem: (String, String, String, String, String, String) -> Unit,
    onDeleteCareerItem: (String) -> Unit
) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text(strings.careerCenterTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CareerCenterContent(
                careerItems = careerItems,
                onAddCareerItem = onAddCareerItem,
                onDeleteCareerItem = onDeleteCareerItem
            )
        }
    }
}

@Composable
private fun CreateCommunityPostDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var category by remember { mutableStateOf("discussion") }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share with Global Actors") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("discussion", "challenge", "collaboration", "feedback").forEach { cat ->
                        item {
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Post Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Your message or prompt") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && body.isNotBlank()) {
                        onSubmit(category, title.trim(), body.trim())
                    }
                }
            ) { Text("Publish") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ReportPostDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var reason by remember { mutableStateOf("Off-topic or disrespectful behavior") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Community Post") },
        text = {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason for review") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(reason.ifBlank { "Community policy review" }) },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonCue)
            ) { Text("Submit Report") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AddCareerItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var itemType by remember { mutableStateOf("audition") }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Preparing") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Career Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("audition", "credit", "goal").forEach { t ->
                        item {
                            FilterChip(
                                selected = itemType == t,
                                onClick = { itemType = t },
                                label = { Text(t.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project / Goal Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Role / Studio / Medium") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status (e.g. Preparing, Taped, Booked)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Scene Beats") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(itemType, title.trim(), subtitle.trim(), status.trim(), notes.trim())
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
