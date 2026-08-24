package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.GameShowType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiHostService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
        You are "Monte Carlo Smith", the world's most energetic, charismatic, stylish prime-time TV game show host.
        You wear bespoke sharp suits, deliver high-voltage broadcast commentary, dramatic pauses ("Survey says...!", "Let's see the board!", "Is that your final answer?!"), witty banter, hilarious comedic reactions, and authentic game show flair.
        Your style is playful, welcoming, razor-sharp, and entertaining.
        Always keep responses punchy, theatrical, engaging, under 3-4 sentences, and strictly in character as a live TV show host addressing contestants and the live studio audience.
    """.trimIndent()

    suspend fun askQuestion(gameType: GameShowType, questionContext: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            We are live on air playing ${gameType.title} (${gameType.subtitle})!
            The current question / scenario is:
            "$questionContext"
            
            As host Monte Carlo Smith, introduce this round with high TV energy, read the question dramatically to the contestants, build tension, and tell them the clock is ticking or to lock in their answers!
        """.trimIndent()
        
        callGemini(prompt, fallback = getFallbackQuestionSpeech(gameType, questionContext))
    }

    suspend fun reactToAnswer(
        gameType: GameShowType,
        contestantName: String,
        answer: String,
        isCorrect: Boolean,
        points: Int
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Contestant "$contestantName" just gave the answer "$answer" in ${gameType.title}.
            Outcome: ${if (isCorrect) "CORRECT / MATCH! Awarded $points points." else "WRONG / STRIKE / NO MATCH!"}.
            
            Give an authentic, hilarious, charismatic host reaction! If correct, celebrate and hype up the crowd. If wrong, give comedic sympathy, a witty tease, or sound the buzzer alarm!
        """.trimIndent()

        callGemini(prompt, fallback = getFallbackReaction(isCorrect, answer, contestantName))
    }

    suspend fun banterWithPlayer(userMessage: String, gameTitle: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            A contestant on stage in $gameTitle said to you: "$userMessage".
            Respond directly in your iconic charismatic TV game show host persona with quick wit, good humor, and stage presence!
        """.trimIndent()

        callGemini(prompt, fallback = "Haha! I love the energy from you! That's why you're on the main stage today! Let's keep those points rolling!")
    }

    suspend fun generateHostJoke(gameTitle: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Tell a short, clean, hilarious 1-2 line joke or playful observation suitable for a prime-time TV game show host during a break in $gameTitle.
        """.trimIndent()

        callGemini(prompt, fallback = "You know, they told me before the show: 'Monte, just look handsome and read the cards.' Well, one out of two ain't bad!")
    }

    suspend fun generateCustomAiQuestion(gameType: GameShowType, topic: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Create a brand-new, original TV game show question for ${gameType.title} on the topic of "$topic".
            Format:
            - Present the question in host dialogue.
            - Include 4-5 potential answers or options.
            Keep it entertaining, authentic, and fun!
        """.trimIndent()

        callGemini(prompt, fallback = "Alright studio audience, here's a fresh one! We asked 100 people: 'What is something you always forget when packing for vacation?' Top 5 answers on the board!")
    }

    private fun callGemini(prompt: String, fallback: String): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiHostService", "Using authentic fallback host dialogue (No external API key set)")
            return fallback
        }

        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val sysParts = JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    }
                    put("parts", sysParts)
                }
                put("systemInstruction", systemInstructionObj)

                val genConfig = JSONObject().apply {
                    put("temperature", 0.85)
                    put("topP", 0.95)
                    put("topK", 40)
                }
                put("generationConfig", genConfig)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("GeminiHostService", "API error: ${response.code} ${response.message}")
                return fallback
            }

            val responseString = response.body?.string() ?: return fallback
            val rootJson = JSONObject(responseString)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return text.trim()
                    }
                }
            }
            fallback
        } catch (e: Exception) {
            Log.e("GeminiHostService", "Exception in callGemini", e)
            fallback
        }
    }

    private fun getFallbackQuestionSpeech(gameType: GameShowType, context: String): String {
        return when (gameType) {
            GameShowType.FAMILY_FEUD ->
                "Alright families, captains step right up! We surveyed 100 people, and here is the question: \"$context\"! Buzzers ready—let's play the Feud!"
            GameShowType.THE_PRICE_IS_RIGHT ->
                "Contestants, feast your eyes on this next incredible showcase item: \"$context\"! Team consensus in the huddle—closest to the actual retail price without going over takes the points!"
            GameShowType.WHEEL_OF_FORTUNE ->
                "Look at this giant board, folks! The category is \"$context\". Spin that wheel, call out those letters, and let's solve the puzzle!"
            GameShowType.JEOPARDY ->
                "Here is the clue for the arena: \"$context\". Think fast, buzz in, and remember to phrase your response in the form of a question!"
            GameShowType.ONE_VS_100 ->
                "One on the hot seat against the entire 100-member mob! Here comes your next eliminator question: \"$context\"! Choose wisely!"
            GameShowType.DEBATE_SHOWDOWN ->
                "Order in the arena! Today's heated motion on the floor is: \"$context\". PRO squad and CON squad, take your positions—let the debate begin!"
        }
    }

    private fun getFallbackReaction(isCorrect: Boolean, answer: String, contestant: String): String {
        return if (isCorrect) {
            val cheers = listOf(
                "YES! That's what I'm talking about! $contestant, you hit the mark with '$answer'!",
                "BOOM! Survey says... IT'S ON THE BOARD! Fantastic answer, $contestant!",
                "That is 100% CORRECT! The studio audience is going wild for $contestant!"
            )
            cheers.random()
        } else {
            val roasts = listOf(
                "Ouch! ❌ Strike on the board! '$answer' was bold, but not quite what our survey said!",
                "Uh oh! That loud buzzer means no points for '$answer'! But don't lose heart, squad!",
                "That answer was creative, but the board says NO! Let's see if the other side can capitalize!"
            )
            roasts.random()
        }
    }
}
