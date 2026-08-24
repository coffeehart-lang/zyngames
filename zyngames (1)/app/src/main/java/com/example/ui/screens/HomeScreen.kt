package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
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
fun HomeScreen(
    uiState: GameUiState,
    onEconomySelected: (EconomyMode) -> Unit,
    onCategorySelected: (GameCategory) -> Unit,
    onGameSelected: (GameShowType) -> Unit,
    onToggleHostMode: () -> Unit,
    onSelectDebateTopic: (DebateTopic) -> Unit = {},
    onToggleCustomTopicDialog: () -> Unit = {},
    onCreateCustomDebateTopic: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onTogglePlayWithFriends: (Boolean?) -> Unit = {},
    onSetPlayWithFriendsTab: (String) -> Unit = {},
    onScanNearbyFriends: () -> Unit = {},
    onToggleInviteFriend: (FriendPlayer) -> Unit = {},
    onSetPartyCode: (String) -> Unit = {},
    onJoinPartyCode: (String) -> Unit = {},
    onSelectPlayWithFriendsGame: (GameShowType) -> Unit = {},
    onStartPartyGameWithFriends: (GameShowType) -> Unit = {},
    onClearPartySquad: () -> Unit = {},
    onNavigateToSection: (AppSection) -> Unit = {},
    onSpinDailyWheel: () -> Unit = {},
    onBuyShopItem: (ShopItem) -> Unit = {},
    onClaimDailyChallenge: (DailyChallengeItem) -> Unit = {},
    onInstantQuit: () -> Unit = {},
    onInstantLogOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isRealMoney = uiState.selectedEconomy == EconomyMode.REAL_CASH
    var customTitle by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("Tech & AI") }
    var customPro by remember { mutableStateOf("") }
    var customCon by remember { mutableStateOf("") }

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

    if (uiState.showCustomTopicDialog) {
        AlertDialog(
            onDismissRequest = onToggleCustomTopicDialog,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎙️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Custom Debate Topic", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter any topic, question, or proposition you want to debate live:", fontSize = 12.sp, color = TextSecondary)
                    
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Debate Topic / Motion") },
                        placeholder = { Text("e.g. Electric vehicles vs Hydrogen fuel") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Category") },
                        placeholder = { Text("e.g. Tech, Society, Food, Sports") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customPro,
                        onValueChange = { customPro = it },
                        label = { Text("PRO Stance (For the motion)") },
                        placeholder = { Text("Core reasoning for PRO side") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customCon,
                        onValueChange = { customCon = it },
                        label = { Text("CON Stance (Against the motion)") },
                        placeholder = { Text("Core reasoning for CON side") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTitle.isNotBlank()) {
                            onCreateCustomDebateTopic(customTitle, customCategory, customPro, customCon)
                            onGameSelected(GameShowType.DEBATE_SHOWDOWN)
                            customTitle = ""
                            customPro = ""
                            customCon = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Launch Debate Arena 🚀", color = Color(0xFF002026), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onToggleCustomTopicDialog) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = StadiumSurface
        )
    }

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
            // Horizontal Navigation Pill Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val sections: List<Pair<AppSection, String>> = listOf(
                    AppSection.MAIN_MENU to "🏠 Menu",
                    AppSection.TEAM_GAMES to "👥 Team Games",
                    AppSection.SOLO_GAMES to "⚡ Solo Games",
                    AppSection.DEBATE_ARENA to "🎙️ Debate",
                    AppSection.LEADERBOARDS to "🏆 Leaderboards",
                    AppSection.DAILY_CHALLENGES to "🎯 Challenges",
                    AppSection.DAILY_SPIN to "🎡 Lucky Spin",
                    AppSection.SHOP to "🛍️ Shop",
                    AppSection.ACHIEVEMENTS to "🎖️ Badges",
                    AppSection.GAME_SETTINGS to "⚙️ Settings",
                    AppSection.PROFILE to "👤 Profile",
                    AppSection.HELP to "❓ Rules"
                )
                items(sections) { (sec, label) ->
                    val isCurrent = uiState.currentSection == sec
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCurrent) NeonCyan else StadiumSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) NeonCyan else Color(0xFF263345)
                        ),
                        modifier = Modifier.clickable { onNavigateToSection(sec) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color(0xFF002026) else TextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            if (uiState.currentSection != AppSection.MAIN_MENU) {
                when (uiState.currentSection) {
                    AppSection.TEAM_GAMES -> {
                        TeamGamesSectionView(
                            uiState = uiState,
                            onGameSelected = onGameSelected,
                            onTogglePlayWithFriends = onTogglePlayWithFriends,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.SOLO_GAMES -> {
                        SoloGamesSectionView(
                            uiState = uiState,
                            onGameSelected = onGameSelected,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.DEBATE_ARENA -> {
                        DebateSectionView(
                            uiState = uiState,
                            onSelectDebateTopic = onSelectDebateTopic,
                            onToggleCustomTopicDialog = onToggleCustomTopicDialog,
                            onGameSelected = onGameSelected,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.LEADERBOARDS -> {
                        LeaderboardsSectionView(
                            uiState = uiState,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.DAILY_CHALLENGES -> {
                        DailyChallengesSectionView(
                            uiState = uiState,
                            onClaimChallenge = onClaimDailyChallenge,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.DAILY_SPIN -> {
                        DailySpinSectionView(
                            uiState = uiState,
                            onSpinWheel = onSpinDailyWheel,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.SHOP -> {
                        RewardShopSectionView(
                            uiState = uiState,
                            onBuyItem = onBuyShopItem,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.ACHIEVEMENTS -> {
                        AchievementsSectionView(
                            uiState = uiState,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.GAME_SETTINGS, AppSection.SETTINGS -> {
                        GameSettingsSectionView(
                            uiState = uiState,
                            onInstantQuit = onInstantQuit,
                            onInstantLogOut = onInstantLogOut,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.PROFILE -> {
                        ProfileSectionView(
                            uiState = uiState,
                            onInstantLogOut = onInstantLogOut,
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    AppSection.HELP -> {
                        HelpRulesSectionView(
                            onBackToMenu = { onNavigateToSection(AppSection.MAIN_MENU) }
                        )
                    }
                    else -> {}
                }
            } else {
            // Host Avatar Spotlight Banner (The Official Host of ZynGames Arena)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_master_host_avatar),
                            contentDescription = "Official Game Show Host Avatar",
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(2.dp, ElectricGold, CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🎙️ ${uiState.hostName.uppercase()} (LIVE AI HOST)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ElectricGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (uiState.isHostSpeaking) NeonCyan.copy(alpha = 0.25f) else EmeraldGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (uiState.isHostSpeaking) "🔊 SPEAKING NOW" else "🔴 LIVE ON STAGE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isHostSpeaking) NeonCyan else EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Charismatic AI Broadcast Host & MC",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "\"${uiState.hostBanter}\"",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 🎮 Play With Friends Spotlight Hub Card (Nearby Radar & Online Lobbies)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1E33),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onTogglePlayWithFriends(true) }
                    .testTag("card_play_with_friends_spotlight")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👥", fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PLAY WITH FRIENDS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonCyan
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ElectricGold.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "📡 RADAR + PIN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ElectricGold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Invite nearby devices sitting with you or join online party rooms!",
                                fontSize = 10.5.sp,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = { onTogglePlayWithFriends(true) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("btn_open_friends_lobby")
                        ) {
                            Text("Open Lobby", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF002026))
                        }
                    }

                    if (uiState.partyMembers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(StadiumDarkBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CURRENT SQUAD:", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("😎 You", fontSize = 10.sp, color = TextPrimary)
                            uiState.partyMembers.forEach { m ->
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${m.avatarEmoji} ${m.name}", fontSize = 10.sp, color = NeonCyan)
                            }
                        }
                    }
                }
            }

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
                        // Championship Gold Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onEconomySelected(EconomyMode.REAL_CASH) }
                                .testTag("tab_real_cash"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isRealMoney) EmeraldGreen else Color.Transparent
                        ) {
                            Text(
                                text = "👑 ZynGold (🪙)",
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
                                text = if (isRealMoney) "YOUR ZYNGOLD BALANCE" else "YOUR ZYNPOINTS BALANCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRealMoney) EmeraldGreen else ElectricGold
                            )
                            Text(
                                text = if (isRealMoney) "${uiState.realCashBalance.toInt()} 🪙 GOLD" else "${uiState.freePointsBalance} PTS",
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
                                text = if (isRealMoney) "🏆 PRO TOURNAMENTS" else "🎮 CASUAL LEADERBOARDS",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isRealMoney) EmeraldGreen else ElectricGold
                            )
                        }
                    }
                }
            }

            // Category Tabs: Team Games, Solo Games, Debate Arena
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
                        text = "👥 Team Games",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if (isTeam) Color(0xFF002026) else TextSecondary,
                        fontSize = 11.sp
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
                        fontSize = 11.sp
                    )
                }

                val isDebate = uiState.selectedCategory == GameCategory.DEBATE_ARENA
                Surface(
                    modifier = Modifier
                        .weight(1.1f)
                        .clickable { onCategorySelected(GameCategory.DEBATE_ARENA) }
                        .testTag("tab_debate_category"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDebate) ElectricGold else Color.Transparent
                ) {
                    Text(
                        text = "🎙️ Debate Arena",
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = if (isDebate) Color(0xFF3B2D00) else TextSecondary,
                        fontSize = 11.sp
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

            // Games / Debates Content List
            if (uiState.selectedCategory == GameCategory.DEBATE_ARENA) {
                DebateArenaHomeSection(
                    topics = uiState.debateTopics,
                    selectedTopic = uiState.selectedDebateTopic,
                    onSelectTopic = {
                        onSelectDebateTopic(it)
                        onGameSelected(GameShowType.DEBATE_SHOWDOWN)
                    },
                    onOpenCustomDialog = onToggleCustomTopicDialog,
                    modifier = Modifier.weight(1f)
                )
            } else {
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
                            onJoin = { onGameSelected(game) },
                            onPlayWithFriends = {
                                onSelectPlayWithFriendsGame(game)
                                onTogglePlayWithFriends(true)
                            }
                        )
                    }
                }
            }
            } // end else branch
        }
    }
}

@Composable
fun DebateArenaHomeSection(
    topics: List<DebateTopic>,
    selectedTopic: DebateTopic,
    onSelectTopic: (DebateTopic) -> Unit,
    onOpenCustomDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎙️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "THE GREAT DEBATE ARENA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = ElectricGold
                            )
                            Text(
                                text = "Crossfire squads, PRO vs. CON clashes & audience live voting",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onOpenCustomDialog,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("➕ Create / Choose Your Own Topic", fontWeight = FontWeight.Bold, color = Color(0xFF002026))
                    }
                }
            }
        }

        item {
            Text(
                text = "🔥 HOT PRESET DEBATE TOPICS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(topics) { topic ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (topic.id == selectedTopic.id) ElectricGold else StadiumCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTopic(topic) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Text(
                                text = topic.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("📊 ${topic.votesPro}% PRO / ${topic.votesCon}% CON", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = topic.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💬 ${topic.arguments.size} arguments posted", fontSize = 10.sp, color = TextMuted)
                        Button(
                            onClick = { onSelectTopic(topic) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Enter Debate ⚔️", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B2D00))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: GameShowType,
    isRealMoney: Boolean,
    onJoin: () -> Unit,
    onPlayWithFriends: () -> Unit = {}
) {
    val prizeString = if (isRealMoney) {
        "${game.realCashPrizePool} 🪙 GOLD POOL"
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onJoin,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .testTag("join_button_${game.name.lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRealMoney) EmeraldGreen else if (game.category == GameCategory.TEAM_GAMES) NeonCyan else NeonPurple,
                        contentColor = if (isRealMoney) Color(0xFF00281F) else if (game.category == GameCategory.TEAM_GAMES) Color(0xFF002026) else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (game.category == GameCategory.TEAM_GAMES) Icons.Default.Groups else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (game.category == GameCategory.TEAM_GAMES) "Auto-Squad" else "Solo Match",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onPlayWithFriends,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp)
                        .testTag("btn_friends_${game.name.lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF26210A),
                        contentColor = ElectricGold
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "👥 With Friends",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = ElectricGold
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// DEDICATED SECTION VIEWS (WITH BACK TO MAIN MENU)
// ----------------------------------------------------

@Composable
fun BackToMenuButton(onBackToMenu: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onBackToMenu,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .testTag("btn_back_to_main_menu")
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = NeonCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("⬅ BACK TO MAIN MENU", fontSize = 12.sp, fontWeight = FontWeight.Black, color = NeonCyan)
    }
}

@Composable
fun TeamGamesSectionView(
    uiState: GameUiState,
    onGameSelected: (GameShowType) -> Unit,
    onTogglePlayWithFriends: (Boolean?) -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "👥 TEAM & SQUAD GAME SHOWS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan
            )
            Text(
                text = "Play together in squads, compete as families, and challenge opponent teams!",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGameSelected(GameShowType.FAMILY_FEUD) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("👨‍👩‍👧‍👦 FAMILY FEUD", fontSize = 15.sp, fontWeight = FontWeight.Black, color = ElectricGold)
                    Text("Survey 100 people, captain face-offs, play or pass, squad huddles & steals!", fontSize = 12.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onGameSelected(GameShowType.FAMILY_FEUD) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("LAUNCH FAMILY FEUD ▶", fontWeight = FontWeight.Black, color = Color(0xFF002026))
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGameSelected(GameShowType.ONE_VS_100) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("👥 1 VS 100 MOB SHOWDOWN", fontSize = 15.sp, fontWeight = FontWeight.Black, color = CrimsonRed)
                    Text("Stand on the podium against a live crowd of 100 players to claim the prize pool!", fontSize = 12.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onGameSelected(GameShowType.ONE_VS_100) },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("PLAY 1 VS 100 ▶", fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }

        item {
            Button(
                onClick = { onTogglePlayWithFriends(true) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F3B4A)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📡 Open Squad Radar & Invite Friends", fontWeight = FontWeight.Bold, color = NeonCyan)
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun SoloGamesSectionView(
    uiState: GameUiState,
    onGameSelected: (GameShowType) -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "⚡ SOLO GAME SHOW EXPERIENCES",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = ElectricGold
            )
            Text(
                text = "Single-player tournament rounds with AI host commentary and instant prizes!",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        val soloGames: List<Pair<GameShowType, Pair<String, String>>> = listOf(
            GameShowType.THE_PRICE_IS_RIGHT to ("🏷️ THE PRICE IS RIGHT" to "Guess retail prices with exact precision"),
            GameShowType.WHEEL_OF_FORTUNE to ("🎡 WHEEL OF FORTUNE" to "Spin the puzzle wheel and guess mystery letters"),
            GameShowType.JEOPARDY to ("🧠 JEOPARDY CLUES" to "Test your intellect across high-value categories"),
            GameShowType.ONE_VS_100 to ("⚡ 1 VS 100 SOLO RUN" to "Take on 100 mob players on the solo hot seat")
        )

        items(soloGames) { (gameType, info) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGameSelected(gameType) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(info.first, fontSize = 13.sp, fontWeight = FontWeight.Black, color = ElectricGold)
                        Text(info.second, fontSize = 11.sp, color = TextSecondary)
                    }
                    Button(
                        onClick = { onGameSelected(gameType) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PLAY ▶", fontWeight = FontWeight.Black, color = Color(0xFF3B2D00))
                    }
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun DebateSectionView(
    uiState: GameUiState,
    onSelectDebateTopic: (DebateTopic) -> Unit,
    onToggleCustomTopicDialog: () -> Unit,
    onGameSelected: (GameShowType) -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "🎙️ DEBATE SHOWDOWN ARENA",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = NeonPurple
            )
            Text(
                text = "Engage in structured debates with AI host scoring and real-time community voting!",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        item {
            Button(
                onClick = onToggleCustomTopicDialog,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✨ Create Custom Debate Topic", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        items(uiState.debateTopics) { topic ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectDebateTopic(topic)
                        onGameSelected(GameShowType.DEBATE_SHOWDOWN)
                    }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NeonPurple.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = topic.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPurple,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${topic.votesPro}% PRO / ${topic.votesCon}% CON",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun LeaderboardsSectionView(
    uiState: GameUiState,
    onBackToMenu: () -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }

    val entries = if (tabIndex == 1) {
        PreloadedGameData.pointsLeaderboard
    } else {
        PreloadedGameData.realCashLeaderboard
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "🏆 LIVE ARENA LEADERBOARDS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = ElectricGold
            )
            Text(
                text = "Top competitors across daily, weekly, and all-time tournament brackets!",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ZynGold (🪙)", "ZynPoints (PTS)", "All-Time").forEachIndexed { idx, label ->
                    val isSel = tabIndex == idx
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) ElectricGold else StadiumSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { tabIndex = idx }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color(0xFF3B2D00) else TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }

        items(entries) { entry ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (entry.isUser) Color(0xFF1B3B2B) else StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (entry.rank == 1) ElectricGold else if (entry.rank == 2) Color(0xFFC0C0C0) else Color(0xFF263345)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${entry.rank}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (entry.rank == 1) ElectricGold else TextPrimary,
                        modifier = Modifier.width(36.dp)
                    )
                    Text(
                        text = entry.avatarEmoji,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.playerName,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${entry.squadName} • ${entry.winsCount} Wins",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = entry.totalEarnings,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricGold
                    )
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun DailyChallengesSectionView(
    uiState: GameUiState,
    onClaimChallenge: (DailyChallengeItem) -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "🎯 DAILY ARENA CHALLENGES",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldGreen
            )
            Text(
                text = "Complete daily objectives to earn bonus points, ZynGold, and exclusive badges!",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        items(PreloadedGameData.dailyChallenges) { chal ->
            val isCompleted = uiState.completedChallengeIds.contains(chal.id) || chal.isCompleted
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCompleted) EmeraldGreen else Color(0xFF263345)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(chal.title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(chal.description, fontSize = 10.5.sp, color = TextSecondary)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ElectricGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = chal.reward,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = ElectricGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { chal.progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = EmeraldGreen,
                            trackColor = Color(0xFF263345)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = chal.progressText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isCompleted) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldGreen
                            ) {
                                Text(
                                    text = "CLAIMED ✓",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00281F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = { onClaimChallenge(chal) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("COMPLETE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00281F))
                            }
                        }
                    }
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun DailySpinSectionView(
    uiState: GameUiState,
    onSpinWheel: () -> Unit,
    onBackToMenu: () -> Unit
) {
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 2000),
        label = "wheel_spin"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "🎡 ARENA LUCKY SPIN WHEEL",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = ElectricGold
            )
            Text(
                text = "100% Free Spins • Zero Ads • Huge ZynGold & Diamond Chest Prizes!",
                fontSize = 11.5.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Animated Wheel Visual
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF3B2D00), Color(0xFF1E1500), StadiumDarkBg)
                        )
                    )
                    .border(4.dp, ElectricGold, CircleShape)
                    .rotate(animatedRotation),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💰 💎 🎁 🪙 🌟 💵",
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Recent Spin Banner
        item {
            if (uiState.latestDailySpinReward != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldGreen.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 CONGRATULATIONS!", fontSize = 13.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
                        Text("You won: ${uiState.latestDailySpinReward.label}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = ElectricGold)
                        Text("Prize has been credited to your balance instantly!", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Spin Button
        item {
            Button(
                onClick = {
                    rotationAngle += 1440f + (0..360).random()
                    onSpinWheel()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_spin_daily_wheel")
            ) {
                Text("🎰 SPIN THE WHEEL NOW (FREE)", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF3B2D00))
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun RewardShopSectionView(
    uiState: GameUiState,
    onBuyItem: (ShopItem) -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "🛍️ ARENA REWARD SHOP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = ElectricGold
            )
            Text(
                text = "Redeem your coins and balance for game boosts, VIP badges, and avatar skins!",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        items(PreloadedGameData.shopItems) { item ->
            val isPurchased = uiState.purchasedShopItemIds.contains(item.id) || item.isPurchased
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isPurchased) EmeraldGreen else Color(0xFF263345)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(item.description, fontSize = 10.5.sp, color = TextSecondary)
                    }
                    if (isPurchased) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                        ) {
                            Text(
                                text = "OWNED ✓",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onBuyItem(item) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                if (item.priceCash != null) "${item.priceCash.toInt()} 🪙" else "${item.pricePoints} PTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF3B2D00)
                            )
                        }
                    }
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun AchievementsSectionView(
    uiState: GameUiState,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "🎖️ ARENA TROPHIES & ACHIEVEMENTS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan
            )
            Text(
                text = "Unlock hall-of-fame accolades and showcase your game show expertise!",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        items(PreloadedGameData.achievements) { ach ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (ach.isUnlocked) ElectricGold else Color(0xFF263345)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ach.iconEmoji, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ach.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (ach.isUnlocked) ElectricGold else TextPrimary)
                        Text(ach.description, fontSize = 10.5.sp, color = TextSecondary)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (ach.isUnlocked) ElectricGold else StadiumSurfaceVariant
                    ) {
                        Text(
                            text = if (ach.isUnlocked) "UNLOCKED ✓" else "LOCKED 🔒",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ach.isUnlocked) Color(0xFF3B2D00) else TextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun GameSettingsSectionView(
    uiState: GameUiState,
    onInstantQuit: () -> Unit,
    onInstantLogOut: () -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "⚙️ GAME SETTINGS & PREFERENCES",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = ElectricGold
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StadiumSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🔊 Audio & Broadcast", fontWeight = FontWeight.Bold, color = NeonCyan)
                    Text("• Mature TV Broadcast Host Voice: Enabled", fontSize = 11.5.sp, color = TextPrimary)
                    Text("• Live Crowd FX & Buzzers: Active", fontSize = 11.5.sp, color = TextPrimary)
                    Text("• Audio Question Playback: Available across all games", fontSize = 11.5.sp, color = TextPrimary)
                }
            }
        }

        // Instant Quit and Log Out Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onInstantLogOut,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E1F1F)),
                    modifier = Modifier.fillMaxWidth().testTag("btn_instant_logout")
                ) {
                    Text("🔒 Instant Log Out (No Confirmation)", fontWeight = FontWeight.Bold, color = Color(0xFFFFB4AB))
                }

                Button(
                    onClick = onInstantQuit,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    modifier = Modifier.fillMaxWidth().testTag("btn_instant_quit")
                ) {
                    Text("🚪 Instant Quit Game (No Confirmation)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun ProfileSectionView(
    uiState: GameUiState,
    onInstantLogOut: () -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StadiumSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👑", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("The VIP Contestant", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ElectricGold)
                    Text("VIP Arena Player • Level 14", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪙 Coins", fontSize = 10.sp, color = TextSecondary)
                            Text("${uiState.freePointsBalance}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ElectricGold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👑 ZynGold", fontSize = 10.sp, color = TextSecondary)
                            Text("${uiState.realCashBalance.toInt()} 🪙", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆 Wins", fontSize = 10.sp, color = TextSecondary)
                            Text("28", fontSize = 14.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onInstantLogOut,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E1F1F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔒 Instant Log Out", fontWeight = FontWeight.Bold, color = Color(0xFFFFB4AB))
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}

@Composable
fun HelpRulesSectionView(
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { BackToMenuButton(onBackToMenu) }

        item {
            Text(
                text = "❓ GAME SHOW OFFICIAL RULES",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan
            )
        }

        val rules = listOf(
            "👨‍👩‍👧‍👦 Family Feud" to "Survey 100 people. Captains face off at the podium. Winner can PLAY or PASS. Squad huddles offer ideas. 3 strikes and the opposing team can STEAL the entire bank!",
            "🏷️ The Price Is Right" to "Bid as close to the actual retail price without going over. Highest valid bid wins the prize!",
            "🎡 Wheel of Fortune" to "Spin the wheel for points or cash. Guess consonants and buy vowels to solve the hidden phrase.",
            "🧠 Jeopardy" to "High stakes trivia with varying clue point values. Buzz in and state your answer accurately.",
            "👥 1 VS 100" to "One contestant takes on 100 mob members. Eliminate all 100 to take home the massive jackpot.",
            "🎙️ Debate Showdown" to "Engage in timed pro/con motions. AI host rates logic, persuasion, and clarity while live crowd votes."
        )

        items(rules) { (title, desc) ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StadiumSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(desc, fontSize = 11.sp, color = TextPrimary)
                }
            }
        }

        item { BackToMenuButton(onBackToMenu) }
    }
}
