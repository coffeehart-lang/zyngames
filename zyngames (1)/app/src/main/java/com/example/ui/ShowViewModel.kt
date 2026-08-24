package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiHostService
import com.example.ai.HostVoiceManager
import com.example.data.*
import com.example.util.ConnectivityObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    // Section & Mode toggles
    val currentSection: AppSection = AppSection.MAIN_MENU,
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
    // 1. Family Feud (Authentic TV Stage & Communication)
    val feudRoundIndex: Int = 0,
    val currentFeudRound: PreloadedFeudRound = PreloadedGameData.feudRounds[0],
    val currentFeudAnswers: List<FeudAnswer> = PreloadedGameData.feudRounds[0].answers,
    val feudStrikes: Int = 0,
    val feudBank: Int = 0,
    val feudActiveTeamTurn: String = "MY_TEAM", // "MY_TEAM" or "OPP_TEAM"
    val feudFaceOffActive: Boolean = true,
    val feudFaceOffWinner: String? = null,
    val feudPlayOrPassActive: Boolean = false,
    val feudOpponentSpeech: FeudLiveSpeech? = null,
    val feudMyTeamSpeech: FeudLiveSpeech? = null,
    val feudSuggestions: List<String> = listOf("Toothbrush / Toiletries", "Phone Charger", "Sunscreen", "Extra Socks", "Passport"),
    val feudPopulatedAnswer: String = "",
    val feudIsStealActive: Boolean = false,
    val feudSoundFxBanner: String? = "🔔 WELCOME TO FAMILY FEUD! Face-off captains to the center podium!",

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

    // 6. Debate Arena
    val debateTopics: List<DebateTopic> = PreloadedGameData.debateTopics,
    val selectedDebateTopic: DebateTopic = PreloadedGameData.debateTopics[0],
    val debateRoundStage: DebateRoundStage = DebateRoundStage.OPENING_PRO,
    val userDebateSide: Boolean = true, // true = PRO, false = CON
    val debateTimerSeconds: Int = 45,
    val showCustomTopicDialog: Boolean = false,

    // Daily Spin Wheel (Zero Cooldown, Zero Ads, Huge Lucrative Rewards)
    val isSpinningDailyWheel: Boolean = false,
    val latestDailySpinReward: SpinReward? = null,
    val dailySpinHistoryCount: Int = 0,

    // Leaderboards & Rewards Subsystem
    val purchasedShopItemIds: Set<String> = emptySet(),
    val completedChallengeIds: Set<String> = setOf("dc_1"),

    // Host Persona & Real-time AI Audio Broadcast
    val hostName: String = "Monte Carlo Smith",
    val hostBanter: String = "Welcome in, beautiful folks! Look at these good-looking squads today! Y'all are looking sharp, let's get some big points on the board!",
    val hostBanterType: String = "GREETING", // GREETING, QUESTION, COMPLIMENT, JOKE, LAUGH, STRIKE_JOKE
    val hostReactionEmoji: String = "😄",
    val isHostSpeaking: Boolean = false,
    val isHostVoiceEnabled: Boolean = true,
    val isHostLoadingAi: Boolean = false,
    val showTalkToHostDialog: Boolean = false,
    val showAiCustomQuestionDialog: Boolean = false,

    // Family Feud Funny Last Names
    val myFamilyLastName: String = "Wigglebottom",
    val oppFamilyLastName: String = "Snickerdoodle",

    // Play With Friends (Nearby & Online Rooms)
    val showPlayWithFriendsDialog: Boolean = false,
    val playWithFriendsTab: String = "NEARBY", // "NEARBY", "ONLINE", "PARTY_ROOM", "JOIN_CODE"
    val nearbyFriends: List<FriendPlayer> = PreloadedGameData.nearbyFriendsList,
    val onlineFriends: List<FriendPlayer> = PreloadedGameData.onlineFriendsList,
    val partyMembers: List<FriendPlayer> = emptyList(),
    val generatedPartyCode: String = "ZYNA-8842",
    val enteredPartyCode: String = "",
    val isScanningNearbyRadar: Boolean = false,
    val playWithFriendsSelectedGame: GameShowType = GameShowType.FAMILY_FEUD,

    val toastMessage: String? = null,
    val isInternetConnected: Boolean = true
)

class ShowViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val geminiHostService = GeminiHostService()
    private var hostVoiceManager: HostVoiceManager? = null

    private val squadNames = listOf(
        Pair("Team Volt Blue", 0xFF00B4D8),
        Pair("Team Crimson Titans", 0xFFE63946),
        Pair("Team Gold Pulse", 0xFFFFB703),
        Pair("Team Emerald Synths", 0xFF2EC4B6)
    )

    fun initVoiceManager(voiceManager: HostVoiceManager) {
        this.hostVoiceManager = voiceManager
        viewModelScope.launch {
            voiceManager.isSpeaking.collect { speaking ->
                _uiState.update { it.copy(isHostSpeaking = speaking) }
            }
        }
        viewModelScope.launch {
            voiceManager.isVoiceEnabled.collect { enabled ->
                _uiState.update { it.copy(isHostVoiceEnabled = enabled) }
            }
        }
    }

    fun initConnectivityObserver(observer: ConnectivityObserver) {
        viewModelScope.launch {
            observer.observe().collect { connected ->
                _uiState.update { it.copy(isInternetConnected = connected) }
            }
        }
    }

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

            // Check if Family Feud - pick hilarious funny last names!
            val isFeud = gameType == GameShowType.FAMILY_FEUD
            val myLastName = if (isFeud) {
                PreloadedGameData.funnyFamilyLastNames.random()
            } else {
                _uiState.value.myFamilyLastName
            }
            val oppLastName = if (isFeud) {
                PreloadedGameData.funnyFamilyLastNames.filter { it != myLastName }.random()
            } else {
                _uiState.value.oppFamilyLastName
            }

            val mySquadInfo = squadNames.random()
            val oppSquadInfo = squadNames.filter { it.first != mySquadInfo.first }.random()

            val myTeamName = if (isFeud) "The $myLastName Family" else mySquadInfo.first
            val oppTeamName = if (isFeud) "The $oppLastName Family" else oppSquadInfo.first

            // Build squad members, incorporating invited party members if any
            val partyFriends = _uiState.value.partyMembers
            val myTeamMembers = if (isFeud) {
                val base = mutableListOf<Player>()
                base.add(Player("1", "You ($myLastName)", "😎", isCurrentUser = true, isCaptain = false, score = 150))
                if (partyFriends.isNotEmpty()) {
                    partyFriends.take(4).forEachIndexed { idx, friend ->
                        base.add(Player("friend_${friend.id}", "${friend.name} ($myLastName)", friend.avatarEmoji, score = (200..350).random()))
                    }
                }
                val relatives = listOf(
                    Pair("Papa Bob", "👴"),
                    Pair("Mama Clara", "👩"),
                    Pair("Cousin Timmy", "🧢"),
                    Pair("Uncle Greg", "🎩"),
                    Pair("Aunt Sally", "👒")
                )
                while (base.size < 5) {
                    val rel = relatives[base.size - 1]
                    base.add(Player("rel_${base.size}", "${rel.first} $myLastName", rel.second, score = (180..320).random()))
                }
                base
            } else {
                if (partyFriends.isNotEmpty()) {
                    val base = mutableListOf(Player("1", "You (Player)", "😎", isCurrentUser = true, isCaptain = false, score = 150))
                    partyFriends.take(4).forEachIndexed { idx, friend ->
                        base.add(Player("friend_${friend.id}", friend.name, friend.avatarEmoji, isCaptain = idx == 0, score = (200..380).random()))
                    }
                    val defaults = listOf(
                        Player("4", "LuckySpinner", "🎡", score = 210),
                        Player("5", "PriceMaster_Tom", "🏷️", score = 310)
                    )
                    while (base.size < 5) {
                        base.add(defaults[base.size - 1])
                    }
                    base
                } else {
                    listOf(
                        Player("1", "You (Player)", "😎", isCurrentUser = true, isCaptain = false, score = 150),
                        Player("2", "QuickBuzz_Mike", "⚡", isCaptain = true, score = 380),
                        Player("3", "TriviaQueen_Jen", "👑", score = 290),
                        Player("4", "LuckySpinner", "🎡", score = 210),
                        Player("5", "PriceMaster_Tom", "🏷️", score = 310)
                    )
                }
            }

            val oppTeamMembers = if (isFeud) {
                listOf(
                    Player("6", "Captain Dave $oppLastName", "🔥", isCaptain = true, score = 340),
                    Player("7", "Aunt Gertrude $oppLastName", "👵", score = 260),
                    Player("8", "Nephew Ricky $oppLastName", "🎒", score = 280),
                    Player("9", "Grandma Ethel $oppLastName", "👓", score = 230)
                )
            } else {
                listOf(
                    Player("6", "RivalCaptain_Dan", "🔥", isCaptain = true, score = 340),
                    Player("7", "SonicWave", "🔊", score = 260),
                    Player("8", "BrainySam", "🧠", score = 280),
                    Player("9", "StarGazer", "✨", score = 230)
                )
            }

            val initialChat = if (isFeud) {
                listOf(
                    TeamChatMessage("c1", "team_my", "Papa Bob $myLastName", "👴", "Alright $myLastName family! Let's get these survey answers!", "Just now"),
                    TeamChatMessage("c2", "team_my", "Mama Clara $myLastName", "👩", "Remember what happened last Thanksgiving—no fighting on TV! 😂", "Just now")
                )
            } else {
                listOf(
                    TeamChatMessage("c1", "team_my", "QuickBuzz_Mike", "⚡", "Welcome squad! Host is live on stage. Keep chat active for suggestions!", "Just now"),
                    TeamChatMessage("c2", "team_my", "TriviaQueen_Jen", "👑", "Ready! Let's take the win today! 🔥", "Just now")
                )
            }

            val defaultFeud = PreloadedGameData.feudRounds[0]
            val defaultPrice = PreloadedGameData.priceItems[0]
            val defaultWheel = PreloadedGameData.wheelPuzzles[0]

            val welcomeBanter = if (isFeud) {
                "Welcome to prime-time Family Feud! Today we've got the $myLastName Family taking on the $oppLastName Family! 100 folks surveyed, top answers on the board!"
            } else {
                "Welcome in, players! We are live for ${gameType.title}! Give it up for both squads!"
            }

            _uiState.update {
                it.copy(
                    isMatchmaking = false,
                    activeGameStarted = true,
                    myFamilyLastName = myLastName,
                    oppFamilyLastName = oppLastName,
                    currentTeam = Team(
                        id = "team_my",
                        name = myTeamName,
                        colorHex = mySquadInfo.second,
                        members = myTeamMembers,
                        totalScore = 1340
                    ),
                    opposingTeam = Team(
                        id = "team_opp",
                        name = oppTeamName,
                        colorHex = oppSquadInfo.second,
                        members = oppTeamMembers,
                        totalScore = 1110
                    ),
                    teamMessages = initialChat,
                    hostBanter = welcomeBanter,
                    hostReactionEmoji = "🎙️",
                    feudRoundIndex = 0,
                    currentFeudRound = defaultFeud,
                    currentFeudAnswers = defaultFeud.answers,
                    feudStrikes = 0,
                    feudBank = 450,
                    feudFaceOffWinner = null,
                    feudFaceOffActive = true,
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

            if (isFeud) {
                hostVoiceManager?.speak(welcomeBanter)
            }
        }
    }

    fun sendTeamMessage(messageText: String) {
        if (messageText.isBlank()) return
        val currentTeamId = _uiState.value.currentTeam?.id ?: "team_my"
        val newMessage = TeamChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            teamId = currentTeamId,
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
                teamId = currentTeamId,
                senderName = "QuickBuzz_Mike",
                senderEmoji = "⚡",
                message = teamResponses.random(),
                timestamp = "Just now",
                isSuggestion = true
            )
            _uiState.update { it.copy(teamMessages = it.teamMessages + autoReply) }
        }
    }

    private val familyJokes = listOf(
        "Why did the scarecrow win an award? Because he was outstanding in his field—just like you folks today! 😂",
        "My dad always said: 'Son, if you ever freeze up on stage, just smile and tell 'em you love your mother!' So here I am! ❤️",
        "What do you call a fake noodle? An impasta! But believe me, these prize pools are 100% genuine! 🍝",
        "Why don't eggs tell each other jokes? Because they'd crack each other up! Just like this hilarious squad chat! 🥚",
        "My wife told me to stop impersonating a flamingo... so I had to put my foot down! 🦩",
        "I told my doctor I broke my arm in two places. He told me to stop going to those places! Keep those guesses safe, team! 🏥"
    )

    private val warmCompliments = listOf(
        "Look at that sharp squad energy! You folks make championship gameplay look effortless! ✨",
        "I've hosted hundreds of arenas, but your team has the best synergy I've seen all week! 🏆",
        "Pure superstar intuition right there! The crowd is absolutely loving this! 👏",
        "You're not just playing the game, you're commanding the whole stage! Keep it up! 👑",
        "That's high-IQ play right there. Remind me to invite you to my family trivia night! 🧠"
    )

    fun tellHostJoke() {
        val gameTitle = _uiState.value.selectedGameType?.title ?: "Prime Time Arena"
        _uiState.update { it.copy(isHostLoadingAi = true) }
        viewModelScope.launch {
            val joke = geminiHostService.generateHostJoke(gameTitle)
            _uiState.update {
                it.copy(
                    hostBanter = joke,
                    hostBanterType = "JOKE",
                    hostReactionEmoji = "😂",
                    isHostLoadingAi = false
                )
            }
            hostVoiceManager?.speak(joke)
        }
    }

    fun giveHostCompliment() {
        val comp = warmCompliments.random()
        _uiState.update {
            it.copy(
                hostBanter = comp,
                hostBanterType = "COMPLIMENT",
                hostReactionEmoji = "🌟"
            )
        }
        hostVoiceManager?.speak(comp)
    }

    fun toggleHostVoice() {
        val enabled = hostVoiceManager?.toggleVoiceEnabled() ?: true
        _uiState.update { it.copy(isHostVoiceEnabled = enabled) }
    }

    fun toggleTalkToHostDialog(show: Boolean? = null) {
        _uiState.update {
            it.copy(showTalkToHostDialog = show ?: !it.showTalkToHostDialog)
        }
    }

    fun toggleAiCustomQuestionDialog(show: Boolean? = null) {
        _uiState.update {
            it.copy(showAiCustomQuestionDialog = show ?: !it.showAiCustomQuestionDialog)
        }
    }

    fun askCurrentGameQuestion() {
        val state = _uiState.value
        val game = state.selectedGameType ?: return
        val contextPrompt = when (game) {
            GameShowType.FAMILY_FEUD ->
                "Round ${state.feudRoundIndex + 1}: ${state.currentFeudRound.question}"
            GameShowType.THE_PRICE_IS_RIGHT ->
                "Showcase item #${state.priceItemIndex + 1}: ${state.currentPriceItem.name} - ${state.currentPriceItem.description}"
            GameShowType.WHEEL_OF_FORTUNE ->
                "Category: ${state.currentWheelPuzzle.category}. Secret phrase length: ${state.currentWheelPuzzle.secretPhrase.length} letters."
            GameShowType.JEOPARDY ->
                if (state.selectedJeopardyClue != null) "${state.selectedJeopardyClue.category} for $${state.selectedJeopardyClue.value}: ${state.selectedJeopardyClue.clue}"
                else "Select any category clue from the board to play!"
            GameShowType.ONE_VS_100 ->
                "Tier ${state.currentMobQuestion.tier} Question: ${state.currentMobQuestion.question}. Options: ${state.currentMobQuestion.options.joinToString(", ")}"
            GameShowType.DEBATE_SHOWDOWN ->
                "Current stage: ${state.debateRoundStage.title}. Motion: ${state.selectedDebateTopic.title} (${state.selectedDebateTopic.description})"
        }

        _uiState.update { it.copy(isHostLoadingAi = true) }
        viewModelScope.launch {
            val speech = geminiHostService.askQuestion(game, contextPrompt)
            _uiState.update {
                it.copy(
                    hostBanter = speech,
                    hostBanterType = "QUESTION",
                    hostReactionEmoji = "🎙️",
                    isHostLoadingAi = false
                )
            }
            hostVoiceManager?.speak(speech)
        }
    }

    fun interactWithAiHost(contestantMessage: String) {
        if (contestantMessage.isBlank()) return
        val gameTitle = _uiState.value.selectedGameType?.title ?: "The Arena"
        _uiState.update {
            it.copy(
                isHostLoadingAi = true,
                showTalkToHostDialog = false,
                toastMessage = "AI Host Monte Carlo is thinking..."
            )
        }
        viewModelScope.launch {
            val response = geminiHostService.banterWithPlayer(contestantMessage, gameTitle)
            _uiState.update {
                it.copy(
                    hostBanter = response,
                    hostBanterType = "BANTER",
                    hostReactionEmoji = "🤩",
                    isHostLoadingAi = false
                )
            }
            hostVoiceManager?.speak(response)
        }
    }

    fun requestAiCustomQuestion(topic: String) {
        if (topic.isBlank()) return
        val game = _uiState.value.selectedGameType ?: GameShowType.FAMILY_FEUD
        _uiState.update {
            it.copy(
                isHostLoadingAi = true,
                showAiCustomQuestionDialog = false,
                toastMessage = "AI Host is writing a brand-new custom question..."
            )
        }
        viewModelScope.launch {
            val customQuestion = geminiHostService.generateCustomAiQuestion(game, topic)
            _uiState.update {
                it.copy(
                    hostBanter = customQuestion,
                    hostBanterType = "QUESTION",
                    hostReactionEmoji = "🎲",
                    isHostLoadingAi = false
                )
            }
            hostVoiceManager?.speak(customQuestion)
        }
    }

    fun toggleMic() {
        _uiState.update { it.copy(isMicMuted = !it.isMicMuted) }
    }

    // --- FAMILY FEUD ACTIONS ---
    fun feudBuzzFaceOff() {
        val myName = "The ${_uiState.value.myFamilyLastName} Family (You)"
        _uiState.update {
            it.copy(
                feudFaceOffActive = false,
                feudFaceOffWinner = myName,
                feudPlayOrPassActive = true,
                feudActiveTeamTurn = "MY_TEAM",
                feudSoundFxBanner = "🚨 BUZZ! The ${it.myFamilyLastName} Family buzzed in first! Do you want to PLAY or PASS?",
                hostBanter = "Quick hands on the buzzer for the ${it.myFamilyLastName}s! Do you want to PLAY or PASS the board?",
                hostBanterType = "QUESTION",
                hostReactionEmoji = "⚡"
            )
        }
        hostVoiceManager?.speak("The ${uiState.value.myFamilyLastName}s win the face-off! Do you choose to PLAY or PASS?")
    }

    fun feudChoosePlayOrPass(play: Boolean) {
        val myLastName = _uiState.value.myFamilyLastName
        val oppLastName = _uiState.value.oppFamilyLastName
        _uiState.update {
            it.copy(
                feudPlayOrPassActive = false,
                feudActiveTeamTurn = if (play) "MY_TEAM" else "OPP_TEAM",
                feudSoundFxBanner = if (play) "🎯 The $myLastName Family chose to PLAY! Time to clear the board!" else "🛡️ The $myLastName Family chose to PASS! The $oppLastName Family is on the hot seat!",
                hostBanter = if (play) "They're gonna PLAY! Give me the next top survey answer!" else "They PASS! Let's see if the $oppLastName family can hold the board!",
                hostReactionEmoji = if (play) "🔥" else "👀"
            )
        }
        if (!play) {
            feudOpponentTurn()
        }
    }

    fun feudPopulateAnswer(text: String) {
        _uiState.update { it.copy(feudPopulatedAnswer = text) }
    }

    fun rerollFamilyNames() {
        val myLastName = PreloadedGameData.funnyFamilyLastNames.random()
        val oppLastName = PreloadedGameData.funnyFamilyLastNames.filter { it != myLastName }.random()

        val partyFriends = _uiState.value.partyMembers
        val base = mutableListOf<Player>()
        base.add(Player("1", "You ($myLastName)", "😎", isCurrentUser = true, isCaptain = false, score = 150))
        if (partyFriends.isNotEmpty()) {
            partyFriends.take(4).forEachIndexed { idx, friend ->
                base.add(Player("friend_${friend.id}", "${friend.name} ($myLastName)", friend.avatarEmoji, score = (200..350).random()))
            }
        }
        val relatives = listOf(
            Pair("Papa Bob", "👴"),
            Pair("Mama Clara", "👩"),
            Pair("Cousin Timmy", "🧢"),
            Pair("Uncle Greg", "🎩"),
            Pair("Aunt Sally", "👒")
        )
        while (base.size < 5) {
            val rel = relatives[base.size - 1]
            base.add(Player("rel_${base.size}", "${rel.first} $myLastName", rel.second, score = (180..320).random()))
        }

        val oppTeamMembers = listOf(
            Player("6", "Captain Dave $oppLastName", "🔥", isCaptain = true, score = 340),
            Player("7", "Aunt Gertrude $oppLastName", "👵", score = 260),
            Player("8", "Nephew Ricky $oppLastName", "🎒", score = 280),
            Player("9", "Grandma Ethel $oppLastName", "👓", score = 230)
        )

        val announcement = "Introducing our brand-new hilarious feud: The $myLastName Family taking on The $oppLastName Family! Let's get it on!"

        _uiState.update {
            it.copy(
                myFamilyLastName = myLastName,
                oppFamilyLastName = oppLastName,
                currentTeam = it.currentTeam?.copy(
                    name = "The $myLastName Family",
                    members = base
                ),
                opposingTeam = it.opposingTeam?.copy(
                    name = "The $oppLastName Family",
                    members = oppTeamMembers
                ),
                hostBanter = announcement,
                hostReactionEmoji = "😂",
                toastMessage = "🎲 Funny names rerolled: The $myLastName's vs. The $oppLastName's!"
            )
        }
        hostVoiceManager?.speak(announcement)
    }

    fun feudSuggestHuddleAnswer(answerText: String) {
        if (answerText.isBlank()) return
        val mySpeech = FeudLiveSpeech(
            speakerName = "You",
            speakerEmoji = "😎",
            isOpponent = false,
            speechText = "How about '$answerText'?!",
            isOfficialGuess = false
        )
        _uiState.update {
            it.copy(
                feudMyTeamSpeech = mySpeech,
                feudSuggestions = (it.feudSuggestions + answerText).distinct(),
                feudSoundFxBanner = "💬 Squad Huddle: You suggested '$answerText'"
            )
        }

        viewModelScope.launch {
            delay(1200)
            val teammateCheer = listOf(
                "Good answer! That's definitely up there!",
                "Love that guess, captain! Lock it in!",
                "Yes! My grandma always says that!",
                "Great call! Tell Steve!"
            ).random()
            _uiState.update {
                it.copy(
                    feudMyTeamSpeech = FeudLiveSpeech(
                        speakerName = "QuickBuzz_Mike",
                        speakerEmoji = "⚡",
                        isOpponent = false,
                        speechText = teammateCheer
                    )
                )
            }
        }
    }

    fun feudCheerTeammates() {
        _uiState.update {
            it.copy(
                feudMyTeamSpeech = FeudLiveSpeech(
                    speakerName = "The Squad",
                    speakerEmoji = "👏",
                    isOpponent = false,
                    speechText = "GOOD ANSWER! GOOD ANSWER! 👏🎉"
                ),
                feudSoundFxBanner = "👏 AUDIENCE & SQUAD APPLAUSE: 'Good answer, good answer!'",
                hostBanter = "I love the family spirit! That's how champions play together!",
                hostReactionEmoji = "❤️"
            )
        }
    }

    fun submitFeudOfficialAnswer(answerText: String) {
        val state = _uiState.value
        val matchIndex = state.currentFeudAnswers.indexOfFirst {
            it.text.contains(answerText, ignoreCase = true) || answerText.contains(it.text.split("/")[0].trim(), ignoreCase = true)
        }

        if (matchIndex >= 0 && !state.currentFeudAnswers[matchIndex].isRevealed) {
            revealFeudAnswer(matchIndex)
        } else {
            addFeudStrike()
        }
    }

    fun feudOpponentTurn() {
        viewModelScope.launch {
            val oppLastName = _uiState.value.oppFamilyLastName
            val opponentNames = listOf("Captain Dave $oppLastName (🔥)", "Aunt Gertrude $oppLastName (👵)", "Nephew Ricky $oppLastName (🎒)", "Grandma Ethel $oppLastName (👓)")
            val speaker = opponentNames.random()

            // Find unrevealed answers for realistic opponent simulation
            val unrevealed = _uiState.value.currentFeudAnswers.filter { !it.isRevealed }
            val willHit = (0..10).random() > 4 && unrevealed.isNotEmpty()

            val opponentGuess = if (willHit) {
                unrevealed.random().text.split("/")[0].trim()
            } else {
                listOf("Swimsuit", "Snacks", "Water Bottle", "Camera", "Pillow").random()
            }

            _uiState.update {
                it.copy(
                    feudActiveTeamTurn = "OPP_TEAM",
                    feudOpponentSpeech = FeudLiveSpeech(
                        speakerName = speaker,
                        speakerEmoji = "🗣️",
                        isOpponent = true,
                        speechText = "Monte Carlo, the $oppLastName family is going with '$opponentGuess'!",
                        isOfficialGuess = true
                    ),
                    feudSoundFxBanner = "🔊 Opponent Guess: '$opponentGuess'..."
                )
            }

            delay(2000)

            val matchIndex = _uiState.value.currentFeudAnswers.indexOfFirst {
                it.text.contains(opponentGuess, ignoreCase = true) || opponentGuess.contains(it.text.split("/")[0].trim(), ignoreCase = true)
            }

            if (matchIndex >= 0 && !_uiState.value.currentFeudAnswers[matchIndex].isRevealed) {
                val addedPoints = (_uiState.value.currentFeudAnswers[matchIndex].points) * 10
                val updatedAnswers = _uiState.value.currentFeudAnswers.mapIndexed { i, ans ->
                    if (i == matchIndex) ans.copy(isRevealed = true) else ans
                }
                _uiState.update {
                    it.copy(
                        currentFeudAnswers = updatedAnswers,
                        feudBank = it.feudBank + addedPoints,
                        feudSoundFxBanner = "🔔 DING! Opponent found #${matchIndex + 1}: '$opponentGuess' (+$addedPoints PTS)!",
                        feudOpponentSpeech = FeudLiveSpeech(
                            speakerName = speaker,
                            speakerEmoji = "🎉",
                            isOpponent = true,
                            speechText = "YES! It's on the board! Let's go!",
                            resultBanner = "CORRECT"
                        ),
                        hostBanter = "The survey says YES! The rival family gets the points on the board!",
                        hostReactionEmoji = "🔥"
                    )
                }
            } else {
                val newStrikes = (_uiState.value.feudStrikes + 1).coerceAtMost(3)
                val isStealNow = newStrikes >= 3
                _uiState.update {
                    it.copy(
                        feudStrikes = newStrikes,
                        feudIsStealActive = isStealNow,
                        feudActiveTeamTurn = if (isStealNow) "MY_TEAM" else "OPP_TEAM",
                        feudSoundFxBanner = if (isStealNow) "🚨 3 STRIKES! The Hart Family can now STEAL the entire bank!" else "❌ BUZZ! Strike $newStrikes for the opposing family!",
                        feudOpponentSpeech = FeudLiveSpeech(
                            speakerName = speaker,
                            speakerEmoji = "😫",
                            isOpponent = true,
                            speechText = "Aw man, not on the board!",
                            resultBanner = "STRIKE"
                        ),
                        hostBanter = if (isStealNow) "THREE STRIKES! The door is wide open for a dramatic steal! Squad, what's your answer?!" else "That's a strike! Two more and the other side is stealing!",
                        hostReactionEmoji = if (isStealNow) "🚨" else "😅"
                    )
                }
            }
        }
    }

    fun revealFeudAnswer(index: Int) {
        _uiState.update { state ->
            val updated = state.currentFeudAnswers.mapIndexed { i, ans ->
                if (i == index) ans.copy(isRevealed = true) else ans
            }
            val addedPoints = (state.currentFeudAnswers.getOrNull(index)?.points ?: 0) * 10
            state.copy(
                currentFeudAnswers = updated,
                feudBank = state.feudBank + addedPoints,
                feudSoundFxBanner = "🔔 DING! #${index + 1} ${updated[index].text} revealed! +$addedPoints PTS to Bank!",
                hostBanter = "BOOM! Look at that board light up! The survey says YES! Big points added to the bank! 🎉",
                hostBanterType = "LAUGH",
                hostReactionEmoji = "🔥"
            )
        }
    }

    fun addFeudStrike() {
        _uiState.update { state ->
            val newStrikes = (state.feudStrikes + 1).coerceAtMost(3)
            val isSteal = newStrikes >= 3
            val strikeLines = listOf(
                "Ooh, strike! Don't sweat it, my uncle takes three strikes just to find his car keys! Shake it off! ❌",
                "Not on the board! But good answer, good answer! That's what we say at the family dinner table! 🤝",
                "Three strikes means the other family is smelling a steal! Hold onto your seats, folks! ⚡"
            )
            state.copy(
                feudStrikes = newStrikes,
                feudIsStealActive = isSteal,
                feudSoundFxBanner = if (isSteal) "🚨 3 STRIKES! STEAL OPPORTUNITY ACTIVATED!" else "❌ BUZZ! Strike $newStrikes!",
                hostBanter = strikeLines.getOrElse(newStrikes - 1) { strikeLines[0] },
                hostBanterType = "STRIKE_JOKE",
                hostReactionEmoji = "😅"
            )
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
                feudBank = it.feudBank + 300,
                feudFaceOffActive = true,
                feudFaceOffWinner = null,
                feudIsStealActive = false,
                feudOpponentSpeech = null,
                feudMyTeamSpeech = null,
                feudSoundFxBanner = "🔔 NEW ROUND! Face-off captains to the podium!",
                hostBanter = "Fresh round, fresh energy! Let's see which family takes the lead! You're all looking gorgeous! 🌟",
                hostBanterType = "GREETING",
                hostReactionEmoji = "🎙️"
            )
        }
    }

    // --- DEBATE ARENA ACTIONS ---
    fun selectDebateTopic(topic: DebateTopic) {
        _uiState.update {
            it.copy(
                selectedDebateTopic = topic,
                debateRoundStage = DebateRoundStage.OPENING_PRO,
                debateTimerSeconds = 45,
                hostBanter = "New debate topic selected: '${topic.title}'. Host is ready to moderate! Let's hear opening arguments!",
                hostBanterType = "GREETING",
                hostReactionEmoji = "🎙️"
            )
        }
    }

    fun setDebateSide(isPro: Boolean) {
        _uiState.update {
            it.copy(
                userDebateSide = isPro,
                hostBanter = if (isPro) "You joined the PRO side! Bring your sharpest logic!" else "You joined the CON side! Challenge the motion with passion!",
                hostReactionEmoji = "⚡"
            )
        }
    }

    fun submitDebateArgument(text: String) {
        if (text.isBlank()) return
        val currentTopic = _uiState.value.selectedDebateTopic
        val isPro = _uiState.value.userDebateSide
        val newArg = DebateArgument(
            id = "arg_${System.currentTimeMillis()}",
            authorName = if (isPro) "You (PRO)" else "You (CON)",
            authorEmoji = if (isPro) "⚡" else "🛡️",
            isPro = isPro,
            text = text.trim(),
            likes = 1,
            timestamp = "Just now"
        )

        val updatedTopic = currentTopic.copy(
            arguments = currentTopic.arguments + newArg,
            votesPro = if (isPro) (currentTopic.votesPro + 3).coerceAtMost(100) else (currentTopic.votesPro - 3).coerceAtLeast(0),
            votesCon = if (!isPro) (currentTopic.votesCon + 3).coerceAtMost(100) else (currentTopic.votesCon - 3).coerceAtLeast(0)
        )

        _uiState.update {
            it.copy(
                selectedDebateTopic = updatedTopic,
                debateTopics = it.debateTopics.map { t -> if (t.id == updatedTopic.id) updatedTopic else t },
                hostBanter = "Compelling point raised! The crowd applause meter is shifting! Let's hear the counter-point!",
                hostBanterType = "COMPLIMENT",
                hostReactionEmoji = "🎯"
            )
        }
    }

    fun voteDebate(isPro: Boolean) {
        val currentTopic = _uiState.value.selectedDebateTopic
        val updatedTopic = currentTopic.copy(
            votesPro = if (isPro) (currentTopic.votesPro + 1).coerceAtMost(100) else (currentTopic.votesPro - 1).coerceAtLeast(0),
            votesCon = if (!isPro) (currentTopic.votesCon + 1).coerceAtMost(100) else (currentTopic.votesCon - 1).coerceAtLeast(0)
        )
        _uiState.update {
            it.copy(
                selectedDebateTopic = updatedTopic,
                debateTopics = it.debateTopics.map { t -> if (t.id == updatedTopic.id) updatedTopic else t },
                toastMessage = "Voted for ${if (isPro) "PRO (For)" else "CON (Against)"}!"
            )
        }
    }

    fun nextDebateStage() {
        val stages = DebateRoundStage.values()
        val nextStageIdx = (_uiState.value.debateRoundStage.ordinal + 1) % stages.size
        val nextStage = stages[nextStageIdx]
        _uiState.update {
            it.copy(
                debateRoundStage = nextStage,
                debateTimerSeconds = nextStage.seconds,
                hostBanter = "Moving to ${nextStage.title}! Keep it respectful, sharp, and fiery!",
                hostReactionEmoji = "🔔"
            )
        }
    }

    fun toggleCustomTopicDialog() {
        _uiState.update { it.copy(showCustomTopicDialog = !it.showCustomTopicDialog) }
    }

    fun createCustomDebateTopic(title: String, category: String, pro: String, con: String) {
        if (title.isBlank()) return
        val newTopic = DebateTopic(
            id = "custom_${System.currentTimeMillis()}",
            title = title.trim(),
            category = "$category 💡",
            description = "Custom community debate topic submitted by users.",
            proStance = if (pro.isNotBlank()) "PRO: $pro" else "PRO: For the motion.",
            conStance = if (con.isNotBlank()) "CON: $con" else "CON: Against the motion.",
            isCustom = true,
            votesPro = 50,
            votesCon = 50,
            moderatorNotes = "HOST CUE: Custom topic! Keep discussions engaging and moderated!",
            arguments = listOf(
                DebateArgument("arg_init", "Topic Creator", "🎙️", true, "Let's kick off the debate! Share your best evidence and reasoning below.")
            )
        )

        _uiState.update {
            it.copy(
                debateTopics = listOf(newTopic) + it.debateTopics,
                selectedDebateTopic = newTopic,
                showCustomTopicDialog = false,
                toastMessage = "Custom debate topic created and launched!"
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
                oppTeamMedianGuess = (guessNum * 0.92).toInt(),
                hostBanter = "Bids are locked in! I love that confidence—reminds me of my cousin bargaining at the flea market! Let's see the retail price! 🏷️",
                hostBanterType = "JOKE",
                hostReactionEmoji = "😄"
            )
        }
    }

    fun revealPriceResult() {
        _uiState.update {
            it.copy(
                priceResultRevealed = true,
                hostBanter = "Look at that price tag! Closest without going over takes the glory! What a showcase! 👏",
                hostBanterType = "COMPLIMENT",
                hostReactionEmoji = "🎉"
            )
        }
    }

    fun nextPriceItem() {
        val nextIdx = (_uiState.value.priceItemIndex + 1) % PreloadedGameData.priceItems.size
        _uiState.update {
            it.copy(
                priceItemIndex = nextIdx,
                currentPriceItem = PreloadedGameData.priceItems[nextIdx],
                myPriceGuess = "",
                isPriceSubmitted = false,
                priceResultRevealed = false,
                hostBanter = "Next luxury showcase is on stage! Trust your squad's instincts! 🚗",
                hostBanterType = "GREETING",
                hostReactionEmoji = "✨"
            )
        }
    }

    // --- WHEEL OF FORTUNE ACTIONS ---
    fun spinWheel() {
        if (_uiState.value.isSpinningWheel) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSpinningWheel = true,
                    hostBanter = "Big money, big money! Round and round she goes, where she stops, everybody wins! 🎡",
                    hostBanterType = "LAUGH",
                    hostReactionEmoji = "🌀"
                )
            }
            delay(1800)
            val multipliers = listOf(250, 500, 750, 1000, 1500, 2500)
            val selectedMultiplier = multipliers.random()
            _uiState.update {
                it.copy(
                    isSpinningWheel = false,
                    wheelMultiplier = selectedMultiplier,
                    toastMessage = "Wheel landed on \$$selectedMultiplier per consonant!",
                    hostBanter = "Landed on \$$selectedMultiplier per letter! Call out a winner for the family! 🎯",
                    hostBanterType = "COMPLIMENT",
                    hostReactionEmoji = "🤩"
                )
            }
        }
    }

    fun guessLetter(letter: Char) {
        _uiState.update {
            it.copy(
                guessedLetters = it.guessedLetters + letter.uppercaseChar(),
                hostBanter = "You called '$letter'! Let's see if our puzzle board has got some love for ya! 🔤",
                hostBanterType = "GREETING",
                hostReactionEmoji = "👌"
            )
        }
    }

    // --- JEOPARDY ACTIONS ---
    fun selectJeopardyClue(clue: PreloadedJeopardyClue) {
        _uiState.update {
            it.copy(
                selectedJeopardyClue = clue,
                jeopardyAnswerFeedback = null,
                hostBanter = "${clue.category} for \$${clue.value}! You've got the smarts for this, take your time! 🧠",
                hostBanterType = "COMPLIMENT",
                hostReactionEmoji = "⚡"
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
                jeopardyAnswerFeedback = if (isCorrect) "CORRECT! +$${pts}" else "INCORRECT! -$${pts}. Host: ${activeClue.secretAnswer}",
                hostBanter = if (isCorrect) "BINGO! You're lighting up the leaderboard! Brilliant mind on stage! 🌟" else "Ah so close! Keep swinging, champions bounce right back! 💪",
                hostBanterType = if (isCorrect) "COMPLIMENT" else "JOKE",
                hostReactionEmoji = if (isCorrect) "🥳" else "👍"
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
                mobPrizePoolPoints = if (isCorrect) state.mobPrizePoolPoints + (eliminatedCount * 3000) else state.mobPrizePoolPoints,
                hostBanter = if (isCorrect) "YES! You just knocked out $eliminatedCount mob members! You're an absolute legend on the hot seat! 💥" else "Ooh, the mob got the upper hand that round! Shake it off! 🛡️",
                hostBanterType = if (isCorrect) "COMPLIMENT" else "JOKE",
                hostReactionEmoji = if (isCorrect) "😎" else "🧐"
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

    // --- PLAY WITH FRIENDS (NEARBY & ONLINE) ---
    fun togglePlayWithFriendsDialog(show: Boolean? = null) {
        _uiState.update { it.copy(showPlayWithFriendsDialog = show ?: !it.showPlayWithFriendsDialog) }
    }

    fun setPlayWithFriendsTab(tab: String) {
        _uiState.update { it.copy(playWithFriendsTab = tab) }
    }

    fun selectPlayWithFriendsGame(gameType: GameShowType) {
        _uiState.update { it.copy(playWithFriendsSelectedGame = gameType) }
    }

    fun scanNearbyFriends() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningNearbyRadar = true, toastMessage = "📡 Scanning for nearby friends via Bluetooth & Wi-Fi...") }
            delay(1400)
            _uiState.update {
                it.copy(
                    isScanningNearbyRadar = false,
                    nearbyFriends = PreloadedGameData.nearbyFriendsList,
                    toastMessage = "✨ Radar scan complete! 4 nearby friends found ready to play!"
                )
            }
        }
    }

    fun toggleInviteFriend(friend: FriendPlayer) {
        _uiState.update { state ->
            val alreadyInParty = state.partyMembers.any { it.id == friend.id }
            val updatedParty = if (alreadyInParty) {
                state.partyMembers.filter { it.id != friend.id }
            } else {
                if (state.partyMembers.size >= 4) {
                    state.partyMembers
                } else {
                    state.partyMembers + friend.copy(status = FriendStatus.IN_PARTY, isInvited = true)
                }
            }

            val updatedNearby = state.nearbyFriends.map {
                if (it.id == friend.id) it.copy(isInvited = !alreadyInParty, status = if (!alreadyInParty) FriendStatus.IN_PARTY else FriendStatus.NEARBY_RADAR) else it
            }
            val updatedOnline = state.onlineFriends.map {
                if (it.id == friend.id) it.copy(isInvited = !alreadyInParty, status = if (!alreadyInParty) FriendStatus.IN_PARTY else FriendStatus.ONLINE_AVAILABLE) else it
            }

            state.copy(
                partyMembers = updatedParty,
                nearbyFriends = updatedNearby,
                onlineFriends = updatedOnline,
                toastMessage = if (!alreadyInParty) "🎉 ${friend.name} joined your squad!" else "${friend.name} left the squad."
            )
        }
    }

    fun clearPartySquad() {
        _uiState.update { state ->
            state.copy(
                partyMembers = emptyList(),
                nearbyFriends = PreloadedGameData.nearbyFriendsList,
                onlineFriends = PreloadedGameData.onlineFriendsList,
                toastMessage = "Squad reset."
            )
        }
    }

    fun setEnteredPartyCode(code: String) {
        _uiState.update { it.copy(enteredPartyCode = code.uppercase()) }
    }

    fun joinPartyWithCode(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(toastMessage = "Connecting to Party Room $code...") }
            delay(900)
            val mockParty = PreloadedGameData.onlineFriendsList.take(3)
            _uiState.update {
                it.copy(
                    partyMembers = mockParty,
                    showPlayWithFriendsDialog = false,
                    toastMessage = "Joined Room $code! Launching game with squad!"
                )
            }
            selectGame(_uiState.value.playWithFriendsSelectedGame)
        }
    }

    fun startPartyGameWithFriends(gameType: GameShowType) {
        _uiState.update { it.copy(showPlayWithFriendsDialog = false) }
        selectGame(gameType)
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

    // --- NAVIGATION & SECTIONS ---
    fun navigateToSection(section: AppSection) {
        _uiState.update {
            it.copy(
                currentSection = section,
                selectedCategory = when (section) {
                    AppSection.TEAM_GAMES -> GameCategory.TEAM_GAMES
                    AppSection.SOLO_GAMES -> GameCategory.SOLO_GAMES
                    AppSection.DEBATE_ARENA -> GameCategory.DEBATE_ARENA
                    else -> it.selectedCategory
                }
            )
        }
    }

    // --- QUESTION AUDIO PLAYBACK ---
    fun playQuestionAudio() {
        val state = _uiState.value
        val questionText = when (state.selectedGameType) {
            GameShowType.FAMILY_FEUD -> "Survey question: ${state.currentFeudRound.question}"
            GameShowType.THE_PRICE_IS_RIGHT -> "Item up for bid: ${state.currentPriceItem.name}. ${state.currentPriceItem.description}"
            GameShowType.WHEEL_OF_FORTUNE -> "The category is: ${state.currentWheelPuzzle.category}. Spin the wheel or guess a letter!"
            GameShowType.JEOPARDY -> state.selectedJeopardyClue?.clue ?: "Select a Jeopardy clue on the game board."
            GameShowType.ONE_VS_100 -> "Question for the hot seat: ${state.currentMobQuestion.question}"
            GameShowType.DEBATE_SHOWDOWN -> "The motion before the arena: ${state.selectedDebateTopic.title}"
            null -> "Welcome to ZynGames Arena!"
        }
        _uiState.update {
            it.copy(
                toastMessage = "🔊 Playing question broadcast audio..."
            )
        }
        hostVoiceManager?.speak(questionText)
    }

    // --- MULTI-QUESTION SELECTORS ---
    fun selectFeudRound(index: Int) {
        if (index in PreloadedGameData.feudRounds.indices) {
            val round = PreloadedGameData.feudRounds[index]
            _uiState.update {
                it.copy(
                    feudRoundIndex = index,
                    currentFeudRound = round,
                    currentFeudAnswers = round.answers,
                    feudStrikes = 0,
                    feudFaceOffActive = true,
                    feudFaceOffWinner = null,
                    feudPlayOrPassActive = false,
                    feudSuggestions = round.answers.map { a -> a.text.split("/")[0].trim() },
                    feudSoundFxBanner = "🔔 Round ${index + 1}: ${round.question}"
                )
            }
        }
    }

    fun selectPriceItem(index: Int) {
        if (index in PreloadedGameData.priceItems.indices) {
            val item = PreloadedGameData.priceItems[index]
            _uiState.update {
                it.copy(
                    priceItemIndex = index,
                    currentPriceItem = item,
                    myPriceGuess = "",
                    isPriceSubmitted = false,
                    priceResultRevealed = false
                )
            }
        }
    }

    fun selectWheelPuzzle(index: Int) {
        if (index in PreloadedGameData.wheelPuzzles.indices) {
            val puzzle = PreloadedGameData.wheelPuzzles[index]
            _uiState.update {
                it.copy(
                    wheelPuzzleIndex = index,
                    currentWheelPuzzle = puzzle,
                    guessedLetters = emptySet(),
                    isSpinningWheel = false
                )
            }
        }
    }

    fun selectMobQuestion(index: Int) {
        if (index in PreloadedGameData.mobQuestions.indices) {
            val q = PreloadedGameData.mobQuestions[index]
            _uiState.update {
                it.copy(
                    mobQuestionIndex = index,
                    currentMobQuestion = q,
                    selectedMobOption = null,
                    isMobAnswered = false
                )
            }
        }
    }

    // --- DAILY LUCKY SPIN (ZERO COOLDOWN, ZERO ADS, HUGE JACKPOTS) ---
    fun spinDailyWheel() {
        if (_uiState.value.isSpinningDailyWheel) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSpinningDailyWheel = true, latestDailySpinReward = null) }
            delay(1600)
            val wonReward = PreloadedGameData.spinRewards.random()
            _uiState.update {
                it.copy(
                    isSpinningDailyWheel = false,
                    latestDailySpinReward = wonReward,
                    realCashBalance = it.realCashBalance + wonReward.valueCash,
                    freePointsBalance = it.freePointsBalance + wonReward.valuePoints,
                    dailySpinHistoryCount = it.dailySpinHistoryCount + 1,
                    toastMessage = "🎉 JACKPOT! You won ${wonReward.label}!"
                )
            }
            hostVoiceManager?.speak("Congratulations! You just won the ${wonReward.label} on the lucky spin wheel!")
        }
    }

    fun buyShopItem(item: ShopItem) {
        val state = _uiState.value
        if (item.priceCash != null && state.realCashBalance >= item.priceCash) {
            _uiState.update {
                it.copy(
                    realCashBalance = it.realCashBalance - item.priceCash,
                    purchasedShopItemIds = it.purchasedShopItemIds + item.id,
                    toastMessage = "🛍️ Purchased ${item.title}!"
                )
            }
        } else if (item.pricePoints != null && state.freePointsBalance >= item.pricePoints) {
            _uiState.update {
                it.copy(
                    freePointsBalance = it.freePointsBalance - item.pricePoints,
                    purchasedShopItemIds = it.purchasedShopItemIds + item.id,
                    toastMessage = "🛍️ Purchased ${item.title}!"
                )
            }
        } else {
            _uiState.update { it.copy(toastMessage = "Insufficient balance for ${item.title}") }
        }
    }

    fun claimDailyChallenge(challenge: DailyChallengeItem) {
        _uiState.update {
            it.copy(
                completedChallengeIds = it.completedChallengeIds + challenge.id,
                freePointsBalance = it.freePointsBalance + 5000,
                realCashBalance = it.realCashBalance + 50.0,
                toastMessage = "🎯 Claimed challenge reward for ${challenge.title}!"
            )
        }
    }

    // --- INSTANT QUIT & LOGOUT (NO CONFIRMATION DIALOGS) ---
    fun instantQuit() {
        _uiState.update {
            it.copy(
                activeGameStarted = false,
                selectedGameType = null,
                isMatchmaking = false,
                currentSection = AppSection.MAIN_MENU,
                toastMessage = "👋 Exited to Main Menu."
            )
        }
    }

    fun instantLogOut() {
        _uiState.update {
            it.copy(
                activeGameStarted = false,
                selectedGameType = null,
                currentSection = AppSection.MAIN_MENU,
                toastMessage = "🔒 Logged out successfully."
            )
        }
    }
}
