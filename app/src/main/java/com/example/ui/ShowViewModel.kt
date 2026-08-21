package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    // Mode toggles
    val selectedEconomy: EconomyMode = EconomyMode.REAL_CASH,
    val selectedCategory: GameCategory = GameCategory.TEAM_GAMES,
    val selectedGameType: GameShowType? = null,
    val isHostMode: Boolean = false, // Host Mode enables secret host prompter / answers view
    val showHostSecretSheet: Boolean = false,
    
    // User Balances
    val realCashBalance: Double = 1450.00,
    val freePointsBalance: Int = 42500,
    
    // Matchmaking & Teams
    val isMatchmaking: Boolean = false,
    val activeGameStarted: Boolean = false,
    val currentTeam: Team? = null,
    val opposingTeam: Team? = null,
    val teamMessages: List<TeamChatMessage> = emptyList(),
    val isMicMuted: Boolean = false,
    val timerSeconds: Int = 30,
    val isTimerRunning: Boolean = false,

    // Preloaded Data Indices & Current States
    // 1. Family Feud
    val feudRoundIndex: Int = 0,
    val currentFeudRound: PreloadedFeudRound = PreloadedGameData.feudRounds[0],
    val currentFeudAnswers: List<FeudAnswer> = PreloadedGameData.feudRounds[0].answers,
    val feudStrikes: Int = 0,
    val feudBank: Int = 0,

    // 2. The Price Is Right
    val priceItemIndex: Int = 0,
    val currentPriceItem: PreloadedPriceItem = PreloadedGameData.priceItems[0],
    val myPriceGuess: String = "",
    val teamMedianGuess: Int? = null,
    val oppTeamMedianGuess: Int? = null,
    val isPriceSubmitted: Boolean = false,
    val priceResultRevealed: Boolean = false,

    // 3. Wheel of Fortune
    val wheelPuzzleIndex: Int = 0,
    val currentWheelPuzzle: PreloadedWheelPuzzle = PreloadedGameData.wheelPuzzles[0],
    val guessedLetters: Set<Char> = emptySet(),
    val wheelMultiplier: Int = 500,
    val isSpinningWheel: Boolean = false,

    // 4. Jeopardy!
    val jeopardyClues: List<PreloadedJeopardyClue> = PreloadedGameData.jeopardyClues,
    val selectedJeopardyClue: PreloadedJeopardyClue? = null,
    val jeopardyPlayerScore: Int = 0,
    val jeopardyAnswerFeedback: String? = null,

    // 5. 1 vs. 100
    val mobQuestionIndex: Int = 0,
    val currentMobQuestion: PreloadedMobQuestion = PreloadedGameData.mobQuestions[0],
    val mobCount: Int = 100,
    val mobPrizePoolCash: Int = 15000,
    val mobPrizePoolPoints: Int = 300000,
    val selectedMobOption: Int? = null,
    val isMobAnswered: Boolean = false,

    val toastMessage: String? = null
)

class ShowViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val squadNames = listOf(
        Pair("Team Volt Blue", 0xFF00B4D8),
        Pair("Team Crimson Titans", 0xFFE63946),
        Pair("Team Gold Pulse", 0xFFFFB703),
        Pair("Team Emerald Synths", 0xFF2EC4B6)
    )

    fun selectEconomy(economy: EconomyMode) {
        _uiState.update { it.copy(selectedEconomy = economy) }
    }

    fun selectCategory(category: GameCategory) {
        _uiState.update { it.copy(selectedCategory = category, selectedGameType = null) }
    }

    fun toggleHostMode() {
        _uiState.update { it.copy(isHostMode = !it.isHostMode) }
    }

    fun toggleHostSecretSheet() {
        _uiState.update { it.copy(showHostSecretSheet = !it.showHostSecretSheet) }
    }

    fun selectGame(gameType: GameShowType) {
        _uiState.update { it.copy(selectedGameType = gameType, isMatchmaking = true) }
        startMatchmaking(gameType)
    }

    private fun startMatchmaking(gameType: GameShowType) {
        viewModelScope.launch {
            delay(1200) // Fast dynamic matchmaking
            val mySquadInfo = squadNames.random()
            val oppSquadInfo = squadNames.filter { it.first != mySquadInfo.first }.random()

            val myTeamMembers = listOf(
                Player("1", "You (Player)", "😎", isCurrentUser = true, isCaptain = false, score = 150),
                Player("2", "QuickBuzz_Mike", "⚡", isCaptain = true, score = 380),
                Player("3", "TriviaQueen_Jen", "👑", score = 290),
                Player("4", "LuckySpinner", "🎡", score = 210),
                Player("5", "PriceMaster_Tom", "🏷️", score = 310)
            )

            val oppTeamMembers = listOf(
                Player("6", "RivalCaptain_Dan", "🔥", isCaptain = true, score = 340),
                Player("7", "SonicWave", "🔊", score = 260),
                Player("8", "BrainySam", "🧠", score = 280),
                Player("9", "StarGazer", "✨", score = 230)
            )

            val initialChat = listOf(
                TeamChatMessage("c1", "QuickBuzz_Mike", "⚡", "Welcome squad! Host is live on stage. Keep chat active for suggestions!", "Just now"),
                TeamChatMessage("c2", "TriviaQueen_Jen", "👑", "Ready! Let's take the win today! 🔥", "Just now")
            )

            val defaultFeud = PreloadedGameData.feudRounds[0]
            val defaultPrice = PreloadedGameData.priceItems[0]
            val defaultWheel = PreloadedGameData.wheelPuzzles[0]

            _uiState.update {
                it.copy(
                    isMatchmaking = false,
                    activeGameStarted = true,
                    currentTeam = Team(
                        id = "team_my",
                        name = mySquadInfo.first,
                        colorHex = mySquadInfo.second,
                        members = myTeamMembers,
                        totalScore = 1340
                    ),
                    opposingTeam = Team(
                        id = "team_opp",
                        name = oppSquadInfo.first,
                        colorHex = oppSquadInfo.second,
                        members = oppTeamMembers,
                        totalScore = 1110
                    ),
                    teamMessages = initialChat,
                    feudRoundIndex = 0,
                    currentFeudRound = defaultFeud,
                    currentFeudAnswers = defaultFeud.answers,
                    feudStrikes = 0,
                    feudBank = 450,
                    priceItemIndex = 0,
                    currentPriceItem = defaultPrice,
                    myPriceGuess = "",
                    isPriceSubmitted = false,
                    priceResultRevealed = false,
                    wheelPuzzleIndex = 0,
                    currentWheelPuzzle = defaultWheel,
                    guessedLetters = setOf('E', 'A', 'O', 'T', 'S'),
                    jeopardyClues = PreloadedGameData.jeopardyClues,
                    selectedJeopardyClue = null,
                    jeopardyPlayerScore = 0,
                    mobQuestionIndex = 0,
                    currentMobQuestion = PreloadedGameData.mobQuestions[0],
                    mobCount = 100,
                    selectedMobOption = null,
                    isMobAnswered = false,
                    timerSeconds = 30,
                    isTimerRunning = true
                )
            }
        }
    }

    fun sendTeamMessage(messageText: String) {
        if (messageText.isBlank()) return
        val newMessage = TeamChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = "You",
            senderEmoji = "😎",
            message = messageText.trim(),
            timestamp = "Now",
            isCurrentUser = true
        )
        _uiState.update { it.copy(teamMessages = it.teamMessages + newMessage) }

        viewModelScope.launch {
            delay(1000)
            val teamResponses = listOf(
                "Good call! Locking that in.",
                "I agree with that guess!",
                "Great suggestion, squad!",
                "Let's put that on the main board!"
            )
            val autoReply = TeamChatMessage(
                id = "msg_reply_${System.currentTimeMillis()}",
                senderName = "QuickBuzz_Mike",
                senderEmoji = "⚡",
                message = teamResponses.random(),
                timestamp = "Just now",
                isSuggestion = true
            )
            _uiState.update { it.copy(teamMessages = it.teamMessages + autoReply) }
        }
    }

    fun toggleMic() {
        _uiState.update { it.copy(isMicMuted = !it.isMicMuted) }
    }

    // --- FAMILY FEUD ACTIONS ---
    fun revealFeudAnswer(index: Int) {
        _uiState.update { state ->
            val updated = state.currentFeudAnswers.mapIndexed { i, ans ->
                if (i == index) ans.copy(isRevealed = true) else ans
            }
            val addedPoints = (state.currentFeudAnswers.getOrNull(index)?.points ?: 0) * 10
            state.copy(
                currentFeudAnswers = updated,
                feudBank = state.feudBank + addedPoints
            )
        }
    }

    fun addFeudStrike() {
        _uiState.update { state ->
            val newStrikes = (state.feudStrikes + 1).coerceAtMost(3)
            state.copy(feudStrikes = newStrikes)
        }
    }

    fun nextFeudRound() {
        val nextIdx = (_uiState.value.feudRoundIndex + 1) % PreloadedGameData.feudRounds.size
        val round = PreloadedGameData.feudRounds[nextIdx]
        _uiState.update {
            it.copy(
                feudRoundIndex = nextIdx,
                currentFeudRound = round,
                currentFeudAnswers = round.answers,
                feudStrikes = 0,
                feudBank = it.feudBank + 300
            )
        }
    }

    // --- THE PRICE IS RIGHT ACTIONS ---
    fun submitPriceGuess(guessStr: String) {
        val guessNum = guessStr.toIntOrNull() ?: return
        val variance = (guessNum * 0.05).toInt().coerceAtLeast(100)
        _uiState.update {
            it.copy(
                myPriceGuess = guessStr,
                isPriceSubmitted = true,
                teamMedianGuess = guessNum + (variance / 2),
                oppTeamMedianGuess = (guessNum * 0.92).toInt()
            )
        }
    }

    fun revealPriceResult() {
        _uiState.update { it.copy(priceResultRevealed = true) }
    }

    fun nextPriceItem() {
        val nextIdx = (_uiState.value.priceItemIndex + 1) % PreloadedGameData.priceItems.size
        _uiState.update {
            it.copy(
                priceItemIndex = nextIdx,
                currentPriceItem = PreloadedGameData.priceItems[nextIdx],
                myPriceGuess = "",
                isPriceSubmitted = false,
                priceResultRevealed = false
            )
        }
    }

    // --- WHEEL OF FORTUNE ACTIONS ---
    fun spinWheel() {
        if (_uiState.value.isSpinningWheel) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSpinningWheel = true) }
            delay(1800)
            val multipliers = listOf(250, 500, 750, 1000, 1500, 2500)
            val selectedMultiplier = multipliers.random()
            _uiState.update {
                it.copy(
                    isSpinningWheel = false,
                    wheelMultiplier = selectedMultiplier,
                    toastMessage = "Wheel landed on \$$selectedMultiplier per consonant!"
                )
            }
        }
    }

    fun guessLetter(letter: Char) {
        _uiState.update {
            it.copy(guessedLetters = it.guessedLetters + letter.uppercaseChar())
        }
    }

    // --- JEOPARDY ACTIONS ---
    fun selectJeopardyClue(clue: PreloadedJeopardyClue) {
        _uiState.update {
            it.copy(
                selectedJeopardyClue = clue,
                jeopardyAnswerFeedback = null
            )
        }
    }

    fun answerJeopardyClue(optionIndex: Int) {
        val activeClue = _uiState.value.selectedJeopardyClue ?: return
        val isCorrect = optionIndex == activeClue.correctIndex
        val pts = activeClue.value

        _uiState.update { state ->
            val updatedClues = state.jeopardyClues.map {
                if (it.id == activeClue.id) it.copy(isAnswered = true) else it
            }
            state.copy(
                jeopardyClues = updatedClues,
                jeopardyPlayerScore = if (isCorrect) state.jeopardyPlayerScore + pts else state.jeopardyPlayerScore - pts,
                jeopardyAnswerFeedback = if (isCorrect) "CORRECT! +$${pts}" else "INCORRECT! -$${pts}. Host: ${activeClue.secretAnswer}"
            )
        }
    }

    // --- 1 VS. 100 ACTIONS ---
    fun answerMobQuestion(optionIndex: Int) {
        val currentQ = _uiState.value.currentMobQuestion
        val isCorrect = optionIndex == currentQ.correctIndex

        _uiState.update { state ->
            val eliminatedCount = if (isCorrect) (25..45).random() else 0
            state.copy(
                selectedMobOption = optionIndex,
                isMobAnswered = true,
                mobCount = (state.mobCount - eliminatedCount).coerceAtLeast(0),
                mobPrizePoolCash = if (isCorrect) state.mobPrizePoolCash + (eliminatedCount * 150) else state.mobPrizePoolCash,
                mobPrizePoolPoints = if (isCorrect) state.mobPrizePoolPoints + (eliminatedCount * 3000) else state.mobPrizePoolPoints
            )
        }
    }

    fun nextMobQuestion() {
        val nextIdx = (_uiState.value.mobQuestionIndex + 1) % PreloadedGameData.mobQuestions.size
        _uiState.update {
            it.copy(
                mobQuestionIndex = nextIdx,
                currentMobQuestion = PreloadedGameData.mobQuestions[nextIdx],
                selectedMobOption = null,
                isMobAnswered = false
            )
        }
    }

    fun leaveGame() {
        _uiState.update {
            it.copy(
                activeGameStarted = false,
                selectedGameType = null,
                isMatchmaking = false,
                showHostSecretSheet = false
            )
        }
    }
}
