package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WarningAmber
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
import com.example.data.catalog.CatalogDepartmentsFtoJ
import com.example.data.catalog.TrainingExercise
import com.example.data.model.CustomExercise
import com.example.ui.components.CinematicCard
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.departmentAccentColor
import com.example.ui.theme.CrimsonCue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmberOrange
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold

@Composable
fun TrainingLibraryScreen(
    favoriteIds: Set<String>,
    customExercises: List<CustomExercise>,
    onToggleFavorite: (String) -> Unit,
    onSelectExercise: (TrainingExercise) -> Unit,
    onOpenMonologues: () -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenCameraStudio: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDeptId by remember { mutableStateOf("all") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var selectedDictionLang by remember { mutableStateOf("All") }
    var favoritesOnly by remember { mutableStateOf(false) }

    // Dept F Random Emotion state
    var activeEmotionIndex by remember { mutableStateOf(0) }
    var emotionTab by remember { mutableStateOf("Facial") }

    // Dept H Random Improv Generator state
    var improvLoc by remember { mutableStateOf(CatalogDepartmentsFtoJ.improvLocations.first()) }
    var improvChar by remember { mutableStateOf(CatalogDepartmentsFtoJ.improvCharacters.first()) }
    var improvConflict by remember { mutableStateOf(CatalogDepartmentsFtoJ.improvConflicts.first()) }
    var improvTwist by remember { mutableStateOf(CatalogDepartmentsFtoJ.improvTwists.first()) }

    val mergedExercises = remember(customExercises) {
        val adminOnes = customExercises.map {
            TrainingExercise(
                id = "custom_${it.id}",
                departmentId = it.departmentId,
                title = "${it.title} (Academy)",
                difficulty = it.difficulty,
                durationSeconds = it.durationSeconds.toInt(),
                objective = it.objective.ifBlank { "Studio practice drill" },
                instructions = it.instructions,
                correctTechnique = it.correctTechnique.ifBlank { "Stay grounded and relaxed." },
                commonMistakes = it.commonMistakes.ifBlank { "Rushing without intention." }
            )
        }
        CatalogDepartmentsFtoJ.allExercises + adminOnes
    }

    val filteredExercises = remember(
        mergedExercises, searchQuery, selectedDeptId, selectedDifficulty, selectedDictionLang, favoritesOnly, favoriteIds
    ) {
        mergedExercises.filter { ex ->
            val matchDept = selectedDeptId == "all" || ex.departmentId == selectedDeptId
            val matchDiff = selectedDifficulty == "All" || ex.difficulty.equals(selectedDifficulty, ignoreCase = true)
            val matchLang = selectedDeptId != "diction" || selectedDictionLang == "All" ||
                ex.languageTag.equals(selectedDictionLang, ignoreCase = true) || ex.languageTag == "Global"
            val matchFav = !favoritesOnly || ex.id in favoriteIds
            val matchQuery = searchQuery.isBlank() ||
                ex.title.contains(searchQuery, ignoreCase = true) ||
                ex.objective.contains(searchQuery, ignoreCase = true) ||
                ex.languageTag.contains(searchQuery, ignoreCase = true)
            matchDept && matchDiff && matchLang && matchFav && matchQuery
        }
    }

    val selectedDept = CatalogDepartmentsFtoJ.allDepartments.firstOrNull { it.id == selectedDeptId }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Training Library",
                subtitle = "10 Departments • Breathing, Body, Voice, Diction, Craft, Emotions, Improv & Camera"
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search exercises, languages, or techniques...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("library_search_input")
            )
        }

        item {
            // Department horizontal selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedDeptId == "all",
                        onClick = { selectedDeptId = "all" },
                        label = { Text("All (10 Depts)") }
                    )
                }
                items(CatalogDepartmentsFtoJ.allDepartments) { dept ->
                    FilterChip(
                        selected = selectedDeptId == dept.id,
                        onClick = { selectedDeptId = dept.id },
                        label = { Text("${dept.code}: ${dept.title.substringBefore(" ")}") }
                    )
                }
            }
        }

        item {
            // Difficulty & Favorites filter row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = favoritesOnly,
                        onClick = { favoritesOnly = !favoritesOnly },
                        label = { Text("Favorites (${favoriteIds.size})") },
                        leadingIcon = { Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
                listOf("All", "Beginner", "Intermediate", "Advanced").forEach { diff ->
                    item {
                        FilterChip(
                            selected = selectedDifficulty == diff,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(diff) }
                        )
                    }
                }
            }
        }

        if (selectedDept != null) {
            item {
                val accent = departmentAccentColor(selectedDept.id)
                CinematicCard(elevated = true, accentBorder = accent.copy(alpha = 0.6f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DepartmentBadge(selectedDept.code, accent)
                        if (selectedDept.id == "voice") {
                            OutlinedButton(onClick = onOpenVoiceStudio) { Text("Open Voice Studio") }
                        } else if (selectedDept.id == "camera") {
                            OutlinedButton(onClick = onOpenCameraStudio) { Text("Open Camera Studio") }
                        } else if (selectedDept.id == "monologue") {
                            OutlinedButton(onClick = onOpenMonologues) { Text("Open Scripts Library") }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(selectedDept.title, style = MaterialTheme.typography.titleLarge)
                    Text(selectedDept.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (!selectedDept.safetyNote.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = EmberOrange.copy(alpha = 0.14f)) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = EmberOrange, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(selectedDept.safetyNote, style = MaterialTheme.typography.bodyMedium, color = EmberOrange)
                            }
                        }
                    }
                }
            }
        }

        // Language filter for Dept D
        if (selectedDeptId == "diction") {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "English", "American English", "British English", "Urdu", "Hindi", "Punjabi", "Sindhi", "Arabic").forEach { lang ->
                        item {
                            FilterChip(
                                selected = selectedDictionLang == lang,
                                onClick = { selectedDictionLang = lang },
                                label = { Text(lang) }
                            )
                        }
                    }
                }
            }
        }

        // Dept F: Interactive Emotional Expression Lab (All 25 Emotions + Random Generator)
        if (selectedDeptId == "emotion" || selectedDeptId == "all") {
            item {
                val emotion = CatalogDepartmentsFtoJ.emotionPacks[activeEmotionIndex]
                CinematicCard(elevated = true, accentBorder = CrimsonCue.copy(alpha = 0.5f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            DepartmentBadge("DEPT F • 25 EMOTIONS LAB", CrimsonCue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(emotion.emotion, style = MaterialTheme.typography.headlineMedium)
                        }
                        Button(
                            onClick = {
                                activeEmotionIndex = (CatalogDepartmentsFtoJ.emotionPacks.indices).random()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg)
                        ) {
                            Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Random", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CatalogDepartmentsFtoJ.emotionPacks.indices.toList()) { idx ->
                            val pack = CatalogDepartmentsFtoJ.emotionPacks[idx]
                            FilterChip(
                                selected = idx == activeEmotionIndex,
                                onClick = { activeEmotionIndex = idx },
                                label = { Text(pack.emotion) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Facial", "Body", "Voice", "Dialogue", "Silent", "Camera").forEach { mode ->
                            item {
                                FilterChip(
                                    selected = emotionTab == mode,
                                    onClick = { emotionTab = mode },
                                    label = { Text(mode) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val promptText = when (emotionTab) {
                        "Facial" -> emotion.facialPrompt
                        "Body" -> emotion.bodyPrompt
                        "Voice" -> emotion.voicePrompt
                        "Dialogue" -> emotion.dialoguePrompt
                        "Silent" -> emotion.silentPrompt
                        else -> emotion.cameraPrompt
                    }
                    Text(promptText, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            onSelectExercise(
                                TrainingExercise(
                                    id = "emotion_${emotion.id}",
                                    departmentId = "emotion",
                                    title = "${emotion.emotion} — $emotionTab Drill",
                                    difficulty = "All Levels",
                                    durationSeconds = 90,
                                    objective = "Explore ${emotion.emotion} through safe imagination and physical/vocal choices.",
                                    instructions = "Facial: ${emotion.facialPrompt}\nBody: ${emotion.bodyPrompt}\nVoice: ${emotion.voicePrompt}\nDialogue: ${emotion.dialoguePrompt}\nSilent: ${emotion.silentPrompt}\nCamera: ${emotion.cameraPrompt}",
                                    correctTechnique = "Focus on objective, breath, and body posture—stay grounded in safe imagination.",
                                    commonMistakes = "Forcing tension or trying to recall painful personal memories.",
                                    safetyNote = "Never force tears or relive trauma. Use safe imaginary circumstances."
                                )
                            )
                        }) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start Timed Drill")
                        }
                        OutlinedButton(onClick = onOpenCameraStudio) {
                            Text("Record Take")
                        }
                    }
                }
            }
        }

        // Dept H: Interactive Improvisation Challenge Generator
        if (selectedDeptId == "improvisation") {
            item {
                CinematicCard(elevated = true, accentBorder = StageGold.copy(alpha = 0.6f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Improv Challenge Generator", style = MaterialTheme.typography.titleLarge, color = StageGold)
                        Button(
                            onClick = {
                                improvLoc = CatalogDepartmentsFtoJ.improvLocations.random()
                                improvChar = CatalogDepartmentsFtoJ.improvCharacters.random()
                                improvConflict = CatalogDepartmentsFtoJ.improvConflicts.random()
                                improvTwist = CatalogDepartmentsFtoJ.improvTwists.random()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg)
                        ) {
                            Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Challenge", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Location: $improvLoc", style = MaterialTheme.typography.bodyLarge)
                    Text("Character: $improvChar", style = MaterialTheme.typography.bodyLarge)
                    Text("Conflict: $improvConflict", style = MaterialTheme.typography.bodyLarge)
                    Text("Twist at 30s: $improvTwist", style = MaterialTheme.typography.bodyMedium, color = ElectricViolet)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = {
                        onSelectExercise(
                            TrainingExercise(
                                id = "improv_challenge",
                                departmentId = "improvisation",
                                title = "Live Improv Challenge",
                                difficulty = "Intermediate",
                                durationSeconds = 60,
                                objective = "Commit immediately to location, character, conflict, and 30-second twist.",
                                instructions = "Location: $improvLoc\nCharacter: $improvChar\nConflict: $improvConflict\nTwist: $improvTwist",
                                correctTechnique = "Say 'Yes, and...' to every impulse and play the objective boldly.",
                                commonMistakes = "Stopping to think or apologizing mid-scene."
                            )
                        )
                    }) {
                        Text("Start 60s Improv Timer")
                    }
                }
            }
        }

        // Dept J Monologues shortcut when Monologues tab picked
        if (selectedDeptId == "monologue") {
            item {
                CinematicCard(elevated = true, accentBorder = ElectricViolet) {
                    Text("Explore Full Original Monologue Library", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "8 original scripts across Drama, Comedy, Romance, Thriller, Suspense, Tragedy, Action & Film—with Memorization Mode and AI Script Analysis.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = onOpenMonologues) {
                        Text("Open Monologues & Script Analyzer")
                    }
                }
            }
        }

        items(filteredExercises, key = { it.id }) { ex ->
            val accent = departmentAccentColor(ex.departmentId)
            val isFav = ex.id in favoriteIds
            CinematicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectExercise(ex) }
                    .testTag("exercise_card_${ex.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DepartmentBadge(ex.departmentId.uppercase(), accent)
                            Text(
                                text = ex.difficulty,
                                style = MaterialTheme.typography.labelSmall,
                                color = StageGold
                            )
                            if (ex.languageTag != "Global") {
                                Text(
                                    text = ex.languageTag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricViolet
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(ex.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            ex.objective,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onToggleFavorite(ex.id) }) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFav) "Remove favorite" else "Favorite",
                            tint = if (isFav) CrimsonCue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = accent)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${ex.durationSeconds}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { onSelectExercise(ex) },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Train")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
