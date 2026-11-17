package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.data.ai.StudioFeedbackResult
import com.example.data.catalog.CatalogDepartmentsFtoJ
import com.example.data.model.StudioRecording
import com.example.ui.components.CinematicCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CrimsonCue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldStage
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StageGold
import com.example.ui.viewmodel.AiTaskState
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun StudiosHubScreen(
    initialTab: Int,
    recordings: List<StudioRecording>,
    studioFeedbackState: AiTaskState<StudioFeedbackResult>,
    onSaveRecording: (
        title: String,
        studioType: String,
        localFilePath: String,
        durationSeconds: Long,
        estimatedWpm: Long,
        fillerWordsCount: Long,
        framingPreset: String,
        personalNotes: String
    ) -> Unit,
    onRequestAiFeedback: (StudioRecording) -> Unit,
    onDeleteRecording: (String) -> Unit,
    monologueTabContent: @Composable () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ObsidianBg,
            contentColor = StageGold
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Voice Studio") },
                icon = { Icon(Icons.Filled.Mic, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Camera Studio") },
                icon = { Icon(Icons.Filled.Videocam, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Monologues") },
                icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) }
            )
        }

        when (selectedTab) {
            0 -> VoiceStudioView(
                recordings = recordings.filter { it.studioType == "voice" },
                studioFeedbackState = studioFeedbackState,
                onSaveRecording = onSaveRecording,
                onRequestAiFeedback = onRequestAiFeedback,
                onDeleteRecording = onDeleteRecording
            )
            1 -> CameraStudioView(
                recordings = recordings.filter { it.studioType == "camera" },
                studioFeedbackState = studioFeedbackState,
                onSaveRecording = onSaveRecording,
                onRequestAiFeedback = onRequestAiFeedback,
                onDeleteRecording = onDeleteRecording
            )
            else -> monologueTabContent()
        }
    }
}

