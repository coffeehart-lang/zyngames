package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.GameUiState
import com.example.ui.components.PlayWithFriendsDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameShowArenaScreen(
    uiState: GameUiState,
    onSendMessage: (String) -> Unit,
    onToggleMic: () -> Unit,
    onToggleHostSecretSheet: () -> Unit,
    onTellJoke: () -> Unit,
    onGiveCompliment: () -> Unit,
    onAskQuestion: () -> Unit = {},
    onPlayQuestionAudio: () -> Unit = {},
    onInteractWithAiHost: (String) -> Unit = {},
    onToggleTalkToHost: (Boolean?) -> Unit = {},
    onToggleAiCustomQuestion: (Boolean?) -> Unit = {},
    onRequestAiCustomQuestion: (String) -> Unit = {},
    onToggleHostVoice: () -> Unit = {},
    // Family Feud Authentic TV Show Actions
    onRevealFeudAnswer: (Int) -> Unit,
    onAddFeudStrike: () -> Unit,
    onNextFeudRound: () -> Unit,
    onFeudBuzzFaceOff: () -> Unit = {},
    onFeudChoosePlayOrPass: (Boolean) -> Unit = {},
    onSelectFeudRound: (Int) -> Unit = {},
    onFeudSuggestHuddleAnswer: (String) -> Unit = {},
    onFeudCheerTeammates: () -> Unit = {},
    onFeudSubmitOfficialAnswer: (String) -> Unit = {},
    onFeudOpponentTurn: () -> Unit = {},
    onRerollFamilyNames: () -> Unit = {},
    // Play with Friends & Radar
    onTogglePlayWithFriends: (Boolean?) -> Unit = {},
    onSetPlayWithFriendsTab: (String) -> Unit = {},
    onScanNearbyFriends: () -> Unit = {},
    onToggleInviteFriend: (FriendPlayer) -> Unit = {},
    onSetPartyCode: (String) -> Unit = {},
    onJoinPartyCode: (String) -> Unit = {},
    onSelectPlayWithFriendsGame: (GameShowType) -> Unit = {},
    onStartPartyGameWithFriends: (GameShowType) -> Unit = {},
    onClearPartySquad: () -> Unit = {},
    // The Price Is Right
    onSubmitPriceGuess: (String) -> Unit,
    onRevealPriceResult: () -> Unit,
    onNextPriceItem: () -> Unit,
    onSelectPriceItem: (Int) -> Unit = {},
    // Wheel of Fortune
    onSpinWheel: () -> Unit,
    onGuessLetter: (Char) -> Unit,
    onSelectWheelPuzzle: (Int) -> Unit = {},
    // Jeopardy
    onSelectJeopardyClue: (PreloadedJeopardyClue) -> Unit,
    onAnswerJeopardyClue: (Int) -> Unit,
    // 1 vs 100
    onAnswerMobQuestion: (Int) -> Unit,
    onNextMobQuestion: () -> Unit,
    onSelectMobQuestion: (Int) -> Unit = {},
    // Debate Arena Actions
    onSelectDebateTopic: (DebateTopic) -> Unit = {},
    onSetDebateSide: (Boolean) -> Unit = {},
    onSubmitDebateArgument: (String) -> Unit = {},
    onVoteDebate: (Boolean) -> Unit = {},
    onNextDebateStage: () -> Unit = {},
    onToggleCustomTopicDialog: () -> Unit = {},
    onCreateCustomDebateTopic: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onLeaveGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputChatMessage by remember { mutableStateOf("") }
    var inputPriceGuess by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf("BOARD") } // "BOARD" or "TEAM_COMMS"

    val isTeamGame = uiState.selectedGameType?.category == GameCategory.TEAM_GAMES
    val isRealCash = uiState.selectedEconomy == EconomyMode.REAL_CASH

    if (uiState.showPlayWithFriendsDialog) {
        PlayWithFriendsDialog(
            uiState = uiState,
            onDismiss = { onTogglePlayWithFriends(false) },
            onSetTab = onSetPlayWithFriendsTab,
            onScanNearby = onScanNearbyFriends,
            onToggleInvite = onToggleInviteFriend,
            onSetPartyCode = onSetPartyCode,
            onJoinPartyCode = onJoinPartyCode,
            onSelectGame = onSelectPlayWithFriendsGame,
            onStartGameWithSquad = onStartPartyGameWithFriends,
            onClearSquad = onClearPartySquad
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLeaveGame) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Column {
                            Text(
                                text = uiState.selectedGameType?.title ?: "Arena Match",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isRealCash) "💰 Real Cash Competition" else "🪙 Free Play ZynPoints",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isRealCash) EmeraldGreen else ElectricGold
                            )
                        }
                    }
                },
                actions = {
                    // Host Answers Teleprompter Button
                    if (uiState.isHostMode) {
                        IconButton(
                            onClick = onToggleHostSecretSheet,
                            modifier = Modifier.testTag("btn_host_answers_sheet")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Host Answers",
                                tint = CrimsonRed
                            )
                        }
                    }

                    if (isTeamGame) {
                        IconButton(
                            onClick = onToggleMic,
                            modifier = Modifier.testTag("mic_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (uiState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mic Toggle",
                                tint = if (uiState.isMicMuted) CrimsonRed else EmeraldGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StadiumDarkBg)
            )
        },
        containerColor = StadiumDarkBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp)
        ) {
            // Real-time Network Connectivity Warning Banner (Alerts if internet drops during active match)
            NetworkConnectionWarningBanner(isConnected = uiState.isInternetConnected)

            // Live Host Secret Prompter Alert (Visible only when in Host Mode)
            if (uiState.isHostMode) {
                HostPrompterBanner(
                    gameType = uiState.selectedGameType,
                    uiState = uiState,
                    onClickViewAll = onToggleHostSecretSheet
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Live Score & Stage Header Banner
            if (isTeamGame && uiState.currentTeam != null && uiState.opposingTeam != null) {
                TeamScoreHeader(
                    myTeam = uiState.currentTeam,
                    oppTeam = uiState.opposingTeam,
                    timerSeconds = uiState.timerSeconds
                )
            } else {
                SoloScoreHeader(
                    gameType = uiState.selectedGameType,
                    uiState = uiState,
                    isRealCash = isRealCash
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dual View Tab switcher for Team Games (Playing Board vs Private Team Comms)
            if (isTeamGame) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StadiumSurface, RoundedCornerShape(10.dp))
                        .padding(3.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = "BOARD" }
                            .testTag("tab_playing_board"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeTab == "BOARD") NeonCyan else Color.Transparent
                    ) {
                        Text(
                            text = "🏟️ Main Playing Board",
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == "BOARD") Color(0xFF002026) else TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = "TEAM_COMMS" }
                            .testTag("tab_team_comms"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeTab == "TEAM_COMMS") ElectricGold else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔒 Private Squad Comms",
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == "TEAM_COMMS") Color(0xFF3B2D00) else TextSecondary,
                                fontSize = 13.sp
                            )
                            if (uiState.teamMessages.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(CrimsonRed, CircleShape)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Main Content Area
            if (!isTeamGame || activeTab == "BOARD") {
                // Live Host Banter & Complimentary Dialogue Box
                LiveHostStageBanterBanner(
                    hostName = uiState.hostName,
                    hostBanter = uiState.hostBanter,
                    hostReactionEmoji = uiState.hostReactionEmoji,
                    isHostSpeaking = uiState.isHostSpeaking,
                    isHostVoiceEnabled = uiState.isHostVoiceEnabled,
                    isHostLoadingAi = uiState.isHostLoadingAi,
                    onAskQuestion = onAskQuestion,
                    onTellJoke = onTellJoke,
                    onGiveCompliment = onGiveCompliment,
                    onOpenTalkToHost = { onToggleTalkToHost(true) },
                    onOpenAiCustomQuestion = { onToggleAiCustomQuestion(true) },
                    onToggleHostVoice = onToggleHostVoice
                )

                Spacer(modifier = Modifier.height(10.dp))

                when (uiState.selectedGameType) {
                    GameShowType.FAMILY_FEUD -> {
                        FamilyFeudBoard(
                            round = uiState.currentFeudRound,
                            answers = uiState.currentFeudAnswers,
                            strikes = uiState.feudStrikes,
                            bank = uiState.feudBank,
                            isHostMode = uiState.isHostMode,
                            myTeam = uiState.currentTeam,
                            opposingTeam = uiState.opposingTeam,
                            faceOffActive = uiState.feudFaceOffActive,
                            faceOffWinner = uiState.feudFaceOffWinner,
                            playOrPassActive = uiState.feudPlayOrPassActive,
                            myTeamSpeech = uiState.feudMyTeamSpeech,
                            oppSpeech = uiState.feudOpponentSpeech,
                            suggestions = uiState.feudSuggestions,
                            isStealActive = uiState.feudIsStealActive,
                            soundFxBanner = uiState.feudSoundFxBanner,
                            currentRoundIndex = uiState.feudRoundIndex,
                            onReveal = onRevealFeudAnswer,
                            onAddStrike = onAddFeudStrike,
                            onNextRound = onNextFeudRound,
                            onBuzzFaceOff = onFeudBuzzFaceOff,
                            onChoosePlayOrPass = onFeudChoosePlayOrPass,
                            onPlayQuestionAudio = onPlayQuestionAudio,
                            onSelectRound = onSelectFeudRound,
                            onSuggestAnswer = onFeudSuggestHuddleAnswer,
                            onCheerSquad = onFeudCheerTeammates,
                            onSubmitOfficialAnswer = onFeudSubmitOfficialAnswer,
                            onOpponentTurn = onFeudOpponentTurn,
                            onRerollFamilyNames = onRerollFamilyNames,
                            onOpenPlayWithFriends = { onTogglePlayWithFriends(true) }
                        )
                    }
                    GameShowType.DEBATE_SHOWDOWN -> {
                        DebateArenaBoard(
                            topic = uiState.selectedDebateTopic,
                            topics = uiState.debateTopics,
                            stage = uiState.debateRoundStage,
                            isPro = uiState.userDebateSide,
                            timerSeconds = uiState.debateTimerSeconds,
                            isHostMode = uiState.isHostMode,
                            onSelectTopic = onSelectDebateTopic,
                            onSetSide = onSetDebateSide,
                            onSubmitArgument = onSubmitDebateArgument,
                            onVote = onVoteDebate,
                            onNextStage = onNextDebateStage,
                            onOpenCustomDialog = onToggleCustomTopicDialog
                        )
                    }
                    GameShowType.THE_PRICE_IS_RIGHT -> {
                        ThePriceIsRightBoard(
                            item = uiState.currentPriceItem,
                            myGuess = uiState.myPriceGuess,
                            teamMedian = uiState.teamMedianGuess,
                            oppMedian = uiState.oppTeamMedianGuess,
                            isSubmitted = uiState.isPriceSubmitted,
                            isRevealed = uiState.priceResultRevealed,
                            isHostMode = uiState.isHostMode,
                            inputGuess = inputPriceGuess,
                            onInputChange = { inputPriceGuess = it },
                            onSubmitGuess = {
                                onSubmitPriceGuess(inputPriceGuess)
                                inputPriceGuess = ""
                            },
                            onReveal = onRevealPriceResult,
                            onNextItem = onNextPriceItem
                        )
                    }
                    GameShowType.WHEEL_OF_FORTUNE -> {
                        WheelOfFortuneBoard(
                            puzzle = uiState.currentWheelPuzzle,
                            guessedLetters = uiState.guessedLetters,
                            multiplier = uiState.wheelMultiplier,
                            isSpinning = uiState.isSpinningWheel,
                            isHostMode = uiState.isHostMode,
                            onSpin = onSpinWheel,
                            onGuess = onGuessLetter
                        )
                    }
                    GameShowType.JEOPARDY -> {
                        JeopardyBoard(
                            clues = uiState.jeopardyClues,
                            selectedClue = uiState.selectedJeopardyClue,
                            score = uiState.jeopardyPlayerScore,
                            feedback = uiState.jeopardyAnswerFeedback,
                            isHostMode = uiState.isHostMode,
                            onSelectClue = onSelectJeopardyClue,
                            onAnswerClue = onAnswerJeopardyClue
                        )
                    }
                    GameShowType.ONE_VS_100 -> {
                        OneVs100Board(
                            question = uiState.currentMobQuestion,
                            selectedOption = uiState.selectedMobOption,
                            isAnswered = uiState.isMobAnswered,
                            mobCount = uiState.mobCount,
                            isHostMode = uiState.isHostMode,
                            onSelectOption = onAnswerMobQuestion,
                            onNextQuestion = onNextMobQuestion
                        )
                    }
                    else -> {}
                }
            } else {
                PrivateTeamCommsView(
                    team = uiState.currentTeam,
                    opposingTeam = uiState.opposingTeam,
                    messages = uiState.teamMessages,
                    isMicMuted = uiState.isMicMuted,
                    inputText = inputChatMessage,
                    onInputChange = { inputChatMessage = it },
                    onSendMessage = {
                        onSendMessage(inputChatMessage)
                        inputChatMessage = ""
                    },
                    onToggleMic = onToggleMic
                )
            }
        }
    }

    // Host Secret Sheet Modal
    if (uiState.showHostSecretSheet) {
        HostSecretAnswersModal(
            uiState = uiState,
            onDismiss = onToggleHostSecretSheet
        )
    }

    // Talk Directly to AI Host Dialog
    if (uiState.showTalkToHostDialog) {
        TalkToHostDialog(
            hostName = uiState.hostName,
            onDismiss = { onToggleTalkToHost(false) },
            onSendMessage = onInteractWithAiHost
        )
    }

    // AI Custom Question Generator Dialog
    if (uiState.showAiCustomQuestionDialog) {
        AiCustomQuestionDialog(
            gameType = uiState.selectedGameType ?: GameShowType.FAMILY_FEUD,
            onDismiss = { onToggleAiCustomQuestion(false) },
            onGenerateQuestion = onRequestAiCustomQuestion
        )
    }
}

