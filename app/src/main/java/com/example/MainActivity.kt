package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.ShowViewModel
import com.example.ui.screens.GameShowArenaScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StadiumDarkBg

class MainActivity : ComponentActivity() {
    private val viewModel: ShowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
}

@Composable
fun MainApp(viewModel: ShowViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.activeGameStarted && uiState.selectedGameType != null) {
        GameShowArenaScreen(
            uiState = uiState,
            onSendMessage = { viewModel.sendTeamMessage(it) },
            onToggleMic = { viewModel.toggleMic() },
            onToggleHostSecretSheet = { viewModel.toggleHostSecretSheet() },
            onRevealFeudAnswer = { viewModel.revealFeudAnswer(it) },
            onAddFeudStrike = { viewModel.addFeudStrike() },
            onNextFeudRound = { viewModel.nextFeudRound() },
            onSubmitPriceGuess = { viewModel.submitPriceGuess(it) },
            onRevealPriceResult = { viewModel.revealPriceResult() },
            onNextPriceItem = { viewModel.nextPriceItem() },
            onSpinWheel = { viewModel.spinWheel() },
            onGuessLetter = { viewModel.guessLetter(it) },
            onSelectJeopardyClue = { viewModel.selectJeopardyClue(it) },
            onAnswerJeopardyClue = { viewModel.answerJeopardyClue(it) },
            onAnswerMobQuestion = { viewModel.answerMobQuestion(it) },
            onNextMobQuestion = { viewModel.nextMobQuestion() },
            onLeaveGame = { viewModel.leaveGame() }
        )
    } else {
        HomeScreen(
            uiState = uiState,
            onEconomySelected = { viewModel.selectEconomy(it) },
            onCategorySelected = { viewModel.selectCategory(it) },
            onGameSelected = { viewModel.selectGame(it) },
            onToggleHostMode = { viewModel.toggleHostMode() }
        )
    }
}
