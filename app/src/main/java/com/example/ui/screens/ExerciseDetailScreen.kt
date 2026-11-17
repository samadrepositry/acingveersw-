package com.example.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.LocalAppStrings
import com.example.data.catalog.TrainingExercise
import com.example.ui.components.CinematicCard
import com.example.ui.components.DepartmentBadge
import com.example.ui.components.departmentAccentColor
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmberOrange
import com.example.ui.theme.EmeraldStage
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exercise: TrainingExercise,
    onBack: () -> Unit,
    onCompleteSession: (durationSeconds: Long, repetitions: Long, notes: String) -> Unit,
    onNextExercise: () -> Unit
) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val accent = departmentAccentColor(exercise.departmentId)

    var totalDurationSec by remember(exercise.id) { mutableIntStateOf(exercise.durationSeconds) }
    var remainingSec by remember(exercise.id) { mutableIntStateOf(exercise.durationSeconds) }
    var isRunning by remember(exercise.id) { mutableStateOf(false) }
    var repetitions by remember(exercise.id) { mutableIntStateOf(0) }
    var soundEnabled by remember { mutableStateOf(true) }
    var voiceCueEnabled by remember { mutableStateOf(false) }
    var customSecondsInput by remember { mutableStateOf("") }
    var personalNotes by remember(exercise.id) { mutableStateOf("") }
    var sessionSaved by remember(exercise.id) { mutableStateOf(false) }

    // TextToSpeech for optional voice instructions
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.US
                ttsReady = true
            }
        }
        engine
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun playToneAlert() {
        if (!soundEnabled) return
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(isRunning, remainingSec) {
        if (isRunning && remainingSec > 0) {
            delay(1000L)
            remainingSec -= 1
            if (remainingSec == 0) {
                isRunning = false
                repetitions += 1
                playToneAlert()
                if (voiceCueEnabled && ttsReady) {
                    tts.speak("Round complete. Great focus.", TextToSpeech.QUEUE_FLUSH, null, "cue_done")
                }
            }
        }
    }

    val progress = if (totalDurationSec > 0) remainingSec.toFloat() / totalDurationSec.toFloat() else 0f
    val animatedSweep by animateFloatAsState(
        targetValue = progress * 360f,
        animationSpec = tween(durationMillis = 350),
        label = "timerRing"
    )

    Scaffold(
        containerColor = ObsidianBg,
        topBar = {
            TopAppBar(
                title = { Text(exercise.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { soundEnabled = !soundEnabled }) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.Filled.VolumeOff,
                            contentDescription = "Toggle Sound Alert"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBg)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    DepartmentBadge(exercise.departmentId.uppercase(), accent)
                    Surface(shape = RoundedCornerShape(8.dp), color = StageGold.copy(alpha = 0.16f)) {
                        Text(
                            text = exercise.difficulty,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = StageGold
                        )
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = ElectricBlue.copy(alpha = 0.16f)) {
                        Text(
                            text = "Reps: $repetitions",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue
                        )
                    }
                }
            }

            if (!exercise.safetyNote.isNullOrBlank()) {
                item {
                    CinematicCard(accentBorder = EmberOrange.copy(alpha = 0.6f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = EmberOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.safetyNoteLabel, style = MaterialTheme.typography.titleMedium, color = EmberOrange)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(exercise.safetyNote, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                // Smart Training Timer Card
                CinematicCard(elevated = true, accentBorder = accent.copy(alpha = 0.6f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(190.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                                drawArc(
                                    color = accent.copy(alpha = 0.18f),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 22f, cap = StrokeCap.Round)
                                )
                                drawArc(
                                    brush = Brush.sweepGradient(listOf(accent, StageGold, ElectricViolet, accent)),
                                    startAngle = -90f,
                                    sweepAngle = animatedSweep,
                                    useCenter = false,
                                    style = Stroke(width = 22f, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val mins = remainingSec / 60
                                val secs = remainingSec % 60
                                Text(
                                    text = "%02d:%02d".format(mins, secs),
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isRunning) "IN SESSION" else if (remainingSec == 0) "COMPLETE" else "READY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StageGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // Preset duration pills
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val presets = listOf(15 to "15s", 30 to "30s", 60 to "1m", 120 to "2m", 300 to "5m", 600 to "10m")
                            items(presets.size) { i ->
                                val (sec, label) = presets[i]
                                FilterChip(
                                    selected = totalDurationSec == sec,
                                    onClick = {
                                        isRunning = false
                                        totalDurationSec = sec
                                        remainingSec = sec
                                    },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customSecondsInput,
                                onValueChange = { customSecondsInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Custom (sec)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(
                                onClick = {
                                    val c = customSecondsInput.toIntOrNull()?.coerceIn(5, 3600)
                                    if (c != null) {
                                        isRunning = false
                                        totalDurationSec = c
                                        remainingSec = c
                                    }
                                }
                            ) { Text("Set") }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // Primary Timer Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (remainingSec == 0) remainingSec = totalDurationSec
                                    isRunning = !isRunning
                                    if (isRunning && voiceCueEnabled && ttsReady) {
                                        tts.speak("Begin ${exercise.title}. ${exercise.objective}", TextToSpeech.QUEUE_FLUSH, null, "cue_start")
                                    }
                                },
                                modifier = Modifier.weight(1f).height(50.dp).testTag("timer_toggle_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = StageGold, contentColor = ObsidianBg)
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRunning) strings.pause else if (remainingSec < totalDurationSec && remainingSec > 0) strings.resume else strings.startExercise,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    isRunning = false
                                    remainingSec = totalDurationSec
                                },
                                modifier = Modifier.height(50.dp)
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = strings.reset)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = voiceCueEnabled,
                                onClick = { voiceCueEnabled = !voiceCueEnabled },
                                label = { Text("Voice Instructions") }
                            )
                            OutlinedButton(onClick = onNextExercise) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item {
                CinematicCard {
                    Text(strings.objectiveLabel, style = MaterialTheme.typography.titleMedium, color = StageGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(exercise.objective, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Instructions", style = MaterialTheme.typography.titleMedium, color = ElectricBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(exercise.instructions, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(strings.correctTechniqueLabel, style = MaterialTheme.typography.titleMedium, color = EmeraldStage)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(exercise.correctTechnique, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(strings.commonMistakesLabel, style = MaterialTheme.typography.titleMedium, color = EmberOrange)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(exercise.commonMistakes, style = MaterialTheme.typography.bodyMedium)
                    if (!exercise.beginnerModification.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Beginner Modification", style = MaterialTheme.typography.titleMedium, color = ElectricViolet)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(exercise.beginnerModification, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                CinematicCard {
                    Text("Personal Practice Notes & Completion", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = personalNotes,
                        onValueChange = { personalNotes = it },
                        label = { Text("How did breath, resonance, or focus feel?") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val elapsed = (totalDurationSec - remainingSec).coerceAtLeast(15)
                            onCompleteSession(
                                elapsed.toLong(),
                                repetitions.coerceAtLeast(1).toLong(),
                                personalNotes.trim()
                            )
                            sessionSaved = true
                        },
                        enabled = !sessionSaved,
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("complete_exercise_button")
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (sessionSaved) "Session Saved to Progress!" else strings.completeSession)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