// ----------------------------------------------------
// HOST PROMPTER & HEADERS
// ----------------------------------------------------

@Composable
fun LiveHostStageBanterBanner(
    hostName: String,
    hostBanter: String,
    hostReactionEmoji: String,
    isHostSpeaking: Boolean,
    isHostVoiceEnabled: Boolean,
    isHostLoadingAi: Boolean,
    onAskQuestion: () -> Unit,
    onTellJoke: () -> Unit,
    onGiveCompliment: () -> Unit,
    onOpenTalkToHost: () -> Unit,
    onOpenAiCustomQuestion: () -> Unit,
    onToggleHostVoice: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1E2E),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isHostSpeaking) ElectricGold else ElectricGold.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_host_banter_banner")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Host Avatar with pulsing border when speaking
                Box {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_master_host_avatar),
                        contentDescription = "Official Game Show Host",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isHostSpeaking) 2.5.dp else 1.5.dp,
                                color = if (isHostSpeaking) NeonCyan else ElectricGold,
                                shape = CircleShape
                            ),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    // Reaction Emoji overlay
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0F0F1A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                    ) {
                        Text(
                            text = hostReactionEmoji,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Host Dialogue Bubble & Live On-Air Status
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎙️ $hostName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = ElectricGold
                            )
                            if (isHostSpeaking) {
                                Spacer(modifier = Modifier.width(6.dp))
                                HostEqualizerWaveform()
                            }
                        }

                        // Voice Mute / Unmute Toggle Button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isHostVoiceEnabled) NeonCyan.copy(alpha = 0.2f) else Color(0xFF3A3A4A),
                            modifier = Modifier
                                .clickable { onToggleHostVoice() }
                                .testTag("btn_host_voice_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHostVoiceEnabled) "🔊 Audio ON" else "🔇 Muted",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHostVoiceEnabled) NeonCyan else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    if (isHostLoadingAi) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = ElectricGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Monte Carlo Smith is preparing live broadcast dialogue...",
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = ElectricGold
                            )
                        }
                    } else {
                        Text(
                            text = "\"$hostBanter\"",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Host broadcast actions (Ask question audio, Talk to host, Custom AI)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onAskQuestion,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D61)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1.2f).height(34.dp).testTag("host_ask_question_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🔊 Question Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }

                Button(
                    onClick = onOpenTalkToHost,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A2800)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(34.dp).testTag("host_talk_button")
                ) {
                    Text("💬 Talk to Host", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                }

                Button(
                    onClick = onOpenAiCustomQuestion,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E1F47)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1.1f).height(34.dp).testTag("host_custom_question_button")
                ) {
                    Text("✨ AI Round", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD6FA))
                }
            }
        }
    }
}

