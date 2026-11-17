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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.ai.AiTrainingPlanResult
import com.example.ui.components.CinematicCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CrimsonCue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.StageGold
import com.example.ui.viewmodel.AiTaskState
import com.example.ui.viewmodel.CoachChatMessage

@Composable
fun AiCoachScreen(
    messages: List<CoachChatMessage>,
    streamingReply: String?,
    isChatLoading: Boolean,
    chatError: String?,
    aiPlanState: AiTaskState<AiTrainingPlanResult>,
    preferredLanguage: String,
    onLanguageChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onGeneratePlan: (dailyMinutes: Int, goals: String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showPlanBuilder by remember { mutableStateOf(false) }
    var dailyMinutesText by remember { mutableStateOf("25") }
    var goalsInput by remember { mutableStateOf("Screen presence, clear diction, and vocal stamina") }

    val quickPrompts = listOf(
        "Help me improve my acting.",
        "Give me a 20-minute voice workout.",
        "Teach me how to perform an angry character naturally.",
        "Give me a beginner monologue.",
        "Create a seven-day acting improvement plan.",
        "اردو تلفظ بہتر بنانے کی مشق بتائیں۔"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionHeader("AI Personal Acting Coach", "Supportive craft coaching • Gemini AI Logic")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = preferredLanguage == "en",
                        onClick = { onLanguageChange("en") },
                        label = { Text("EN") }
                    )
                    FilterChip(
                        selected = preferredLanguage == "ur",
                        onClick = { onLanguageChange("ur") },
                        label = { Text("اردو") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !showPlanBuilder,
                    onClick = { showPlanBuilder = false },
                    label = { Text("Coach Chat") }
                )
                FilterChip(
                    selected = showPlanBuilder,
                    onClick = { showPlanBuilder = true },
                    label = { Text("7-Day AI Plan Generator") },
                    leadingIcon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        if (showPlanBuilder) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    CinematicCard(elevated = true, accentBorder = StageGold.copy(alpha = 0.6f)) {
                        Text("Build Personalized 7-Day Plan", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = dailyMinutesText,
                            onValueChange = { dailyMinutesText = it.filter { c -> c.isDigit() } },
                            label = { Text("Daily Available Minutes") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = goalsInput,
                            onValueChange = { goalsInput = it },
                            label = { Text("Your Focus Areas & Audition Goals") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val mins = dailyMinutesText.toIntOrNull()?.coerceIn(10, 180) ?: 25
                                onGeneratePlan(mins, goalsInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Generate Structured Plan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                when (aiPlanState) {
                    is AiTaskState.Loading -> item {
                        CinematicCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Crafting your personalized 7-day actor plan...")
                            }
                        }
                    }
                    is AiTaskState.Success -> item {
                        val plan = aiPlanState.data
                        CinematicCard(elevated = true, accentBorder = ElectricBlue) {
                            Text(plan.planTitle, style = MaterialTheme.typography.headlineMedium, color = StageGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(plan.focusSummary, style = MaterialTheme.typography.bodyMedium, color = ElectricBlue)
                            Spacer(modifier = Modifier.height(10.dp))
                            plan.dailySchedule.forEach { dayItem ->
                                Text("• $dayItem", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 3.dp))
                            }
                        }
                    }
                    is AiTaskState.Error -> item {
                        CinematicCard(accentBorder = CrimsonCue) {
                            Text(aiPlanState.message, color = CrimsonCue)
                        }
                    }
                    AiTaskState.Idle -> {}
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickPrompts) { prompt ->
                    Surface(
                        onClick = { onSendMessage(prompt) },
                        shape = RoundedCornerShape(50),
                        color = ElectricViolet.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = prompt,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (msg.isUser) ElectricViolet.copy(alpha = 0.28f) else ObsidianCardElevated,
                            modifier = Modifier.fillMaxWidth(0.88f)
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                if (!streamingReply.isNullOrBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = ObsidianCardElevated,
                            modifier = Modifier.fillMaxWidth(0.88f)
                        ) {
                            Text(
                                text = streamingReply,
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else if (isChatLoading) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Coach is thinking...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                if (!chatError.isNullOrBlank()) {
                    item {
                        Surface(shape = RoundedCornerShape(12.dp), color = CrimsonCue.copy(alpha = 0.16f)) {
                            Text(chatError, modifier = Modifier.padding(12.dp), color = CrimsonCue)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask your AI Acting Coach...") },
                    modifier = Modifier.weight(1f).testTag("ai_coach_input"),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val trimmed = inputText.trim()
                        if (trimmed.isNotEmpty()) {
                            inputText = ""
                            onSendMessage(trimmed)
                        }
                    },
                    enabled = !isChatLoading,
                    modifier = Modifier.size(50.dp).testTag("ai_coach_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = StageGold)
                }
            }
        }
    }
}
