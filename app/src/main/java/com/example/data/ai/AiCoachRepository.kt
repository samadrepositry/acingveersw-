package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

data class ScriptAnalysisResult(
    val givenCircumstances: String,
    val sceneObjective: String,
    val primaryObstacle: String,
    val tactics: List<String>,
    val actionVerbs: List<String>,
    val beats: List<String>,
    val subtext: String
)

data class AiTrainingPlanResult(
    val planTitle: String,
    val focusSummary: String,
    val dailySchedule: List<String>
)

data class StudioFeedbackResult(
    val clarityFeedback: String,
    val pacingAssessment: String,
    val actionableDrill: String,
    val disclaimer: String
)

class AiCoachRepository {
    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are the ACTORVERSE AI Acting Coach — a supportive, constructive, world-class acting, voice, breath, movement, and screen-performance coach serving actors globally (Hollywood, Bollywood, Lollywood, Theatre, TV, Voice-Over). " +
                        "Respond warmly and practically with actionable craft guidance. Never judge an actor's worth, never guarantee casting outcomes, and never recommend unsafe emotional or breath-holding techniques. " +
                        "If the user asks in Urdu or selects Urdu, reply clearly in Urdu; otherwise reply in English."
                )
            }
        )
    }

    private val scriptAnalysisSchema = Schema.obj(
        properties = mapOf(
            "givenCircumstances" to Schema.string(description = "Who, where, when, and relationship context"),
            "sceneObjective" to Schema.string(description = "What the character urgently wants in the scene"),
            "primaryObstacle" to Schema.string(description = "What stands in the character's way"),
            "tactics" to Schema.array(items = Schema.string(), description = "Concrete playable tactics used"),
            "actionVerbs" to Schema.array(items = Schema.string(), description = "Transitive acting verbs"),
            "beats" to Schema.array(items = Schema.string(), description = "Dramatic shifts or beats in the script"),
            "subtext" to Schema.string(description = "What remains unsaid beneath the lines")
        )
    )

    private val scriptAnalysisModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = scriptAnalysisSchema
            }
        )
    }

    private val planSchema = Schema.obj(
        properties = mapOf(
            "planTitle" to Schema.string(description = "Title of personalized actor training plan"),
            "focusSummary" to Schema.string(description = "Summary of focus areas and coaching approach"),
            "dailySchedule" to Schema.array(items = Schema.string(), description = "7 daily actionable routines")
        )
    )

    private val planModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = planSchema
            }
        )
    }

    private val feedbackSchema = Schema.obj(
        properties = mapOf(
            "clarityFeedback" to Schema.string(description = "Supportive observations on articulation and delivery"),
            "pacingAssessment" to Schema.string(description = "Guidance on measured speaking pace and pauses"),
            "actionableDrill" to Schema.string(description = "One specific 2-minute drill to try next"),
            "disclaimer" to Schema.string(description = "Clear note that automated feedback is practice guidance, not a casting judgment")
        )
    )

    private val feedbackModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = feedbackSchema
            }
        )
    }

    fun streamCoachReply(
        historyPairs: List<Pair<String, String>>,
        userPrompt: String,
        languageCode: String
    ): Flow<String> = flow {
        val chatHistory = historyPairs.flatMap { (userMsg, modelMsg) ->
            listOf(
                content("user") { text(userMsg) },
                content("model") { text(modelMsg) }
            )
        }
        val langHint = if (languageCode == "ur") "[Respond in Urdu / اردو] " else ""
        val chat = chatModel.startChat(history = chatHistory)
        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            chat.sendMessageStream(langHint + userPrompt).collect { chunk ->
                val part = chunk.text.orEmpty()
                if (part.isNotEmpty()) {
                    accumulated.append(part)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("AI Coach returned an empty response.")
        }
    }

    suspend fun analyzeScript(
        scriptText: String,
        characterContext: String,
        languageCode: String
    ): ScriptAnalysisResult {
        return try {
            val langInstruction = if (languageCode == "ur") "Provide field values in Urdu." else "Provide field values in English."
            val prompt = """
                Analyze this original acting practice script or monologue using practical craft tools.
                $langInstruction
                Character/Context: $characterContext
                Script:
                $scriptText
            """.trimIndent()
            val response = withTimeout(60_000L) {
                scriptAnalysisModel.generateContent(prompt)
            }
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty AI response"))
            val tacticsArr = json.getJSONArray("tactics")
            val verbsArr = json.getJSONArray("actionVerbs")
            val beatsArr = json.getJSONArray("beats")
            ScriptAnalysisResult(
                givenCircumstances = json.getString("givenCircumstances"),
                sceneObjective = json.getString("sceneObjective"),
                primaryObstacle = json.getString("primaryObstacle"),
                tactics = (0 until tacticsArr.length()).map { tacticsArr.getString(it) },
                actionVerbs = (0 until verbsArr.length()).map { verbsArr.getString(it) },
                beats = (0 until beatsArr.length()).map { beatsArr.getString(it) },
                subtext = json.getString("subtext")
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Script analysis failed", e)
            throw e
        }
    }

    suspend fun generatePersonalizedPlan(
        experienceLevel: String,
        interests: List<String>,
        dailyMinutes: Int,
        goals: String,
        languageCode: String
    ): AiTrainingPlanResult {
        return try {
            val langInstruction = if (languageCode == "ur") "Respond in Urdu." else "Respond in English."
            val prompt = """
                Create a personalized 7-day actor training plan. $langInstruction
                Experience Level: $experienceLevel
                Interests: ${interests.joinToString(", ")}
                Daily Available Minutes: $dailyMinutes
                Acting Goals: $goals
            """.trimIndent()
            val response = withTimeout(60_000L) {
                planModel.generateContent(prompt)
            }
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty AI response"))
            val scheduleArr = json.getJSONArray("dailySchedule")
            AiTrainingPlanResult(
                planTitle = json.getString("planTitle"),
                focusSummary = json.getString("focusSummary"),
                dailySchedule = (0 until scheduleArr.length()).map { scheduleArr.getString(it) }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Training plan generation failed", e)
            throw e
        }
    }

    suspend fun generateStudioFeedback(
        studioType: String,
        durationSeconds: Long,
        estimatedWpm: Int,
        fillerWordsCount: Int,
        framingOrNotes: String,
        languageCode: String
    ): StudioFeedbackResult {
        return try {
            val langInstruction = if (languageCode == "ur") "Respond in Urdu." else "Respond in English."
            val prompt = """
                Provide supportive actor studio practice feedback. $langInstruction
                Studio Type: $studioType
                Recording Duration: ${durationSeconds}s
                Estimated Speaking Pace: $estimatedWpm WPM
                Filler Words Detected: $fillerWordsCount
                Context / Framing / Actor Notes: $framingOrNotes
                Remember: never present this as a casting judgment or definitive acting score.
            """.trimIndent()
            val response = withTimeout(60_000L) {
                feedbackModel.generateContent(prompt)
            }
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty AI response"))
            StudioFeedbackResult(
                clarityFeedback = json.getString("clarityFeedback"),
                pacingAssessment = json.getString("pacingAssessment"),
                actionableDrill = json.getString("actionableDrill"),
                disclaimer = json.getString("disclaimer")
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Studio feedback failed", e)
            throw e
        }
    }

    private companion object {
        const val TAG = "AiCoachRepository"
    }
}
