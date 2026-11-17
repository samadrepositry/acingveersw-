package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.example.data.ai.ScriptAnalysisResult
import com.example.data.catalog.CatalogDepartmentsFtoJ
import com.example.data.catalog.MonologueItem
import com.example.ui.components.CinematicCard
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CrimsonCue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold
import com.example.ui.viewmodel.AiTaskState
import kotlinx.coroutines.delay

@Composable
fun MonologueLibraryScreen(
    scriptAnalysisState: AiTaskState<ScriptAnalysisResult>,
    onAnalyzeScript: (scriptText: String, characterContext: String) -> Unit,
    onLogMonologuePractice: (MonologueItem, Long) -> Unit
) {
    var selectedGenre by remember { mutableStateOf("All") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var activeMonologue by remember { mutableStateOf(CatalogDepartmentsFtoJ.monologues.first()) }
    var memorizationMode by remember { mutableStateOf(false) }
    var timerRunning by remember { mutableStateOf(false) }
    var remainingSec by remember(activeMonologue.id) { mutableIntStateOf(activeMonologue.estimatedSeconds) }

    LaunchedEffect(timerRunning, remainingSec) {
        if (timerRunning && remainingSec > 0) {
            delay(1000L)
            remainingSec -= 1
            if (remainingSec == 0) timerRunning = false
        }
    }

    val filtered = remember(selectedGenre, selectedDifficulty) {
        CatalogDepartmentsFtoJ.monologues.filter { m ->
            (selectedGenre == "All" || m.genre == selectedGenre) &&
                (selectedDifficulty == "All" || m.difficulty == selectedDifficulty)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionHeader("Monologue & Script Library", "Original practice scripts with beat analysis & recall tools")
                }
                Button(
                    onClick = {
                        activeMonologue = CatalogDepartmentsFtoJ.monologues.random()
                        timerRunning = false
                        remainingSec = activeMonologue.estimatedSeconds
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg)
                ) {
                    Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Random", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("All", "Drama", "Comedy", "Romance", "Thriller", "Suspense", "Tragedy", "Action", "Emotional Drama").forEach { g ->
                    item {
                        FilterChip(
                            selected = selectedGenre == g,
                            onClick = { selectedGenre = g },
                            label = { Text(g) }
                        )
                    }
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("All", "Beginner", "Intermediate", "Advanced").forEach { d ->
                    item {
                        FilterChip(
                            selected = selectedDifficulty == d,
                            onClick = { selectedDifficulty = d },
                            label = { Text(d) }
                        )
                    }
                }
            }
        }

        // Active Monologue Reader & Script Analyzer
        item {
            CinematicCard(elevated = true, accentBorder = ElectricViolet.copy(alpha = 0.6f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DepartmentBadge("${activeMonologue.genre} • ${activeMonologue.medium}", ElectricViolet)
                    DepartmentBadge(activeMonologue.difficulty, StageGold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(activeMonologue.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(activeMonologue.characterDescription, style = MaterialTheme.typography.bodyMedium, color = StageGold)
                Spacer(modifier = Modifier.height(8.dp))

                val displayedScript = remember(activeMonologue.id, memorizationMode) {
                    if (!memorizationMode) {
                        activeMonologue.scriptLines
                    } else {
                        activeMonologue.scriptLines.split(" ").mapIndexed { idx, word ->
                            if (idx % 3 == 1) "_____" else word
                        }.joinToString(" ")
                    }
                }
                Text(displayedScript, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = memorizationMode,
                        onClick = { memorizationMode = !memorizationMode },
                        label = { Text("Memorization Quiz") },
                        leadingIcon = { Icon(Icons.Filled.VisibilityOff, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Button(
                        onClick = {
                            if (remainingSec == 0) remainingSec = activeMonologue.estimatedSeconds
                            timerRunning = !timerRunning
                        }
                    ) {
                        Icon(if (timerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("%02d:%02d".format(remainingSec / 60, remainingSec % 60))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Craft Breakdown", style = MaterialTheme.typography.titleMedium, color = ElectricBlue)
                Text("Given Circumstances: ${activeMonologue.givenCircumstances}", style = MaterialTheme.typography.bodyMedium)
                Text("Objective: ${activeMonologue.sceneObjective}", style = MaterialTheme.typography.bodyMedium)
                Text("Obstacle: ${activeMonologue.obstacles}", style = MaterialTheme.typography.bodyMedium)
                Text("Tactics: ${activeMonologue.tactics}", style = MaterialTheme.typography.bodyMedium)
                Text("Subtext: ${activeMonologue.subtext}", style = MaterialTheme.typography.bodyMedium, color = StageGold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onAnalyzeScript(activeMonologue.scriptLines, activeMonologue.characterDescription) },
                        modifier = Modifier.testTag("ai_analyze_monologue_btn")
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Deep Script Analysis")
                    }
                    OutlinedButton(onClick = {
                        onLogMonologuePractice(activeMonologue, (activeMonologue.estimatedSeconds - remainingSec).coerceAtLeast(30).toLong())
                    }) {
                        Text("Log Practice")
                    }
                }
            }
        }

        when (scriptAnalysisState) {
            is AiTaskState.Loading -> item {
                CinematicCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("AI Acting Coach is breaking down beats & subtext...")
                    }
                }
            }
            is AiTaskState.Success -> item {
                val res = scriptAnalysisState.data
                CinematicCard(elevated = true, accentBorder = StageGold) {
                    Text("AI Script & Beat Analysis", style = MaterialTheme.typography.titleLarge, color = StageGold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Given Circumstances: ${res.givenCircumstances}", style = MaterialTheme.typography.bodyMedium)
                    Text("Objective: ${res.sceneObjective}", style = MaterialTheme.typography.bodyMedium)
                    Text("Obstacle: ${res.primaryObstacle}", style = MaterialTheme.typography.bodyMedium)
                    Text("Tactics: ${res.tactics.joinToString(" • ")}", style = MaterialTheme.typography.bodyMedium, color = ElectricBlue)
                    Text("Action Verbs: ${res.actionVerbs.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                    Text("Beats:\n• ${res.beats.joinToString("\n• ")}", style = MaterialTheme.typography.bodyMedium)
                    Text("Subtext: ${res.subtext}", style = MaterialTheme.typography.bodyMedium, color = StageGold)
                }
            }
            is AiTaskState.Error -> item {
                CinematicCard(accentBorder = CrimsonCue) {
                    Text(scriptAnalysisState.message, color = CrimsonCue)
                }
            }
            AiTaskState.Idle -> {}
        }

        item {
            Text("All Practice Scripts (${filtered.size})", style = MaterialTheme.typography.headlineMedium)
        }

        items(filtered, key = { it.id }) { item ->
            CinematicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Text("${item.genre} • ${item.medium} • ${item.difficulty} • ~${item.estimatedSeconds}s",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(onClick = {
                        activeMonologue = item
                        timerRunning = false
                        remainingSec = item.estimatedSeconds
                    }) {
                        Text(if (activeMonologue.id == item.id) "Active" else "Load")
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
