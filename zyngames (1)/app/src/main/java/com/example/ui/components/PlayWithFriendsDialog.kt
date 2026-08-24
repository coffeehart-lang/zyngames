package com.example.ui.components

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FriendPlayer
import com.example.data.FriendStatus
import com.example.data.GameShowType
import com.example.ui.GameUiState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayWithFriendsDialog(
    uiState: GameUiState,
    onDismiss: () -> Unit,
    onSetTab: (String) -> Unit,
    onScanNearby: () -> Unit,
    onToggleInvite: (FriendPlayer) -> Unit,
    onSetPartyCode: (String) -> Unit,
    onJoinPartyCode: (String) -> Unit,
    onSelectGame: (GameShowType) -> Unit,
    onStartGameWithSquad: (GameShowType) -> Unit,
    onClearSquad: () -> Unit
) {
    var partyCodeInput by remember { mutableStateOf(uiState.enteredPartyCode) }
    val isScanning = uiState.isScanningNearbyRadar

    // Radar pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radarScale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = NeonCyan.copy(alpha = 0.2f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎮", fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PLAY WITH FRIENDS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Nearby radar or online party rooms",
                        fontSize = 11.sp,
                        color = ElectricGold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Navigation Tabs: NEARBY, ONLINE, PARTY_ROOM
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StadiumSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(4.dp)
                ) {
                    // Nearby Radar Tab
                    val isNearby = uiState.playWithFriendsTab == "NEARBY"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSetTab("NEARBY") }
                            .testTag("tab_friends_nearby"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isNearby) NeonCyan else Color.Transparent
                    ) {
                        Text(
                            text = "📡 Nearby",
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = if (isNearby) Color(0xFF002026) else TextSecondary
                        )
                    }

                    // Online Friends Tab
                    val isOnline = uiState.playWithFriendsTab == "ONLINE"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSetTab("ONLINE") }
                            .testTag("tab_friends_online"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isOnline) EmeraldGreen else Color.Transparent
                    ) {
                        Text(
                            text = "🌐 Online",
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = if (isOnline) Color(0xFF00281F) else TextSecondary
                        )
                    }

                    // Party Code Tab
                    val isCode = uiState.playWithFriendsTab == "PARTY_CODE"
                    Surface(
                        modifier = Modifier
                            .weight(1.1f)
                            .clickable { onSetTab("PARTY_CODE") }
                            .testTag("tab_friends_party_code"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCode) ElectricGold else Color.Transparent
                    ) {
                        Text(
                            text = "🔑 Party Code",
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = if (isCode) Color(0xFF3B2D00) else TextSecondary
                        )
                    }
                }

                // Tab Content
                when (uiState.playWithFriendsTab) {
                    "NEARBY" -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Radar Scanner Action Button
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0B2138),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onScanNearby() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .scale(if (isScanning) pulseScale else 1f)
                                            .background(NeonCyan.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("📡", fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isScanning) "SCANNING NEARBY RADAR..." else "NEARBY DEVICE RADAR (Active)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                        Text(
                                            text = "Auto-detects phones & tablets around you via Wi-Fi/Bluetooth",
                                            fontSize = 9.5.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Button(
                                        onClick = onScanNearby,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp).testTag("btn_scan_nearby_radar")
                                    ) {
                                        Text("Scan", fontSize = 11.sp, color = Color(0xFF002026), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("DISCOVERED FRIENDS AROUND YOU:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.heightIn(max = 180.dp)
                            ) {
                                items(uiState.nearbyFriends) { friend ->
                                    val inSquad = uiState.partyMembers.any { it.id == friend.id }
                                    FriendCardRow(
                                        friend = friend,
                                        isInSquad = inSquad,
                                        onToggleInvite = { onToggleInvite(friend) }
                                    )
                                }
                            }
                        }
                    }

                    "ONLINE" -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("YOUR ONLINE SQUADMATES:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            Spacer(modifier = Modifier.height(4.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.heightIn(max = 220.dp)
                            ) {
                                items(uiState.onlineFriends) { friend ->
                                    val inSquad = uiState.partyMembers.any { it.id == friend.id }
                                    FriendCardRow(
                                        friend = friend,
                                        isInSquad = inSquad,
                                        onToggleInvite = { onToggleInvite(friend) }
                                    )
                                }
                            }
                        }
                    }

                    "PARTY_CODE" -> {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Host Party Code Box
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF26210A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("YOUR HOST PARTY CODE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricGold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = uiState.generatedPartyCode,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 2.sp,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = ElectricGold,
                                            modifier = Modifier.clickable {
                                                // Simulated copy
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF3B2D00), modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Share PIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3B2D00))
                                            }
                                        }
                                    }
                                    Text("Tell your friends to enter this PIN to join your lobby instantly!", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            // Join With Code Section
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = StadiumSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("JOIN A FRIEND'S SQUAD ROOM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = partyCodeInput,
                                            onValueChange = {
                                                partyCodeInput = it.uppercase()
                                                onSetPartyCode(it.uppercase())
                                            },
                                            placeholder = { Text("e.g. ZYNA-8842", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f).height(46.dp).testTag("input_friend_party_code"),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Button(
                                            onClick = { onJoinPartyCode(partyCodeInput) },
                                            enabled = partyCodeInput.isNotBlank(),
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(46.dp).testTag("btn_join_friend_room")
                                        ) {
                                            Text("Join 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF002026))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(color = StadiumCardBorder)

                // Current Squad Summary Tray
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ASSEMBLED SQUAD (${uiState.partyMembers.size + 1}/5):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricGold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (uiState.partyMembers.isNotEmpty()) {
                            Text(
                                text = "Reset Squad",
                                fontSize = 10.sp,
                                color = CrimsonRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onClearSquad() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            SquadMemberChip(name = "You (Leader)", emoji = "😎", isLeader = true)
                        }
                        items(uiState.partyMembers) { friend ->
                            SquadMemberChip(name = friend.name, emoji = friend.avatarEmoji, isLeader = false)
                        }
                        if (uiState.partyMembers.size < 4) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF131828),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+ Open Slot",
                                        fontSize = 10.sp,
                                        color = TextMuted,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Game Selection Picker
                    Text("SELECT SHOWDOWN GAME:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(GameShowType.values().toList()) { g ->
                            val isChosen = uiState.playWithFriendsSelectedGame == g
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isChosen) NeonCyan else StadiumSurfaceVariant,
                                border = if (isChosen) null else androidx.compose.foundation.BorderStroke(1.dp, StadiumCardBorder),
                                modifier = Modifier.clickable { onSelectGame(g) }.testTag("pick_friend_game_${g.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(g.icon, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = g.title,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChosen) Color(0xFF002026) else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onStartGameWithSquad(uiState.playWithFriendsSelectedGame) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("btn_start_game_with_friends")
            ) {
                Text(
                    text = "🚀 START ${uiState.playWithFriendsSelectedGame.title.uppercase()} WITH SQUAD",
                    color = Color(0xFF3B2D00),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {},
        containerColor = StadiumDarkBg,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun FriendCardRow(
    friend: FriendPlayer,
    isInSquad: Boolean,
    onToggleInvite: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isInSquad) Color(0xFF26210A) else StadiumSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isInSquad) ElectricGold else StadiumCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = StadiumSurfaceVariant,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(friend.avatarEmoji, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = friend.name,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (friend.proximityDistance != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• ${friend.proximityDistance}",
                            fontSize = 9.5.sp,
                            color = NeonCyan
                        )
                    }
                }
                Text(
                    text = "Win Rate: ${friend.winRatePercent}% • ${friend.gamesPlayedTogether} games played together",
                    fontSize = 9.5.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = onToggleInvite,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInSquad) ElectricGold else Color(0xFF003642)
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(28.dp).testTag("btn_invite_friend_${friend.id}")
            ) {
                Text(
                    text = if (isInSquad) "✓ In Squad" else "+ Add",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isInSquad) Color(0xFF3B2D00) else NeonCyan
                )
            }
        }
    }
}

@Composable
fun SquadMemberChip(
    name: String,
    emoji: String,
    isLeader: Boolean
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isLeader) Color(0xFF0F2B36) else Color(0xFF26210A),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLeader) NeonCyan else ElectricGold
        ),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLeader) NeonCyan else ElectricGold
            )
        }
    }
}
