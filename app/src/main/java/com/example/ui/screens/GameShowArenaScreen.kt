package com.example.ui.screens

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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameShowArenaScreen(
    uiState: GameUiState,
    onSendMessage: (String) -> Unit,
    onToggleMic: () -> Unit,
    onToggleHostSecretSheet: () -> Unit,
    // Family Feud
    onRevealFeudAnswer: (Int) -> Unit,
    onAddFeudStrike: () -> Unit,
    onNextFeudRound: () -> Unit,
    // The Price Is Right
    onSubmitPriceGuess: (String) -> Unit,
    onRevealPriceResult: () -> Unit,
    onNextPriceItem: () -> Unit,
    // Wheel of Fortune
    onSpinWheel: () -> Unit,
    onGuessLetter: (Char) -> Unit,
    // Jeopardy
    onSelectJeopardyClue: (PreloadedJeopardyClue) -> Unit,
    onAnswerJeopardyClue: (Int) -> Unit,
    // 1 vs 100
    onAnswerMobQuestion: (Int) -> Unit,
    onNextMobQuestion: () -> Unit,
    onLeaveGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputChatMessage by remember { mutableStateOf("") }
    var inputPriceGuess by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf("BOARD") } // "BOARD" or "TEAM_COMMS"

    val isTeamGame = uiState.selectedGameType?.category == GameCategory.TEAM_GAMES
    val isRealCash = uiState.selectedEconomy == EconomyMode.REAL_CASH

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
                when (uiState.selectedGameType) {
                    GameShowType.FAMILY_FEUD -> {
                        FamilyFeudBoard(
                            round = uiState.currentFeudRound,
                            answers = uiState.currentFeudAnswers,
                            strikes = uiState.feudStrikes,
                            bank = uiState.feudBank,
                            isHostMode = uiState.isHostMode,
                            onReveal = onRevealFeudAnswer,
                            onAddStrike = onAddFeudStrike,
                            onNextRound = onNextFeudRound
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
}

// ----------------------------------------------------
// HOST PROMPTER & HEADERS
// ----------------------------------------------------

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
            Text("🤫", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
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
// 1. FAMILY FEUD BOARD
// ----------------------------------------------------

@Composable
fun FamilyFeudBoard(
    round: PreloadedFeudRound,
    answers: List<FeudAnswer>,
    strikes: Int,
    bank: Int,
    isHostMode: Boolean,
    onReveal: (Int) -> Unit,
    onAddStrike: () -> Unit,
    onNextRound: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = StadiumSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📊 FAMILY FEUD SURVEY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("BANK: \$$bank", fontSize = 12.sp, fontWeight = FontWeight.Black, color = ElectricGold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${round.question}\"",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Survey Answers
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(answers.mapIndexed { index, ans -> Pair(index, ans) }) { (idx, ans) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (ans.isRevealed) StadiumSurfaceVariant else Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (ans.isRevealed) NeonCyan else StadiumCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReveal(idx) }
                        .testTag("feud_answer_$idx")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (ans.isRevealed) NeonCyan else StadiumSurface,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${ans.rank}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ans.isRevealed) Color(0xFF002026) else TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
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
                                    text = "${ans.points} PTS",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
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

        // Strikes & Round Advance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text("STRIKES: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonRed)
                repeat(3) { i ->
                    Text(
                        text = if (i < strikes) "❌" else "⭕",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 1.dp)
                    )
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
// PRIVATE SQUAD COMMS VIEW
// ----------------------------------------------------

@Composable
fun PrivateTeamCommsView(
    team: Team?,
    messages: List<TeamChatMessage>,
    isMicMuted: Boolean,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onToggleMic: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = StadiumSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(team?.colorHex ?: 0xFF00B4D8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔒 PRIVATE CHANNEL: ${team?.name ?: "Squad"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(team?.colorHex ?: 0xFF00B4D8)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text("NO LEAKS TO OPPOSITION", fontSize = 9.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(team?.members ?: emptyList()) { player ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StadiumSurfaceVariant
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

        // Chat Log
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isCurrentUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (msg.isCurrentUser) NeonCyan.copy(alpha = 0.2f) else StadiumSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (msg.isCurrentUser) NeonCyan else StadiumCardBorder
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
                            Text(msg.message, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Box & Mic
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = { Text("Suggest guess to squad...", fontSize = 12.sp) },
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