@Composable
fun HostEqualizerWaveform() {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "equalizer")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 14f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 4f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 16f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "h3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(16.dp)
    ) {
        Box(modifier = Modifier.width(3.dp).height(h1.dp).background(NeonCyan, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.width(3.dp).height(h2.dp).background(ElectricGold, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.width(3.dp).height(h3.dp).background(NeonCyan, RoundedCornerShape(2.dp)))
    }
}

@Composable
fun TalkToHostDialog(
    hostName: String,
    onDismiss: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var promptText by remember { mutableStateOf("") }
    val quickPrompts = listOf(
        "💡 Give us a clever hint for this round!",
        "🎙️ Roast the opposing squad's score!",
        "🔥 Hype up the studio audience for my team!",
        "❓ Tell us what makes a great game show champion!"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_master_host_avatar),
                    contentDescription = "Host Avatar",
                    modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, ElectricGold, CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Talk to $hostName", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Live AI Prime-Time Host", fontSize = 11.sp, color = ElectricGold)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Say anything to the host! Ask for a hint, banter, team coaching, or a playful roast.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Quick Talk Prompts:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickPrompts.forEach { quickPrompt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF26263B),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSendMessage(quickPrompt)
                                }
                        ) {
                            Text(
                                text = quickPrompt,
                                fontSize = 11.5.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    placeholder = { Text("Or type custom message...", fontSize = 12.sp, color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("input_talk_to_host"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricGold,
                        unfocusedBorderColor = Color(0xFF3E3E58),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (promptText.isNotBlank()) {
                        onSendMessage(promptText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                shape = RoundedCornerShape(10.dp),
                enabled = promptText.isNotBlank(),
                modifier = Modifier.testTag("btn_submit_talk_to_host")
            ) {
                Text("Send to Host 🎙️", color = Color(0xFF221600), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = Color(0xFF181828),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun AiCustomQuestionDialog(
    gameType: GameShowType,
    onDismiss: () -> Unit,
    onGenerateQuestion: (String) -> Unit
) {
    var topicText by remember { mutableStateOf("") }
    val sampleTopics = listOf(
        "90s Retro Cartoons & Snacks",
        "Famous Movie Quotes & Blockbusters",
        "World Travel & Airport Essentials",
        "Superheroes & Comic Lore",
        "Science Fiction Inventions"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✨ AI Custom Game Round", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Monte Carlo Smith will use Gemini AI to write and broadcast a brand new custom round for ${gameType.title}!",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Pick a Popular Topic:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    sampleTopics.forEach { topic ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF26263B),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onGenerateQuestion(topic)
                                }
                        ) {
                            Text(
                                text = "🎲 $topic",
                                fontSize = 11.5.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = topicText,
                    onValueChange = { topicText = it },
                    placeholder = { Text("Or enter custom topic...", fontSize = 12.sp, color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("input_custom_ai_topic"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF3E3E58),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (topicText.isNotBlank()) {
                        onGenerateQuestion(topicText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(10.dp),
                enabled = topicText.isNotBlank(),
                modifier = Modifier.testTag("btn_generate_custom_ai_round")
            ) {
                Text("Generate Round 🎲", color = Color(0xFF00222B), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = Color(0xFF181828),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun HostPrompterBanner(
    gameType: GameShowType?,
    uiState: GameUiState,
    onClickViewAll: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CrimsonRed.copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickViewAll() }
            .testTag("host_prompter_banner")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_master_host_avatar),
                contentDescription = "Master Host Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, ElectricGold, CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HOST TELEPROMPTER (SECRET ANSWERS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFB4AB)
                )
                val secretSummary = when (gameType) {
                    GameShowType.FAMILY_FEUD -> uiState.currentFeudRound.hostSecretNotes
                    GameShowType.THE_PRICE_IS_RIGHT -> uiState.currentPriceItem.hostSecretNotes
                    GameShowType.WHEEL_OF_FORTUNE -> uiState.currentWheelPuzzle.hostSecretNotes
                    GameShowType.JEOPARDY -> uiState.selectedJeopardyClue?.hostSecretNotes ?: "Select any clue to inspect hidden correct answer."
                    GameShowType.ONE_VS_100 -> uiState.currentMobQuestion.secretExplanation
                    else -> "Host secret prompter active."
                }
                Text(
                    text = secretSummary,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    maxLines = 2
                )
            }
            Text("EXPAND ↗", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
        }
    }
}

@Composable
fun TeamScoreHeader(
    myTeam: Team,
    oppTeam: Team,
    timerSeconds: Int
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(myTeam.colorHex), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = myTeam.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(myTeam.colorHex)
                    )
                }
                Text(
                    text = "${myTeam.totalScore} PTS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text("5 squad members", fontSize = 11.sp, color = TextSecondary)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Surface(shape = RoundedCornerShape(8.dp), color = StadiumSurfaceVariant) {
                    Text(
                        text = "⏱️ ${timerSeconds}s",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text("VS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = oppTeam.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(oppTeam.colorHex)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color(oppTeam.colorHex), CircleShape))
                }
                Text(
                    text = "${oppTeam.totalScore} PTS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text("4 rivals online", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun SoloScoreHeader(
    gameType: GameShowType?,
    uiState: GameUiState,
    isRealCash: Boolean
) {
    val poolText = if (isRealCash) "\$${gameType?.realCashPrizePool}" else "${gameType?.freePointsPrizePool} PTS"

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isRealCash) EmeraldGreen else ElectricGold),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(gameType?.icon ?: "⚡", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isRealCash) "GUARANTEED CASH PRIZE POOL" else "ZYNPOINTS REWARD POOL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRealCash) EmeraldGreen else ElectricGold
                )
                Text(
                    text = poolText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }
            if (gameType == GameShowType.ONE_VS_100) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StadiumSurfaceVariant
                ) {
                    Text(
                        text = "👥 Mob: ${uiState.mobCount}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            } else if (gameType == GameShowType.JEOPARDY) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StadiumSurfaceVariant
                ) {
                    Text(
                        text = "SCORE: $${uiState.jeopardyPlayerScore}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricGold
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 1. FAMILY FEUD BOARD (AUTHENTIC TV GAME SHOW EXPERIENCE)
// ----------------------------------------------------

@Composable
fun FamilyFeudBoard(
    round: PreloadedFeudRound,
    answers: List<FeudAnswer>,
    strikes: Int,
    bank: Int,
    isHostMode: Boolean,
    myTeam: Team?,
    opposingTeam: Team?,
    faceOffActive: Boolean,
    faceOffWinner: String?,
    playOrPassActive: Boolean = false,
    myTeamSpeech: FeudLiveSpeech?,
    oppSpeech: FeudLiveSpeech?,
    suggestions: List<String>,
    isStealActive: Boolean,
    soundFxBanner: String?,
    currentRoundIndex: Int = 0,
    onReveal: (Int) -> Unit,
    onAddStrike: () -> Unit,
    onNextRound: () -> Unit,
    onBuzzFaceOff: () -> Unit,
    onChoosePlayOrPass: (Boolean) -> Unit = {},
    onPlayQuestionAudio: () -> Unit = {},
    onSelectRound: (Int) -> Unit = {},
    onSuggestAnswer: (String) -> Unit,
    onCheerSquad: () -> Unit,
    onSubmitOfficialAnswer: (String) -> Unit,
    onOpponentTurn: () -> Unit,
    onRerollFamilyNames: () -> Unit = {},
    onOpenPlayWithFriends: () -> Unit = {}
) {
    var officialInputText by remember { mutableStateOf("") }
    var huddleInputText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Funny Family Showdown & Squad Control Bar
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF161E33),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👨‍👩‍👧‍👦", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${myTeam?.name ?: "The Harts"}  VS  ${opposingTeam?.name ?: "The Dingleberries"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricGold,
                            maxLines = 1
                        )
                        Text(
                            text = "Funny Family Matchup • Tap reroll for hilarious names",
                            fontSize = 9.5.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = onRerollFamilyNames,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C244A)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp).testTag("btn_reroll_funny_family_names")
                    ) {
                        Text("🎲 Reroll", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = onOpenPlayWithFriends,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003642)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp).testTag("btn_arena_open_squad")
                    ) {
                        Text("👥 Squad", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }
            }
        }

        // Multiple Survey Questions Quick Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SURVEY ROUNDS:", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                PreloadedGameData.feudRounds.forEachIndexed { idx, r ->
                    val isCurrent = idx == currentRoundIndex
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCurrent) ElectricGold else StadiumSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) Color(0xFF3B2D00) else Color(0xFF2A3A50)
                        ),
                        modifier = Modifier.clickable { onSelectRound(idx) }
                    ) {
                        Text(
                            text = "Q${idx + 1}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCurrent) Color(0xFF3B2D00) else TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // TV Broadcast Sound FX & Stage Announcement Banner
        item {
            if (soundFxBanner != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isStealActive || strikes >= 3) CrimsonRed.copy(alpha = 0.25f) else Color(0xFF1E2238),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isStealActive || strikes >= 3) CrimsonRed else ElectricGold
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isStealActive) "🚨" else "🎙️",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = soundFxBanner,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isStealActive) CrimsonRed else ElectricGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Survey Question Header & Bank Counter with Audio Playback
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                        ) {
                            Text(
                                text = "100 PEOPLE SURVEYED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onPlayQuestionAudio,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003844)),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("🔊 Audio", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricGold,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B2D00))
                        ) {
                            Text(
                                text = "BANK: \$$bank",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF3B2D00),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"${round.question}\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }
        }

        // Face-Off Center Podium Button (Who goes first)
        item {
            if (faceOffActive) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF311515),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CrimsonRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBuzzFaceOff() }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡ CAPTAINS FACE-OFF AT CENTER PODIUM ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hit buzzer first to decide who gets control of the survey board!",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onBuzzFaceOff,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🚨 HIT THE BUZZER! (BUZZ IN FIRST)", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }

        // Play or Pass Decision Stage
        item {
            if (playOrPassActive) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E2838),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏆 YOUR FAMILY HAS CONTROL OF THE BOARD!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Do you want to PLAY and clear all answers, or PASS to put the rivals on the spot?",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onChoosePlayOrPass(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🔥 PLAY BOARD", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF00281F))
                            }
                            Button(
                                onClick = { onChoosePlayOrPass(false) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🛡️ PASS BOARD", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Dual Teams Stage (Communicate with your team & Hear/See opponent's answers)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left Podium: Your Family (The Hart Family)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F2236),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛡️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = myTeam?.name ?: "The Hart Family",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Squad Members Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val members = myTeam?.members ?: listOf(
                                Player("1", "You", "😎", isCurrentUser = true),
                                Player("2", "Mike", "⚡"),
                                Player("3", "Jen", "👑"),
                                Player("4", "Tom", "🏷️")
                            )
                            members.take(4).forEach { p ->
                                Surface(
                                    shape = CircleShape,
                                    color = if (p.isCurrentUser) NeonCyan.copy(alpha = 0.3f) else StadiumSurfaceVariant,
                                    border = if (p.isCurrentUser) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(p.avatarEmoji, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Teammate Speech / Chatter Bubble
                        if (myTeamSpeech != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StadiumDarkBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${myTeamSpeech.speakerEmoji} ${myTeamSpeech.speakerName}: \"${myTeamSpeech.speechText}\"",
                                    fontSize = 10.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(6.dp),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = onCheerSquad,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003642)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.fillMaxWidth().height(26.dp)
                        ) {
                            Text("👏 \"Good Answer!\"", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                    }
                }

                // Right Podium: Opposing Family (The Davis Family)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2B141E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = opposingTeam?.name ?: "The Davis Family",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = CrimsonRed,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Opponent Squad Avatars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val oppMembers = opposingTeam?.members ?: listOf(
                                Player("5", "Dan", "🔥"),
                                Player("6", "Sonic", "🔊"),
                                Player("7", "Sam", "🧠"),
                                Player("8", "Star", "✨")
                            )
                            oppMembers.take(4).forEach { p ->
                                Surface(
                                    shape = CircleShape,
                                    color = StadiumSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(p.avatarEmoji, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Opponent Voiced Answer Bubble (Hear and see other team's answers)
                        if (oppSpeech != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StadiumDarkBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🔊", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${oppSpeech.speakerName}: \"${oppSpeech.speechText}\"",
                                        fontSize = 10.sp,
                                        color = if (oppSpeech.resultBanner == "CORRECT") ElectricGold else TextPrimary,
                                        lineHeight = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = onOpponentTurn,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A1828)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.fillMaxWidth().height(26.dp)
                        ) {
                            Text("🤖 Opponent Guess", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB4AB))
                        }
                    }
                }
            }
        }

        // Steal Opportunity Alert Banner
        item {
            if (isStealActive || strikes >= 3) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF421E00),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚨", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "3 STRIKES! STEAL OPPORTUNITY ACTIVE!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = ElectricGold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The defending squad racked up 3 strikes! One correct answer from your team steals the entire \$$bank bank!",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // The Iconic 8-Answer Trilithon Board
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF070D1F),
                border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    answers.forEachIndexed { idx, ans ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (ans.isRevealed) Color(0xFF0E3861) else Color(0xFF0F1B2B),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (ans.isRevealed) NeonCyan else Color(0xFF1E3A5F)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onReveal(idx) }
                                .testTag("feud_answer_$idx")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (ans.isRevealed) NeonCyan else Color(0xFF1E293B),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${ans.rank}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (ans.isRevealed) Color(0xFF002026) else ElectricGold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = if (ans.isRevealed || isHostMode) ans.text else "••••••••••••••••",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ans.isRevealed) TextPrimary else if (isHostMode) Color(0xFFFFB4AB) else TextMuted,
                                    modifier = Modifier.weight(1f)
                                )

                                if (ans.isRevealed || isHostMode) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (ans.isRevealed) ElectricGold else StadiumSurfaceVariant
                                    ) {
                                        Text(
                                            text = "${ans.points}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (ans.isRevealed) Color(0xFF3B2D00) else ElectricGold
                                        )
                                    }
                                } else {
                                    Text("TAP REVEAL", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Squad Huddle Suggestion & Answer Composer
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡 SQUAD HUDDLE & SUGGESTIONS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("(Tap suggestion to populate answer field)", fontSize = 9.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(suggestions) { sug ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StadiumSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                                modifier = Modifier.clickable {
                                    officialInputText = sug
                                    onSuggestAnswer(sug)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📝 ", fontSize = 10.sp)
                                    Text(
                                        text = sug,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Official Guess Input Row (Editable by user)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = officialInputText,
                            onValueChange = { officialInputText = it },
                            placeholder = { Text("Type or tap a squad suggestion...", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("feud_official_answer_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (officialInputText.isNotBlank()) {
                                    onSubmitOfficialAnswer(officialInputText)
                                    officialInputText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(50.dp).testTag("btn_feud_lock_in_answer")
                        ) {
                            Text("LOCK IN 🔒", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF3B2D00))
                        }
                    }
                }
            }
        }

        // Strikes & Round Controls
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Text("STRIKES: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonRed)
                    repeat(3) { i ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (i < strikes) CrimsonRed else Color(0xFF1E2238),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (i < strikes) CrimsonRed else StadiumCardBorder),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(horizontal = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (i < strikes) "❌" else "⭕",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onAddStrike,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("button_add_strike")
                ) {
                    Text("STRIKE ❌", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = onNextRound,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("button_next_feud_round")
                ) {
                    Text("NEXT Q ⏭️", color = Color(0xFF002026), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------------------------------------------
// DEBATE ARENA BOARD (CROSSFIRE SQUADS & LIVE VOTING)
// ----------------------------------------------------

@Composable
fun DebateArenaBoard(
    topic: DebateTopic,
    topics: List<DebateTopic>,
    stage: DebateRoundStage,
    isPro: Boolean,
    timerSeconds: Int,
    isHostMode: Boolean,
    onSelectTopic: (DebateTopic) -> Unit,
    onSetSide: (Boolean) -> Unit,
    onSubmitArgument: (String) -> Unit,
    onVote: (Boolean) -> Unit,
    onNextStage: () -> Unit,
    onOpenCustomDialog: () -> Unit
) {
    var argumentInput by remember { mutableStateOf("") }
    var selectedReaction by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Debate Topic Header & Switcher
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(2.dp, ElectricGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold)
                        ) {
                            Text(
                                text = topic.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = ElectricGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = onOpenCustomDialog,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("➕ Custom Topic", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\"${topic.title}\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pro Stance & Con Stance Mini Summaries
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F2537),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("⚡ PRO STANCE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                                Text(topic.proStance, fontSize = 10.sp, color = TextPrimary, maxLines = 2)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF331520),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("🛡️ CON STANCE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = CrimsonRed)
                                Text(topic.conStance, fontSize = 10.sp, color = TextPrimary, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }

        // Debate Stage Progress & Timer Bar
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STAGE: ${stage.title.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricGold
                        )
                        Text(
                            text = "Round target: ${stage.seconds}s limit",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Text(
                            text = "⏱️ ${timerSeconds}s",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = onNextStage,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Next Stage ⏭️", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B2D00))
                    }
                }
            }
        }

        // Live Tug-of-War Crowd Meter (PRO vs CON)
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚡ PRO: ${topic.votesPro}%", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                        Text("LIVE CROWD VERDICT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("CON: ${topic.votesCon}% 🛡️", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CrimsonRed)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Split Progress Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(topic.votesPro.toFloat().coerceAtLeast(1f))
                                .fillMaxHeight()
                                .background(NeonCyan)
                        )
                        Box(
                            modifier = Modifier
                                .weight(topic.votesCon.toFloat().coerceAtLeast(1f))
                                .fillMaxHeight()
                                .background(CrimsonRed)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Vote Buttons & Crowd Reactions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onVote(true) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003845)),
                            modifier = Modifier.weight(1f).height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("👍 Vote PRO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }

                        Button(
                            onClick = { onVote(false) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A1828)),
                            modifier = Modifier.weight(1f).height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("👎 Vote CON", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB4AB))
                        }

                        // Crowd Reaction Emojis
                        listOf("🔥", "👏", "🤯", "🎯").forEach { emoji ->
                            Surface(
                                shape = CircleShape,
                                color = if (selectedReaction == emoji) ElectricGold.copy(alpha = 0.3f) else StadiumSurfaceVariant,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { selectedReaction = emoji }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(emoji, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Side Selection Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StadiumSurface, RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSetSide(true) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPro) NeonCyan else Color.Transparent
                ) {
                    Text(
                        text = "⚡ I'm on PRO Side",
                        modifier = Modifier.padding(vertical = 6.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if (isPro) Color(0xFF002026) else TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSetSide(false) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (!isPro) CrimsonRed else Color.Transparent
                ) {
                    Text(
                        text = "🛡️ I'm on CON Side",
                        modifier = Modifier.padding(vertical = 6.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if (!isPro) Color.White else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Post New Argument Composer
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPro) NeonCyan.copy(alpha = 0.5f) else CrimsonRed.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isPro) "⚡ SUBMIT PRO ARGUMENT / EVIDENCE:" else "🛡️ SUBMIT CON ARGUMENT / REBUTTAL:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPro) NeonCyan else CrimsonRed
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = argumentInput,
                            onValueChange = { argumentInput = it },
                            placeholder = { Text("Present your point, stat, or rebuttal...") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (argumentInput.isNotBlank()) {
                                    onSubmitArgument(argumentInput)
                                    argumentInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPro) NeonCyan else CrimsonRed
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "POST 🚀",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPro) Color(0xFF002026) else Color.White
                            )
                        }
                    }
                }
            }
        }

        // Arguments Feed Header
        item {
            Text(
                text = "💬 LIVE ARENA ARGUMENTS & REBUTTALS (${topic.arguments.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = ElectricGold,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        // Arguments Feed List
        items(topic.arguments) { arg ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (arg.isPro) Color(0xFF0F1E2E) else Color(0xFF24131B),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (arg.isPro) NeonCyan.copy(alpha = 0.4f) else CrimsonRed.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(arg.authorEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = arg.authorName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (arg.isPro) NeonCyan else Color(0xFFFFB4AB)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (arg.isPro) NeonCyan.copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (arg.isPro) "PRO" else "CON",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (arg.isPro) NeonCyan else CrimsonRed,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(arg.timestamp, fontSize = 9.sp, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = arg.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StadiumDarkBg,
                            modifier = Modifier.clickable { /* Upvote arg */ }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👏", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${arg.likes} Agree", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 2. THE PRICE IS RIGHT BOARD
// ----------------------------------------------------

@Composable
fun ThePriceIsRightBoard(
    item: PreloadedPriceItem,
    myGuess: String,
    teamMedian: Int?,
    oppMedian: Int?,
    isSubmitted: Boolean,
    isRevealed: Boolean,
    isHostMode: Boolean,
    inputGuess: String,
    onInputChange: (String) -> Unit,
    onSubmitGuess: () -> Unit,
    onReveal: () -> Unit,
    onNextItem: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏷️ SHOWCASE PRIZE ITEM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(shape = RoundedCornerShape(6.dp), color = StadiumSurfaceVariant) {
                            Text(
                                text = item.category,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔒 HIVEMIND SQUAD BIDDING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Text(
                        text = "Submit your price guess. Your random squad's median consensus is locked in without going over!",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isSubmitted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = inputGuess,
                                onValueChange = onInputChange,
                                label = { Text("Your Estimate ($)") },
                                placeholder = { Text("e.g. 14500") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_price_guess"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = StadiumCardBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onSubmitGuess,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_submit_price_guess")
                            ) {
                                Text("LOCK IN", color = Color(0xFF002026), fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StadiumSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("✅ YOUR ESTIMATE SUBMITTED: \$$myGuess", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("⚡ Squad Consensus Median: \$${teamMedian ?: 0}", fontWeight = FontWeight.Bold, color = NeonCyan)
                                Text("⚡ Rival Squad Consensus: \$${oppMedian ?: 0}", fontWeight = FontWeight.Bold, color = CrimsonRed)
                            }
                        }
                    }
                }
            }
        }

        item {
            if (isSubmitted || isHostMode) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StadiumSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isRevealed) EmeraldGreen else ElectricGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📺 ACTUAL RETAIL PRICE (ARP)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                        Spacer(modifier = Modifier.height(6.dp))

                        if (isRevealed) {
                            Text(
                                text = "\$${item.actualRetailPrice}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = EmeraldGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🎉 Team Volt Blue was closest without going over (+500 PTS)!", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onNextItem,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold)
                            ) {
                                Text("NEXT SHOWCASE ITEM ⏭️", color = Color(0xFF3B2D00), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = onReveal,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_reveal_actual_price")
                            ) {
                                Text("REVEAL ACTUAL PRICE 🔔", color = Color(0xFF3B2D00), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. WHEEL OF FORTUNE BOARD
// ----------------------------------------------------

@Composable
fun WheelOfFortuneBoard(
    puzzle: PreloadedWheelPuzzle,
    guessedLetters: Set<Char>,
    multiplier: Int,
    isSpinning: Boolean,
    isHostMode: Boolean,
    onSpin: () -> Unit,
    onGuess: (Char) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = StadiumSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("🎡 CATEGORY: ${puzzle.category}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
                Spacer(modifier = Modifier.height(10.dp))

                // Puzzle Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    puzzle.secretPhrase.split(" ").forEach { word ->
                        Row(modifier = Modifier.padding(horizontal = 3.dp)) {
                            word.forEach { char ->
                                val isGuessed = guessedLetters.contains(char)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isGuessed) Color.White else if (isHostMode) Color(0xFF2A1B4E) else Color(0xFF1B233D),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                                    modifier = Modifier
                                        .size(22.dp, 30.dp)
                                        .padding(1.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isGuessed) {
                                            Text(
                                                text = "$char",
                                                fontWeight = FontWeight.Black,
                                                color = Color.Black,
                                                fontSize = 13.sp
                                            )
                                        } else if (isHostMode) {
                                            Text(
                                                text = "$char",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB4AB),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Spinning Wheel Control
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = StadiumSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isSpinning) "🌀 WHEEL IS SPINNING..." else "CURRENT LETTER REWARD: \$$multiplier",
                    fontWeight = FontWeight.Bold,
                    color = ElectricGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onSpin,
                    enabled = !isSpinning,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                    modifier = Modifier.testTag("btn_spin_wheel")
                ) {
                    Text("SPIN MULTIPLIER WHEEL 🎡", color = Color(0xFF3B2D00), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Letter Call Keyboard
        Text("CALL A CONSONANT / VOWEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))

        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(alphabet.toList()) { letter ->
                val alreadyPicked = guessedLetters.contains(letter)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (alreadyPicked) StadiumSurfaceVariant else NeonCyan,
                    modifier = Modifier
                        .size(34.dp)
                        .clickable(enabled = !alreadyPicked) { onGuess(letter) }
                        .testTag("letter_key_$letter")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$letter",
                            fontWeight = FontWeight.Bold,
                            color = if (alreadyPicked) TextMuted else Color(0xFF002026)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. JEOPARDY! BOARD
// ----------------------------------------------------

@Composable
fun JeopardyBoard(
    clues: List<PreloadedJeopardyClue>,
    selectedClue: PreloadedJeopardyClue?,
    score: Int,
    feedback: String?,
    isHostMode: Boolean,
    onSelectClue: (PreloadedJeopardyClue) -> Unit,
    onAnswerClue: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedClue == null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("⚡ JEOPARDY! CATEGORY BOARD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(clues) { clue ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (clue.isAnswered) StadiumSurfaceVariant else Color(0xFF0C2461),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (clue.isAnswered) StadiumCardBorder else ElectricGold
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !clue.isAnswered) { onSelectClue(clue) }
                                    .testTag("jeopardy_clue_${clue.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = clue.category,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (clue.isAnswered) TextMuted else TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = if (clue.isAnswered) "COMPLETED" else "\$${clue.value}",
                                        fontWeight = FontWeight.Black,
                                        color = if (clue.isAnswered) TextMuted else GoldYellow,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Clue In Play
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedClue.category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("\$${selectedClue.value}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldYellow)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${selectedClue.clue}\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (isHostMode) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🤫 HOST ANSWER: ${selectedClue.secretAnswer}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB4AB)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    selectedClue.options.forEachIndexed { index, option ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StadiumSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { onAnswerClue(index) }
                                .testTag("jeopardy_opt_$index")
                        ) {
                            Text(
                                text = "What is $option?",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    if (feedback != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(feedback, fontWeight = FontWeight.Bold, color = if (feedback.startsWith("CORRECT")) EmeraldGreen else CrimsonRed)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. 1 VS 100 BOARD
// ----------------------------------------------------

@Composable
fun OneVs100Board(
    question: PreloadedMobQuestion,
    selectedOption: Int?,
    isAnswered: Boolean,
    mobCount: Int,
    isHostMode: Boolean,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = StadiumSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧠 TIER ${question.tier} MOB QUESTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("REMAINING MOB: $mobCount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        question.options.forEachIndexed { index, opt ->
            val isSelected = selectedOption == index
            val isCorrect = index == question.correctIndex

            val cardColor = when {
                isAnswered && isCorrect -> EmeraldGreen.copy(alpha = 0.2f)
                isAnswered && isSelected && !isCorrect -> CrimsonRed.copy(alpha = 0.2f)
                isSelected -> ElectricGold.copy(alpha = 0.2f)
                else -> StadiumSurface
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = cardColor,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAnswered && isCorrect) EmeraldGreen else StadiumCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable(enabled = !isAnswered) { onSelectOption(index) }
                    .testTag("mob_option_$index")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${'A' + index}.",
                        fontWeight = FontWeight.Black,
                        color = ElectricGold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = opt,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (isAnswered && isCorrect) {
                        Text("CORRECT 🎯", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        if (isAnswered) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onNextQuestion,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("NEXT MOB QUESTION ⏭️", color = Color(0xFF3B2D00), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// PRIVATE SQUAD COMMS VIEW (TEAM-RESTRICTED REAL-TIME MESSAGING)
// ----------------------------------------------------

@Composable
fun PrivateTeamCommsView(
    team: Team?,
    opposingTeam: Team? = null,
    messages: List<TeamChatMessage>,
    isMicMuted: Boolean,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onToggleMic: () -> Unit
) {
    val currentTeamId = team?.id ?: "team_my"
    // CRITICAL PRIVACY BOUNDARY: Strictly filter messages for current team only.
    // Opposing team communications are never displayed in this view.
    val teamRestrictedMessages = remember(messages, currentTeamId) {
        messages.filter { it.teamId == currentTeamId }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Team Privacy & Isolation Security Header
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = StadiumSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(team?.colorHex ?: 0xFF00B4D8)),
            modifier = Modifier.fillMaxWidth().testTag("team_channel_privacy_header")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(team?.colorHex ?: 0xFF00B4D8), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🔒 ${team?.name ?: "Your Squad"} Team Channel",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(team?.colorHex ?: 0xFF00B4D8)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EmeraldGreen.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("🛡️ END-TO-END TEAM ISOLATED", fontSize = 8.sp, color = EmeraldGreen, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Security explanation banner
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F1424)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👀 Hidden from ${opposingTeam?.name ?: "Opposing Squad"} • Only ${team?.members?.size ?: 5} squad teammates can see and hear these comms",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🛡️ ZERO LEAKS", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Teammates in private channel
                Text(
                    text = "ACTIVE TEAMMATES IN PRIVATE HUDDLE (${team?.members?.size ?: 0}):",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(team?.members ?: emptyList()) { player ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StadiumSurfaceVariant,
                            border = if (player.isCurrentUser) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(player.avatarEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (player.isCurrentUser) "You" else player.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (player.isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                                    color = if (player.isCaptain) ElectricGold else TextPrimary
                                )
                                if (player.isCaptain) {
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("👑", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Team Chat Log (Restricted to Team Members Only)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = StadiumSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (teamRestrictedMessages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔒", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Private Team Comms Channel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "Discuss answers and coordinate secret tactics here.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(teamRestrictedMessages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isCurrentUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (msg.isCurrentUser) NeonCyan.copy(alpha = 0.2f) else StadiumSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (msg.isCurrentUser) NeonCyan else Color(0xFF2E334D)
                                ),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(msg.senderEmoji, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = msg.senderName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (msg.isCurrentUser) NeonCyan else ElectricGold
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(msg.timestamp, fontSize = 9.sp, color = TextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.message,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestions for rapid private team huddle
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val quickIdeas = listOf("💡 I think it's top 3!", "🔥 Lock it in!", "🤔 Maybe something else?", "⚡ Buzz fast!")
            items(quickIdeas) { idea ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StadiumSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                    modifier = Modifier.clickable {
                        onInputChange(idea)
                    }
                ) {
                    Text(
                        text = idea,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Input Box & Mic
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = { Text("Suggest secret guess to team...", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = StadiumCardBorder
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onSendMessage,
                modifier = Modifier
                    .background(NeonCyan, RoundedCornerShape(8.dp))
                    .size(44.dp)
                    .testTag("send_chat_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF002026))
            }
        }
    }
}

// ----------------------------------------------------
// HOST SECRET ANSWERS MODAL
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostSecretAnswersModal(
    uiState: GameUiState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = StadiumSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎙️ HOST PROMPTER & ALL SECRET ANSWERS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = CrimsonRed)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Confidential Host Cues — Preloaded answers for all active show rounds.",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("1. FAMILY FEUD SURVEY ROUNDS", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 12.sp)
                    PreloadedGameData.feudRounds.forEach { r ->
                        Surface(shape = RoundedCornerShape(8.dp), color = StadiumSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(r.question, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                r.answers.forEach { ans ->
                                    Text("• #${ans.rank} ${ans.text} (${ans.points} pts)", fontSize = 11.sp, color = ElectricGold)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("2. THE PRICE IS RIGHT EXACT PRICES", fontWeight = FontWeight.Bold, color = ElectricGold, fontSize = 12.sp)
                    PreloadedGameData.priceItems.forEach { p ->
                        Surface(shape = RoundedCornerShape(8.dp), color = StadiumSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(p.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                Text("EXACT ARP: \$${p.actualRetailPrice}", fontWeight = FontWeight.Black, color = EmeraldGreen, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Text("3. WHEEL OF FORTUNE SECRET PUZZLES", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                    PreloadedGameData.wheelPuzzles.forEach { w ->
                        Surface(shape = RoundedCornerShape(8.dp), color = StadiumSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(w.category, fontSize = 11.sp, color = NeonPurple)
                                Text("ANSWER: \"${w.secretPhrase}\"", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkConnectionWarningBanner(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isConnected,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse_warning")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha_anim"
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CrimsonRed.copy(alpha = 0.22f * alpha),
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .border(1.5.dp, CrimsonRed.copy(alpha = alpha), RoundedCornerShape(12.dp))
                .testTag("network_warning_banner")
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "Connection Lost Warning",
                    tint = CrimsonRed,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "⚠️ Connection Lost — Reconnecting...",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Match state & scores are locally cached. Rejoining arena automatically.",
                        color = Color(0xFFFFCDD2),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CrimsonRed,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = "OFFLINE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