@Composable
private fun VoiceStudioView(
    recordings: List<StudioRecording>,
    studioFeedbackState: AiTaskState<StudioFeedbackResult>,
    onSaveRecording: (String, String, String, Long, Long, Long, String, String) -> Unit,
    onRequestAiFeedback: (StudioRecording) -> Unit,
    onDeleteRecording: (String) -> Unit
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }

    var isRecording by remember { mutableStateOf(false) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var currentFilePath by remember { mutableStateOf("") }
    var takeTitle by remember { mutableStateOf("Vocal Warmup Take") }
    var spokenTextNote by remember { mutableStateOf("") }
    var lastRecordedPath by remember { mutableStateOf<String?>(null) }
    var lastDurationSec by remember { mutableIntStateOf(0) }
    val amplitudes = remember { mutableStateListOf<Float>() }

    var activePlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var playingId by remember { mutableStateOf<String?>(null) }
    var compareA by remember { mutableStateOf<StudioRecording?>(null) }
    var compareB by remember { mutableStateOf<StudioRecording?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                recorder?.stop()
                recorder?.release()
            } catch (_: Exception) {}
            try {
                activePlayer?.release()
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(150L)
                val amp = try {
                    (recorder?.maxAmplitude ?: 0) / 32767f
                } catch (_: Exception) {
                    0.05f
                }
                amplitudes.add(amp.coerceIn(0.05f, 1f))
                if (amplitudes.size > 48) amplitudes.removeAt(0)
            }
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            elapsedSec = 0
            while (isRecording) {
                delay(1000L)
                elapsedSec += 1
            }
        }
    }

    fun startRecording() {
        val dir = File(context.filesDir, "recordings").apply { mkdirs() }
        val file = File(dir, "voice_${System.currentTimeMillis()}.m4a")
        currentFilePath = file.absolutePath
        amplitudes.clear()
        val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        mr.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        recorder = mr
        isRecording = true
    }

    fun stopRecording() {
        isRecording = false
        try {
            recorder?.stop()
            recorder?.release()
        } catch (_: Exception) {}
        recorder = null
        lastRecordedPath = currentFilePath
        lastDurationSec = elapsedSec.coerceAtLeast(1)
    }

    fun playFile(id: String, path: String) {
        try {
            activePlayer?.release()
            if (playingId == id) {
                playingId = null
                activePlayer = null
                return
            }
            val file = File(path)
            if (!file.exists()) return
            val mp = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                setOnCompletionListener {
                    playingId = null
                    release()
                    activePlayer = null
                }
                start()
            }
            activePlayer = mp
            playingId = id
        } catch (_: Exception) {
            playingId = null
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("Voice Recording Studio", "Record takes, inspect live waveform, compare A/B, and check pacing")
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.img_voice_studio),
                contentDescription = "Voice Studio Booth",
                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        item {
            CinematicCard(elevated = true, accentBorder = ElectricViolet.copy(alpha = 0.5f)) {
                if (!hasMicPermission) {
                    Text("Microphone Permission Required", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Grant microphone access to record vocal warmups, analyze pacing, and play back takes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                        Text("Allow Microphone Access")
                    }
                } else {
                    // Live Waveform Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .background(ObsidianBg.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp)
                    ) {
                        val barCount = 48
                        val step = size.width / barCount
                        val midY = size.height / 2f
                        amplitudes.forEachIndexed { idx, amp ->
                            val x = idx * step + step / 2f
                            val h = (amp * size.height * 0.85f).coerceAtLeast(6f)
                            drawLine(
                                color = if (isRecording) StageGold else ElectricViolet,
                                start = Offset(x, midY - h / 2f),
                                end = Offset(x, midY + h / 2f),
                                strokeWidth = (step * 0.55f).coerceAtLeast(4f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "%02d:%02d".format(elapsedSec / 60, elapsedSec % 60),
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (isRecording) CrimsonCue else MaterialTheme.colorScheme.onSurface
                        )
                        Button(
                            onClick = { if (isRecording) stopRecording() else startRecording() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) CrimsonCue else StageGold,
                                contentColor = if (isRecording) Color.White else ObsidianBg
                            ),
                            modifier = Modifier.testTag("voice_record_toggle")
                        ) {
                            Icon(if (isRecording) Icons.Filled.Stop else Icons.Filled.FiberManualRecord, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRecording) "Stop Recording" else "Record Voice", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (!lastRecordedPath.isNullOrBlank() && !isRecording) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = takeTitle,
                            onValueChange = { takeTitle = it },
                            label = { Text("Take Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = spokenTextNote,
                            onValueChange = { spokenTextNote = it },
                            label = { Text("Optional: Paste/type lines you spoke for WPM & filler check") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { playFile("preview", lastRecordedPath!!) }) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (playingId == "preview") "Stop" else "Preview")
                            }
                            Button(onClick = {
                                val words = spokenTextNote.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                                val wpm = if (words.isNotEmpty() && lastDurationSec > 0) {
                                    ((words.size.toDouble() / lastDurationSec.toDouble()) * 60.0).toLong().coerceIn(30L, 350L)
                                } else 135L
                                val fillers = words.count {
                                    it.lowercase().trim(',', '.', '!', '?') in setOf("um", "uh", "like", "basically", "actually", "literally")
                                }.toLong()
                                onSaveRecording(
                                    takeTitle.ifBlank { "Voice Take" },
                                    "voice",
                                    lastRecordedPath!!,
                                    lastDurationSec.toLong(),
                                    wpm,
                                    fillers,
                                    "Studio Mic",
                                    spokenTextNote.ifBlank { "Saved from Voice Studio" }
                                )
                                lastRecordedPath = null
                                spokenTextNote = ""
                            }) {
                                Text("Save Practice Take")
                            }
                        }
                    }
                }
            }
        }

        // A/B Comparison Card
        if (compareA != null && compareB != null) {
            item {
                CinematicCard(accentBorder = ElectricBlue) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CompareArrows, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("A/B Take Comparison", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Take A: ${compareA!!.title}", style = MaterialTheme.typography.labelLarge, color = StageGold)
                            Text("${compareA!!.durationSeconds}s • ~${compareA!!.estimatedWpm} WPM", style = MaterialTheme.typography.bodyMedium)
                            OutlinedButton(onClick = { playFile("cmpA", compareA!!.localFilePath) }) {
                                Text(if (playingId == "cmpA") "Stop A" else "Play A")
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Take B: ${compareB!!.title}", style = MaterialTheme.typography.labelLarge, color = ElectricViolet)
                            Text("${compareB!!.durationSeconds}s • ~${compareB!!.estimatedWpm} WPM", style = MaterialTheme.typography.bodyMedium)
                            OutlinedButton(onClick = { playFile("cmpB", compareB!!.localFilePath) }) {
                                Text(if (playingId == "cmpB") "Stop B" else "Play B")
                            }
                        }
                    }
                }
            }
        }

        if (studioFeedbackState is AiTaskState.Loading) {
            item {
                CinematicCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("AI Coach is reviewing pacing & clarity...")
                    }
                }
            }
        } else if (studioFeedbackState is AiTaskState.Success) {
            item {
                val fb = studioFeedbackState.data
                CinematicCard(elevated = true, accentBorder = StageGold) {
                    Text("AI Coach Practice Feedback", style = MaterialTheme.typography.titleMedium, color = StageGold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Clarity: ${fb.clarityFeedback}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pacing: ${fb.pacingAssessment}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Next Drill: ${fb.actionableDrill}", style = MaterialTheme.typography.bodyMedium, color = ElectricBlue)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(fb.disclaimer, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else if (studioFeedbackState is AiTaskState.Error) {
            item {
                CinematicCard(accentBorder = CrimsonCue) {
                    Text(studioFeedbackState.message, color = CrimsonCue)
                }
            }
        }

        item {
            Text("Saved Voice Takes (${recordings.size})", style = MaterialTheme.typography.headlineMedium)
        }

        items(recordings, key = { it.id }) { rec ->
            CinematicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(rec.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${rec.durationSeconds}s • ~${rec.estimatedWpm} WPM • ${rec.fillerWordsCount} filler words",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { playFile(rec.id, rec.localFilePath) }) {
                        Icon(
                            imageVector = if (playingId == rec.id) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                            contentDescription = "Play or Stop",
                            tint = StageGold
                        )
                    }
                    IconButton(onClick = { onDeleteRecording(rec.id) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete recording")
                    }
                }
                if (rec.aiFeedback.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(rec.aiFeedback, style = MaterialTheme.typography.bodyMedium, color = ElectricBlue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onRequestAiFeedback(rec) }) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Clarity Feedback")
                    }
                    OutlinedButton(onClick = {
                        if (compareA == null || compareA?.id == rec.id) compareA = rec else compareB = rec
                    }) {
                        Text(if (compareA?.id == rec.id) "Set as A" else if (compareB?.id == rec.id) "Set as B" else "Compare")
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraStudioView(
    recordings: List<StudioRecording>,
    studioFeedbackState: AiTaskState<StudioFeedbackResult>,
    onSaveRecording: (String, String, String, Long, Long, Long, String, String) -> Unit,
    onRequestAiFeedback: (StudioRecording) -> Unit,
    onDeleteRecording: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        hasPermissions = (map[Manifest.permission.CAMERA] == true) && (map[Manifest.permission.RECORD_AUDIO] == true)
    }

    var framingPreset by remember { mutableStateOf("Close-Up") }
    var useFrontCamera by remember { mutableStateOf(true) }
    var isRecording by remember { mutableStateOf(false) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    var lastVideoPath by remember { mutableStateOf<String?>(null) }
    var playingVideoPath by remember { mutableStateOf<String?>(null) }
    var slateTitle by remember { mutableStateOf("Self-Tape Monologue") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            elapsedSec = 0
            while (isRecording) {
                delay(1000L)
                elapsedSec += 1
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("Camera & Self-Tape Studio", "Front-camera recording, framing guides, eye-line anchors, and playback")
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Close-Up", "Medium Shot", "Wide Shot").forEach { preset ->
                    item {
                        FilterChip(
                            selected = framingPreset == preset,
                            onClick = { framingPreset = preset },
                            label = { Text(preset) }
                        )
                    }
                }
                item {
                    FilterChip(
                        selected = useFrontCamera,
                        onClick = { if (!isRecording) useFrontCamera = !useFrontCamera },
                        label = { Text(if (useFrontCamera) "Front Cam" else "Rear Cam") }
                    )
                }
            }
        }

        item {
            if (!hasPermissions) {
                CinematicCard(elevated = true) {
                    Image(
                        painter = painterResource(id = R.drawable.img_camera_studio),
                        contentDescription = "Camera Acting Studio",
                        modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Camera & Audio Permissions Needed", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Grant Camera and Microphone permissions to practice framing, eye-lines, and self-tape recording.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = {
                        permLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                    }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enable Camera Studio")
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                        },
                        update = { previewView ->
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                            cameraProviderFuture.addListener({
                                val provider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val recorder = Recorder.Builder()
                                    .setQualitySelector(QualitySelector.from(Quality.HD))
                                    .build()
                                val vc = VideoCapture.withOutput(recorder)
                                videoCapture = vc
                                val selector = if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                                try {
                                    provider.unbindAll()
                                    provider.bindToLifecycle(lifecycleOwner, selector, preview, vc)
                                } catch (_: Exception) {
                                }
                            }, ContextCompat.getMainExecutor(context))
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Framing & Eye-Line Overlay Guide
                    val padH = when (framingPreset) {
                        "Close-Up" -> 56.dp
                        "Medium Shot" -> 32.dp
                        else -> 12.dp
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = padH, vertical = 18.dp)
                            .border(1.5.dp, StageGold.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                    )
                    // Eye-line left & right markers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 72.dp, start = 28.dp, end = 28.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(ElectricBlue, CircleShape))
                        Box(modifier = Modifier.size(10.dp).background(ElectricBlue, CircleShape))
                    }

                    // Top status pill
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                        shape = RoundedCornerShape(50),
                        color = ObsidianBg.copy(alpha = 0.78f)
                    ) {
                        Text(
                            text = if (isRecording) "REC %02d:%02d • $framingPreset".format(elapsedSec / 60, elapsedSec % 60) else "$framingPreset • Eye-Line Active",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isRecording) CrimsonCue else StageGold
                        )
                    }

                    // Record Button
                    Button(
                        onClick = {
                            if (isRecording) {
                                activeRecording?.stop()
                                isRecording = false
                            } else {
                                val vc = videoCapture ?: return@Button
                                val dir = File(context.filesDir, "selftapes").apply { mkdirs() }
                                val outFile = File(dir, "tape_${System.currentTimeMillis()}.mp4")
                                val opts = FileOutputOptions.Builder(outFile).build()
                                @Suppress("MissingPermission")
                                val rec = vc.output
                                    .prepareRecording(context, opts)
                                    .withAudioEnabled()
                                    .start(ContextCompat.getMainExecutor(context)) { event ->
                                        if (event is VideoRecordEvent.Finalize && !event.hasError()) {
                                            lastVideoPath = outFile.absolutePath
                                        }
                                    }
                                activeRecording = rec
                                isRecording = true
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(14.dp)
                            .testTag("camera_record_toggle"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) CrimsonCue else StageGold,
                            contentColor = if (isRecording) Color.White else ObsidianBg
                        )
                    ) {
                        Icon(if (isRecording) Icons.Filled.Stop else Icons.Filled.FiberManualRecord, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRecording) "Stop Self-Tape" else "Start Self-Tape", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (!lastVideoPath.isNullOrBlank() && !isRecording) {
            item {
                CinematicCard(elevated = true, accentBorder = EmeraldStage) {
                    Text("Save Self-Tape Take", style = MaterialTheme.typography.titleMedium, color = EmeraldStage)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = slateTitle,
                        onValueChange = { slateTitle = it },
                        label = { Text("Scene / Slate Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Performance Notes (eye-line, lighting, audio)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { playingVideoPath = lastVideoPath }) {
                            Text("Watch Playback")
                        }
                        Button(onClick = {
                            onSaveRecording(
                                slateTitle.ifBlank { "Self-Tape" },
                                "camera",
                                lastVideoPath!!,
                                elapsedSec.coerceAtLeast(1).toLong(),
                                130L,
                                0L,
                                framingPreset,
                                notes.ifBlank { "$framingPreset self-tape practice" }
                            )
                            lastVideoPath = null
                            notes = ""
                        }) {
                            Text("Save Take")
                        }
                    }
                }
            }
        }

        if (!playingVideoPath.isNullOrBlank()) {
            item {
                CinematicCard(accentBorder = ElectricBlue) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Self-Tape Playback", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { playingVideoPath = null }) { Text("Close") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val controller = MediaController(ctx)
                                controller.setAnchorView(this)
                                setMediaController(controller)
                                setVideoURI(Uri.fromFile(File(playingVideoPath!!)))
                                start()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }

        if (studioFeedbackState is AiTaskState.Success) {
            item {
                val fb = studioFeedbackState.data
                CinematicCard(elevated = true, accentBorder = StageGold) {
                    Text("AI Self-Tape Practice Guidance", style = MaterialTheme.typography.titleMedium, color = StageGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(fb.clarityFeedback, style = MaterialTheme.typography.bodyMedium)
                    Text(fb.actionableDrill, style = MaterialTheme.typography.bodyMedium, color = ElectricBlue)
                    Text(fb.disclaimer, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Text("Saved Self-Tapes (${recordings.size})", style = MaterialTheme.typography.headlineMedium)
        }

        items(recordings, key = { it.id }) { rec ->
            CinematicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(rec.title, style = MaterialTheme.typography.titleMedium)
                        Text("${rec.framingPreset} • ${rec.durationSeconds}s", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { playingVideoPath = rec.localFilePath }) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play self-tape", tint = StageGold)
                    }
                    IconButton(onClick = { onDeleteRecording(rec.id) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete self-tape")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onRequestAiFeedback(rec) }) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Practice Review")
                    }
                }
            }
        }
    }
}
