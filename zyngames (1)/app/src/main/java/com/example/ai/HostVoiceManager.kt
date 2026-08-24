package com.example.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class HostVoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isVoiceEnabled = MutableStateFlow(true)
    val isVoiceEnabled: StateFlow<Boolean> = _isVoiceEnabled.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("HostVoiceManager", "Error creating TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.US
            tts?.setPitch(0.92f) // Deep, resonant, mature broadcast host voice (Alex Trebek / Steve Harvey style)
            tts?.setSpeechRate(1.0f) // Clear broadcast delivery

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            Log.d("HostVoiceManager", "TTS successfully initialized")
        } else {
            Log.w("HostVoiceManager", "TTS initialization failed with status $status")
        }
    }

    fun speak(text: String, flush: Boolean = true) {
        if (!_isVoiceEnabled.value) return
        if (text.isBlank()) return

        // Clean out markdown or formatting emojis for smooth speech synthesis
        val speechText = text
            .replace(Regex("[*#_~`]"), "")
            .replace(Regex("[\\p{So}\\p{Cn}]"), "")
            .trim()

        if (isInitialized && tts != null) {
            val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            val utteranceId = "host_speech_${System.currentTimeMillis()}"
            _isSpeaking.value = true
            tts?.speak(speechText, queueMode, null, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun toggleVoiceEnabled(): Boolean {
        val newState = !_isVoiceEnabled.value
        _isVoiceEnabled.value = newState
        if (!newState) {
            stop()
        }
        return newState
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
