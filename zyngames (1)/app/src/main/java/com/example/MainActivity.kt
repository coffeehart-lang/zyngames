package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ai.HostVoiceManager
import com.example.data.AppSection
import com.example.ui.ShowViewModel
import com.example.ui.screens.GameShowArenaScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StadiumDarkBg
import com.example.util.NetworkConnectivityObserver

class MainActivity : ComponentActivity() {
    private val viewModel: ShowViewModel by viewModels()
    private var hostVoiceManager: HostVoiceManager? = null
    private var connectivityObserver: NetworkConnectivityObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        com.example.util.CrashReporter.log("App launch: MainActivity onCreate")

        hostVoiceManager = HostVoiceManager(this).also {
            viewModel.initVoiceManager(it)
        }

        connectivityObserver = NetworkConnectivityObserver(applicationContext).also {
            viewModel.initConnectivityObserver(it)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StadiumDarkBg
                ) {
                    MainApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hostVoiceManager?.shutdown()
    }
}

@Composable
fun MainApp(viewModel: ShowViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Prevent OS Back button from accidentally closing the app.
    // Instead navigate back gracefully.
    BackHandler(enabled = true) {
        if (uiState.activeGameStarted) {
            viewModel.leaveGame()
        } else if (uiState.currentSection != AppSection.MAIN_MENU) {
            viewModel.navigateToSection(AppSection.MAIN_MENU)
        } else {
            // Stay at Main Menu, do not exit
        }
    }

    if (uiState.activeGameStarted && uiState.selectedGameType != null) {
        GameShowArenaScreen(
            uiState = uiState,
            onSendMessage = { viewModel.sendTeamMessage(it) },
            onToggleMic = { viewModel.toggleMic() },
            onToggleHostSecretSheet = { viewModel.toggleHostSecretSheet() },
            onTellJoke = { viewModel.tellHostJoke() },
            onGiveCompliment = { viewModel.giveHostCompliment() },
            onAskQuestion = { viewModel.askCurrentGameQuestion() },
            onPlayQuestionAudio = { viewModel.playQuestionAudio() },
            onInteractWithAiHost = { viewModel.interactWithAiHost(it) },
            onToggleTalkToHost = { viewModel.toggleTalkToHostDialog(it) },
            onToggleAiCustomQuestion = { viewModel.toggleAiCustomQuestionDialog(it) },
            onRequestAiCustomQuestion = { viewModel.requestAiCustomQuestion(it) },
            onToggleHostVoice = { viewModel.toggleHostVoice() },
            onRevealFeudAnswer = { viewModel.revealFeudAnswer(it) },
            onAddFeudStrike = { viewModel.addFeudStrike() },
            onNextFeudRound = { viewModel.nextFeudRound() },
            onFeudBuzzFaceOff = { viewModel.feudBuzzFaceOff() },
            onFeudChoosePlayOrPass = { viewModel.feudChoosePlayOrPass(it) },
            onSelectFeudRound = { viewModel.selectFeudRound(it) },
            onFeudSuggestHuddleAnswer = { viewModel.feudSuggestHuddleAnswer(it) },
            onFeudCheerTeammates = { viewModel.feudCheerTeammates() },
            onFeudSubmitOfficialAnswer = { viewModel.submitFeudOfficialAnswer(it) },
            onFeudOpponentTurn = { viewModel.feudOpponentTurn() },
            onRerollFamilyNames = { viewModel.rerollFamilyNames() },
            onTogglePlayWithFriends = { viewModel.togglePlayWithFriendsDialog(it) },
            onSetPlayWithFriendsTab = { viewModel.setPlayWithFriendsTab(it) },
            onScanNearbyFriends = { viewModel.scanNearbyFriends() },
            onToggleInviteFriend = { viewModel.toggleInviteFriend(it) },
            onSetPartyCode = { viewModel.setEnteredPartyCode(it) },
            onJoinPartyCode = { viewModel.joinPartyWithCode(it) },
            onSelectPlayWithFriendsGame = { viewModel.selectPlayWithFriendsGame(it) },
            onStartPartyGameWithFriends = { viewModel.startPartyGameWithFriends(it) },
            onClearPartySquad = { viewModel.clearPartySquad() },
            onSubmitPriceGuess = { viewModel.submitPriceGuess(it) },
            onRevealPriceResult = { viewModel.revealPriceResult() },
            onNextPriceItem = { viewModel.nextPriceItem() },
            onSelectPriceItem = { viewModel.selectPriceItem(it) },
            onSpinWheel = { viewModel.spinWheel() },
            onGuessLetter = { viewModel.guessLetter(it) },
            onSelectWheelPuzzle = { viewModel.selectWheelPuzzle(it) },
            onSelectJeopardyClue = { viewModel.selectJeopardyClue(it) },
            onAnswerJeopardyClue = { viewModel.answerJeopardyClue(it) },
            onAnswerMobQuestion = { viewModel.answerMobQuestion(it) },
            onNextMobQuestion = { viewModel.nextMobQuestion() },
            onSelectMobQuestion = { viewModel.selectMobQuestion(it) },
            onSelectDebateTopic = { viewModel.selectDebateTopic(it) },
            onSetDebateSide = { viewModel.setDebateSide(it) },
            onSubmitDebateArgument = { viewModel.submitDebateArgument(it) },
            onVoteDebate = { viewModel.voteDebate(it) },
            onNextDebateStage = { viewModel.nextDebateStage() },
            onToggleCustomTopicDialog = { viewModel.toggleCustomTopicDialog() },
            onCreateCustomDebateTopic = { title, cat, pro, con ->
                viewModel.createCustomDebateTopic(title, cat, pro, con)
            },
            onLeaveGame = { viewModel.leaveGame() }
        )
    } else {
        HomeScreen(
            uiState = uiState,
            onEconomySelected = { viewModel.selectEconomy(it) },
            onCategorySelected = { viewModel.selectCategory(it) },
            onGameSelected = { viewModel.selectGame(it) },
            onToggleHostMode = { viewModel.toggleHostMode() },
            onSelectDebateTopic = { viewModel.selectDebateTopic(it) },
            onToggleCustomTopicDialog = { viewModel.toggleCustomTopicDialog() },
            onCreateCustomDebateTopic = { title, cat, pro, con ->
                viewModel.createCustomDebateTopic(title, cat, pro, con)
            },
            onTogglePlayWithFriends = { viewModel.togglePlayWithFriendsDialog(it) },
            onSetPlayWithFriendsTab = { viewModel.setPlayWithFriendsTab(it) },
            onScanNearbyFriends = { viewModel.scanNearbyFriends() },
            onToggleInviteFriend = { viewModel.toggleInviteFriend(it) },
            onSetPartyCode = { viewModel.setEnteredPartyCode(it) },
            onJoinPartyCode = { viewModel.joinPartyWithCode(it) },
            onSelectPlayWithFriendsGame = { viewModel.selectPlayWithFriendsGame(it) },
            onStartPartyGameWithFriends = { viewModel.startPartyGameWithFriends(it) },
            onClearPartySquad = { viewModel.clearPartySquad() },
            onNavigateToSection = { viewModel.navigateToSection(it) },
            onSpinDailyWheel = { viewModel.spinDailyWheel() },
            onBuyShopItem = { viewModel.buyShopItem(it) },
            onClaimDailyChallenge = { viewModel.claimDailyChallenge(it) },
            onInstantQuit = { viewModel.instantQuit() },
            onInstantLogOut = { viewModel.instantLogOut() }
        )
    }
}

