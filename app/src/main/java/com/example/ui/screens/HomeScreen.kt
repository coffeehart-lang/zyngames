package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EconomyMode
import com.example.data.GameCategory
import com.example.data.GameShowType
import com.example.ui.GameUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: GameUiState,
    onEconomySelected: (EconomyMode) -> Unit,
    onCategorySelected: (GameCategory) -> Unit,
    onGameSelected: (GameShowType) -> Unit,
    onToggleHostMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRealMoney = uiState.selectedEconomy == EconomyMode.REAL_CASH

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👑 ZYNGAMES ARENA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = if (isRealMoney) EmeraldGreen else NeonCyan
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Host Mode Toggle Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (uiState.isHostMode) CrimsonRed else StadiumSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (uiState.isHostMode) CrimsonRed else StadiumCardBorder
                            ),
                            modifier = Modifier
                                .clickable { onToggleHostMode() }
                                .testTag("btn_toggle_host_mode")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (uiState.isHostMode) "🎙️ HOST MODE: ON" else "🎙️ HOST: OFF",
                                    color = if (uiState.isHostMode) Color.White else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                .padding(horizontal = 16.dp)
        ) {
            // Real Cash vs Free Play Mode Switcher Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isRealMoney) EmeraldGreen.copy(alpha = 0.8f) else ElectricGold.copy(alpha = 0.8f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StadiumDarkBg, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        // Real Money Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onEconomySelected(EconomyMode.REAL_CASH) }
                                .testTag("tab_real_cash"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isRealMoney) EmeraldGreen else Color.Transparent
                        ) {
                            Text(
                                text = "💰 Real Cash ($)",
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isRealMoney) Color(0xFF00281F) else TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        // Free Play Tab
                        val isFree = uiState.selectedEconomy == EconomyMode.FREE_PLAY
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onEconomySelected(EconomyMode.FREE_PLAY) }
                                .testTag("tab_free_play"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isFree) ElectricGold else Color.Transparent
                        ) {
                            Text(
                                text = "🪙 Free Play (Points)",
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isFree) Color(0xFF3B2D00) else TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Balance Display Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isRealMoney) "YOUR CASH WALLET" else "YOUR ZYNPOINTS BALANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRealMoney) EmeraldGreen else ElectricGold
                            )
                            Text(
                                text = if (isRealMoney) "$${String.format("%.2f", uiState.realCashBalance)}" else "${uiState.freePointsBalance} PTS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isRealMoney) EmeraldGreen.copy(alpha = 0.15f) else ElectricGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isRealMoney) EmeraldGreen.copy(alpha = 0.5f) else ElectricGold.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = if (isRealMoney) "⚡ INSTANT PAYOUTS" else "🎮 CASUAL LEADERBOARDS",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isRealMoney) EmeraldGreen else ElectricGold
                            )
                        }
                    }
                }
            }

            // Team Games vs Solo Games Category Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(StadiumSurface, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                val isTeam = uiState.selectedCategory == GameCategory.TEAM_GAMES
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCategorySelected(GameCategory.TEAM_GAMES) }
                        .testTag("tab_team_category"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isTeam) NeonCyan else Color.Transparent
                ) {
                    Text(
                        text = "👥 Team Games (Random Squads)",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if (isTeam) Color(0xFF002026) else TextSecondary,
                        fontSize = 12.sp
                    )
                }

                val isSolo = uiState.selectedCategory == GameCategory.SOLO_GAMES
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCategorySelected(GameCategory.SOLO_GAMES) }
                        .testTag("tab_solo_category"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSolo) NeonPurple else Color.Transparent
                ) {
                    Text(
                        text = "⚡ Solo Games",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if (isSolo) Color.White else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            if (uiState.isHostMode) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CrimsonRed.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎙️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HOST TELEPROMPTER ACTIVE: Secret answers & host cues will be visible on your stage screen.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB4AB)
                        )
                    }
                }
            }

            // Games List
            val currentGames = GameShowType.values().filter { it.category == uiState.selectedCategory }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(currentGames) { game ->
                    GameCard(
                        game = game,
                        isRealMoney = isRealMoney,
                        onJoin = { onGameSelected(game) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: GameShowType,
    isRealMoney: Boolean,
    onJoin: () -> Unit
) {
    val prizeString = if (isRealMoney) {
        "\$${game.realCashPrizePool} CASH POOL"
    } else {
        "${game.freePointsPrizePool} ZYNPOINTS"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJoin() }
            .testTag("game_card_${game.name.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        color = StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isRealMoney) EmeraldGreen.copy(alpha = 0.4f) else StadiumCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(game.icon, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = game.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isRealMoney) EmeraldGreen else ElectricGold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isRealMoney) EmeraldGreen.copy(alpha = 0.2f) else StadiumSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isRealMoney) EmeraldGreen else ElectricGold
                    )
                ) {
                    Text(
                        text = prizeString,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isRealMoney) EmeraldGreen else ElectricGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = game.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onJoin,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("join_button_${game.name.lowercase()}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRealMoney) EmeraldGreen else if (game.category == GameCategory.TEAM_GAMES) NeonCyan else NeonPurple,
                    contentColor = if (isRealMoney) Color(0xFF00281F) else if (game.category == GameCategory.TEAM_GAMES) Color(0xFF002026) else Color.White
                )
            ) {
                Icon(
                    imageVector = if (game.category == GameCategory.TEAM_GAMES) Icons.Default.Groups else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (game.category == GameCategory.TEAM_GAMES) "AUTO-ASSIGN SQUAD & PLAY" else "ENTER SOLO SHOWDOWN",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
