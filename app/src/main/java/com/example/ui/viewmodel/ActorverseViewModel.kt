package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiCoachRepository
import com.example.data.ai.AiTrainingPlanResult
import com.example.data.ai.ScriptAnalysisResult
import com.example.data.ai.StudioFeedbackResult
import com.example.data.model.ActorProfile
import com.example.data.model.CareerItem
import com.example.data.model.ChecklistItem
import com.example.data.model.CommunityPost
import com.example.data.model.CustomExercise
import com.example.data.model.ExerciseSession
import com.example.data.model.ModerationReport
import com.example.data.model.StudioRecording
import com.example.data.repository.ActorverseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CoachChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String
)

sealed interface AiTaskState<out T> {
    data object Idle : AiTaskState<Nothing>
    data object Loading : AiTaskState<Nothing>
    data class Success<T>(val data: T) : AiTaskState<T>
    data class Error(val message: String) : AiTaskState<Nothing>
}

class ActorverseViewModel(
    private val repository: ActorverseRepository,
    private val aiRepository: AiCoachRepository,
    val currentUserId: String,
    private val userDisplayName: String
) : ViewModel() {

    val profile: StateFlow<ActorProfile?> = repository.observeProfile(currentUserId)
        .catch { e ->
            if (e is CancellationException) throw e
            Log.e(TAG, "Error observing profile", e)
            _statusMessage.value = e.localizedMessage ?: "Failed to load profile"
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val sessions: StateFlow<List<ExerciseSession>> = repository.observeSessions(currentUserId)
        .catch { e ->
            if (e is CancellationException) throw e
            Log.e(TAG, "Error observing sessions", e)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val checklist: StateFlow<List<ChecklistItem>> = repository.observeChecklist(currentUserId)
        .catch { e ->
            if (e is CancellationException) throw e
            Log.e(TAG, "Error observing checklist", e)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val recordings: StateFlow<List<StudioRecording>> = repository.observeRecordings(currentUserId)
        .catch { e ->
            if (e is CancellationException) throw e
            Log.e(TAG, "Error observing recordings", e)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val careerItems: StateFlow<List<CareerItem>> = repository.observeCareerItems(currentUserId)
        .catch { e ->
            if (e is CancellationException) throw e
            Log.e(TAG, "Error observing career items", e)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val communityPosts: StateFlow<List<CommunityPost>> = repository.observeCommunityPosts(currentUserId)
        .catch { e ->
            if (e is CancellationException) throw e
            Log.e(TAG, "Error observing community posts", e)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val customExercises: StateFlow<List<CustomExercise>> = repository.observePublishedCustomExercises(currentUserId)
        .catch { e ->
            if (e is CancellationException) throw e
            Log.e(TAG, "Error observing custom exercises", e)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Local favorites and language override before profile syncs
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // AI Coach Chat state
    private val _chatMessages = MutableStateFlow(
        listOf(
            CoachChatMessage(
                id = "welcome",
                isUser = false,
                text = "Welcome to your ACTORVERSE studio! Ask me for a 20-minute vocal warm-up, emotional craft tips, script analysis, audition prep, or a 7-day plan—in English or Urdu."
            )
        )
    )
    val chatMessages: StateFlow<List<CoachChatMessage>> = _chatMessages.asStateFlow()

    private val _streamingReply = MutableStateFlow<String?>(null)
    val streamingReply: StateFlow<String?> = _streamingReply.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private val _scriptAnalysisState = MutableStateFlow<AiTaskState<ScriptAnalysisResult>>(AiTaskState.Idle)
    val scriptAnalysisState: StateFlow<AiTaskState<ScriptAnalysisResult>> = _scriptAnalysisState.asStateFlow()

    private val _aiPlanState = MutableStateFlow<AiTaskState<AiTrainingPlanResult>>(AiTaskState.Idle)
    val aiPlanState: StateFlow<AiTaskState<AiTrainingPlanResult>> = _aiPlanState.asStateFlow()

    private val _studioFeedbackState = MutableStateFlow<AiTaskState<StudioFeedbackResult>>(AiTaskState.Idle)
    val studioFeedbackState: StateFlow<AiTaskState<StudioFeedbackResult>> = _studioFeedbackState.asStateFlow()

    // Admin data
    private val _adminUsers = MutableStateFlow<List<ActorProfile>>(emptyList())
    val adminUsers: StateFlow<List<ActorProfile>> = _adminUsers.asStateFlow()

    private val _adminReports = MutableStateFlow<List<ModerationReport>>(emptyList())
    val adminReports: StateFlow<List<ModerationReport>> = _adminReports.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.ensureProfileInitialized(userDisplayName)
                _isAdmin.value = repository.checkIsAdmin()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Profile initialization failed", e)
                _statusMessage.value = e.localizedMessage ?: "Failed to initialize actor profile."
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun toggleFavorite(exerciseId: String) {
        val current = _favoriteIds.value
        _favoriteIds.value = if (exerciseId in current) current - exerciseId else current + exerciseId
    }

    fun updateLanguage(langCode: String) {
        val current = profile.value ?: return
        updateProfile(current.copy(preferredLanguage = langCode))
    }

    fun updateProfile(updated: ActorProfile) {
        viewModelScope.launch {
            try {
                repository.updateProfile(updated)
                _statusMessage.value = "Profile saved."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to update profile."
            }
        }
    }

    fun toggleChecklist(item: ChecklistItem) {
        viewModelScope.launch {
            try {
                repository.toggleChecklistItem(item)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Could not update checklist."
            }
        }
    }

    fun addChecklistItem(title: String, timeOfDay: String, departmentId: String, minutes: Long) {
        viewModelScope.launch {
            try {
                repository.addChecklistItem(title, timeOfDay, departmentId, minutes)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Could not add schedule item."
            }
        }
    }

    fun deleteChecklistItem(itemId: String) {
        viewModelScope.launch {
            try {
                repository.deleteChecklistItem(itemId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Could not remove schedule item."
            }
        }
    }

    fun logCompletedSession(
        exerciseId: String,
        exerciseTitle: String,
        departmentId: String,
        durationSeconds: Long,
        repetitions: Long,
        notes: String
    ) {
        viewModelScope.launch {
            try {
                repository.logCompletedSession(
                    exerciseId = exerciseId,
                    exerciseTitle = exerciseTitle,
                    departmentId = departmentId,
                    durationSeconds = durationSeconds,
                    repetitions = repetitions,
                    notes = notes
                )
                _statusMessage.value = "Session saved! XP and skill progress updated."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to save session."
            }
        }
    }

    fun saveRecording(
        title: String,
        studioType: String,
        localFilePath: String,
        durationSeconds: Long,
        estimatedWpm: Long,
        fillerWordsCount: Long,
        framingPreset: String,
        personalNotes: String
    ) {
        viewModelScope.launch {
            try {
                val rec = StudioRecording(
                    title = title,
                    studioType = studioType,
                    localFilePath = localFilePath,
                    durationSeconds = durationSeconds,
                    estimatedWpm = estimatedWpm,
                    fillerWordsCount = fillerWordsCount,
                    framingPreset = framingPreset,
                    personalNotes = personalNotes
                )
                repository.saveRecording(rec)
                _statusMessage.value = "Recording saved to your private studio."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to save recording."
            }
        }
    }

    fun deleteRecording(recordingId: String) {
        viewModelScope.launch {
            try {
                repository.deleteRecording(recordingId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to delete recording."
            }
        }
    }

    fun requestStudioFeedback(recording: StudioRecording) {
        viewModelScope.launch {
            _studioFeedbackState.value = AiTaskState.Loading
            try {
                val lang = profile.value?.preferredLanguage ?: "en"
                val result = aiRepository.generateStudioFeedback(
                    studioType = recording.studioType,
                    durationSeconds = recording.durationSeconds,
                    estimatedWpm = recording.estimatedWpm.toInt(),
                    fillerWordsCount = recording.fillerWordsCount.toInt(),
                    framingOrNotes = "${recording.framingPreset}. ${recording.personalNotes}",
                    languageCode = lang
                )
                _studioFeedbackState.value = AiTaskState.Success(result)
                val summary = "${result.clarityFeedback}\n\nPacing: ${result.pacingAssessment}\n\nNext Drill: ${result.actionableDrill}\n\n(${result.disclaimer})"
                if (recording.id.isNotBlank()) {
                    repository.updateRecordingFeedback(recording.id, summary, recording.personalNotes)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _studioFeedbackState.value = AiTaskState.Error(e.localizedMessage ?: "AI feedback unavailable.")
            }
        }
    }

    fun addCareerItem(itemType: String, title: String, subtitle: String, status: String, notes: String, mediaUri: String = "") {
        viewModelScope.launch {
            try {
                repository.addCareerItem(
                    CareerItem(
                        itemType = itemType,
                        title = title,
                        subtitle = subtitle,
                        status = status,
                        notes = notes,
                        mediaUri = mediaUri
                    )
                )
                _statusMessage.value = "Added to Career Center."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to add career entry."
            }
        }
    }

    fun deleteCareerItem(itemId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCareerItem(itemId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to remove item."
            }
        }
    }

    fun createCommunityPost(category: String, title: String, body: String) {
        val currentProfile = profile.value
        val stageName = currentProfile?.stageName?.ifBlank { userDisplayName } ?: userDisplayName
        val country = currentProfile?.country ?: "Global"
        val lang = currentProfile?.preferredLanguage ?: "en"
        viewModelScope.launch {
            try {
                repository.createCommunityPost(stageName, country, lang, category, title, body)
                _statusMessage.value = "Post published to Global Actor Community."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to publish post."
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.likeCommunityPost(postId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Could not like post."
            }
        }
    }

    fun reportPost(postId: String, reason: String) {
        viewModelScope.launch {
            try {
                repository.reportCommunityPost(postId, reason)
                _statusMessage.value = "Report submitted for moderator review."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to submit report."
            }
        }
    }

    // --- AI Coach ---

    fun sendCoachMessage(prompt: String) {
        if (prompt.isBlank() || _isChatLoading.value) return
        val historyPairs = _chatMessages.value
            .drop(1)
            .chunked(2)
            .mapNotNull { pair ->
                if (pair.size == 2 && pair[0].isUser && !pair[1].isUser) {
                    pair[0].text to pair[1].text
                } else null
            }
        val userMsg = CoachChatMessage(id = "u_${System.currentTimeMillis()}", isUser = true, text = prompt)
        _chatMessages.value = _chatMessages.value + userMsg
        _chatError.value = null
        _isChatLoading.value = true
        _streamingReply.value = ""

        val lang = profile.value?.preferredLanguage ?: "en"
        viewModelScope.launch {
            var latest = ""
            try {
                aiRepository.streamCoachReply(historyPairs, prompt, lang).collect { chunk ->
                    latest = chunk
                    _streamingReply.value = chunk
                }
                _chatMessages.value = _chatMessages.value + CoachChatMessage(
                    id = "m_${System.currentTimeMillis()}",
                    isUser = false,
                    text = latest
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Coach streaming failed", e)
                _chatError.value = e.localizedMessage ?: "AI Coach request failed. Please check connection and App Check."
            } finally {
                _streamingReply.value = null
                _isChatLoading.value = false
            }
        }
    }

    fun analyzeScriptWithAi(scriptText: String, characterContext: String) {
        viewModelScope.launch {
            _scriptAnalysisState.value = AiTaskState.Loading
            try {
                val lang = profile.value?.preferredLanguage ?: "en"
                val res = aiRepository.analyzeScript(scriptText, characterContext, lang)
                _scriptAnalysisState.value = AiTaskState.Success(res)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _scriptAnalysisState.value = AiTaskState.Error(e.localizedMessage ?: "Script analysis failed.")
            }
        }
    }

    fun generateAiTrainingPlan(dailyMinutes: Int, goals: String) {
        viewModelScope.launch {
            _aiPlanState.value = AiTaskState.Loading
            try {
                val p = profile.value
                val res = aiRepository.generatePersonalizedPlan(
                    experienceLevel = p?.experienceLevel ?: "Beginner",
                    interests = p?.interests?.ifEmpty { listOf("Film acting", "Voice acting") } ?: listOf("Film acting"),
                    dailyMinutes = dailyMinutes,
                    goals = goals,
                    languageCode = p?.preferredLanguage ?: "en"
                )
                _aiPlanState.value = AiTaskState.Success(res)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _aiPlanState.value = AiTaskState.Error(e.localizedMessage ?: "Failed to generate training plan.")
            }
        }
    }

    // --- Admin Actions ---

    fun loadAdminData() {
        viewModelScope.launch {
            try {
                _adminUsers.value = repository.adminFetchAllUsers()
                _adminReports.value = repository.adminFetchReports()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Admin access required."
            }
        }
    }

    fun adminUpdateUser(targetUserId: String, tier: String, suspended: Boolean) {
        viewModelScope.launch {
            try {
                repository.adminUpdateUserAccount(targetUserId, tier, suspended)
                _adminUsers.value = repository.adminFetchAllUsers()
                _statusMessage.value = "Account updated."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to update user."
            }
        }
    }

    fun adminCreateCustomExercise(
        title: String,
        departmentId: String,
        difficulty: String,
        durationSeconds: Long,
        instructions: String,
        objective: String
    ) {
        viewModelScope.launch {
            try {
                repository.adminCreateExercise(
                    CustomExercise(
                        title = title,
                        departmentId = departmentId,
                        difficulty = difficulty,
                        durationSeconds = durationSeconds,
                        instructions = instructions,
                        objective = objective,
                        correctTechnique = "Focus on relaxed breath and honest intent.",
                        commonMistakes = "Rushing through beats without listening.",
                        isPublished = true
                    )
                )
                _statusMessage.value = "Exercise published to global library."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to publish exercise."
            }
        }
    }

    fun adminResolveReport(report: ModerationReport, hidePost: Boolean) {
        viewModelScope.launch {
            try {
                repository.adminResolveReport(report.id, report.postId, hidePost)
                _adminReports.value = repository.adminFetchReports()
                _statusMessage.value = if (hidePost) "Post hidden & report resolved." else "Report dismissed."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _statusMessage.value = e.localizedMessage ?: "Failed to resolve report."
            }
        }
    }

    private companion object {
        const val TAG = "ActorverseVM"
    }
}
