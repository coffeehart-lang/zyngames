package com.example.data

enum class EconomyMode(val label: String, val badge: String, val description: String) {
    REAL_CASH(
        label = "👑 Championship Arena",
        badge = "ZYNGOLD LEAGUE",
        description = "Compete for massive championship prize pools and tournament trophies."
    ),
    FREE_PLAY(
        label = "🪙 Free Play & Points",
        badge = "PLAY POINTS (FREE)",
        description = "Casual competitive games with virtual ZynPoints and squad rank points."
    )
}

enum class GameCategory(val title: String) {
    TEAM_GAMES(title = "👥 Team Games"),
    SOLO_GAMES(title = "⚡ Solo Games"),
    DEBATE_ARENA(title = "🎙️ Debate Arena")
}

enum class GameShowType(
    val title: String,
    val subtitle: String,
    val category: GameCategory,
    val icon: String,
    val description: String,
    val realCashPrizePool: Int,
    val freePointsPrizePool: Int
) {
    // 1. Team Game: Family Feud
    FAMILY_FEUD(
        title = "Family Feud",
        subtitle = "Survey Question Showdown",
        category = GameCategory.TEAM_GAMES,
        icon = "📊",
        description = "Random online families guess the top survey answers. 3 strikes and the opposing team can steal!",
        realCashPrizePool = 25000,
        freePointsPrizePool = 50000
    ),

    // 2. Team Game: The Price Is Right
    THE_PRICE_IS_RIGHT(
        title = "The Price Is Right",
        subtitle = "Showcase Pricing & Drop Zone",
        category = GameCategory.TEAM_GAMES,
        icon = "🏷️",
        description = "Guess the actual retail price without going over. Your team's hivemind consensus locks in the official bid!",
        realCashPrizePool = 50000,
        freePointsPrizePool = 100000
    ),

    // 3. Team Game: Wheel of Fortune
    WHEEL_OF_FORTUNE(
        title = "Wheel of Fortune",
        subtitle = "Spin, Guess Letters & Solve",
        category = GameCategory.TEAM_GAMES,
        icon = "🎡",
        description = "Spin the giant multiplier wheel, buy vowels, call consonants, and solve the hidden phrase board.",
        realCashPrizePool = 35000,
        freePointsPrizePool = 75000
    ),

    // 4. Solo Game: Jeopardy!
    JEOPARDY(
        title = "Jeopardy!",
        subtitle = "High-Stakes Category Trivia",
        category = GameCategory.SOLO_GAMES,
        icon = "⚡",
        description = "Solo buzzer action across 200–1,000 point category clues, Daily Doubles, and Final Jeopardy wagers.",
        realCashPrizePool = 100000,
        freePointsPrizePool = 200000
    ),

    // 5. Solo Game: 1 vs. 100
    ONE_VS_100(
        title = "1 vs. 100",
        subtitle = "Solo vs. The Online Mob",
        category = GameCategory.SOLO_GAMES,
        icon = "🧠",
        description = "One player on the hot seat against 100 mob members. Eliminate the mob to take home the whole prize pool!",
        realCashPrizePool = 150000,
        freePointsPrizePool = 300000
    ),

    // 6. Debate Showdown
    DEBATE_SHOWDOWN(
        title = "The Great Debate Arena",
        subtitle = "Live Crossfire & Topic Clashes",
        category = GameCategory.DEBATE_ARENA,
        icon = "🎙️",
        description = "Choose preset topics or debate your own! PRO vs. CON squads clash with live host moderation & crowd voting.",
        realCashPrizePool = 50000,
        freePointsPrizePool = 100000
    )
}

data class Player(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val isCurrentUser: Boolean = false,
    val isCaptain: Boolean = false,
    val score: Int = 0
)

data class Team(
    val id: String,
    val name: String,
    val colorHex: Long,
    val members: List<Player>,
    val totalScore: Int = 0,
    val strikes: Int = 0
)

data class TeamChatMessage(
    val id: String,
    val teamId: String = "team_my",
    val senderName: String,
    val senderEmoji: String,
    val message: String,
    val timestamp: String,
    val isCurrentUser: Boolean = false,
    val isSuggestion: Boolean = false
)

data class FeudAnswer(
    val text: String,
    val points: Int,
    val isRevealed: Boolean = false,
    val rank: Int
)

data class PreloadedFeudRound(
    val id: String,
    val question: String,
    val answers: List<FeudAnswer>,
    val hostSecretNotes: String
)

data class PreloadedPriceItem(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val actualRetailPrice: Int,
    val hostSecretNotes: String
)

data class PreloadedJeopardyClue(
    val id: String,
    val category: String,
    val value: Int,
    val clue: String,
    val secretAnswer: String,
    val options: List<String>,
    val correctIndex: Int,
    val hostSecretNotes: String,
    val isAnswered: Boolean = false
)

data class PreloadedWheelPuzzle(
    val id: String,
    val category: String,
    val secretPhrase: String,
    val hostSecretNotes: String
)

data class PreloadedMobQuestion(
    val id: String,
    val tier: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val secretExplanation: String
)

// Debate Arena Models
data class DebateArgument(
    val id: String,
    val authorName: String,
    val authorEmoji: String,
    val isPro: Boolean,
    val text: String,
    val likes: Int = 0,
    val timestamp: String = "Just now"
)

data class DebateTopic(
    val id: String,
    val title: String,
    val category: String, // Tech, Pop Culture, Food, Philosophy, Sports, Work
    val description: String,
    val proStance: String,
    val conStance: String,
    val isCustom: Boolean = false,
    val votesPro: Int = 50,
    val votesCon: Int = 50,
    val arguments: List<DebateArgument> = emptyList(),
    val moderatorNotes: String = ""
)

enum class DebateRoundStage(val title: String, val seconds: Int) {
    OPENING_PRO(title = "Stage 1: PRO Opening Arguments", seconds = 45),
    OPENING_CON(title = "Stage 2: CON Opening Arguments", seconds = 45),
    CROSSFIRE(title = "Stage 3: Squad Crossfire & Rebuttals", seconds = 60),
    CLOSING_VOTE(title = "Stage 4: Audience Live Verdict & Polls", seconds = 30)
}

// Feud Interactive Stage Models
data class FeudLiveSpeech(
    val speakerName: String,
    val speakerEmoji: String,
    val isOpponent: Boolean,
    val speechText: String,
    val isOfficialGuess: Boolean = false,
    val resultBanner: String? = null
)

// Friends and Multiplayer Models
enum class FriendStatus(val label: String, val badgeColorHex: Long) {
    NEARBY_RADAR(label = "Nearby (Bluetooth / Wi-Fi)", badgeColorHex = 0xFF00E5FF),
    ONLINE_AVAILABLE(label = "Online & Ready", badgeColorHex = 0xFF00E676),
    IN_PARTY(label = "In Your Squad", badgeColorHex = 0xFFFFD700),
    OFFLINE(label = "Offline", badgeColorHex = 0xFF757575)
}

data class FriendPlayer(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val status: FriendStatus,
    val proximityDistance: String? = null,
    val isInvited: Boolean = false,
    val gamesPlayedTogether: Int = 0,
    val winRatePercent: Int = 50
)

data class FriendPartyRoom(
    val roomCode: String,
    val roomName: String,
    val hostPlayerName: String,
    val targetGame: GameShowType,
    val members: List<FriendPlayer>,
    val maxCapacity: Int = 5,
    val isNearbyBroadcast: Boolean = false
)

enum class AppSection(val title: String, val icon: String) {
    MAIN_MENU(title = "Main Menu", icon = "🏠"),
    TEAM_GAMES(title = "Team Games", icon = "👥"),
    SOLO_GAMES(title = "Solo Games", icon = "⚡"),
    DEBATE_ARENA(title = "Debate Arena", icon = "🎙️"),
    LEADERBOARDS(title = "Leaderboards", icon = "🏆"),
    DAILY_CHALLENGES(title = "Daily Challenges", icon = "🎯"),
    GAME_SETTINGS(title = "Game Settings", icon = "🎮"),
    SETTINGS(title = "Settings", icon = "⚙️"),
    PROFILE(title = "Player Profile", icon = "👤"),
    HELP(title = "Help & Rules", icon = "❓"),
    SHOP(title = "Reward Shop", icon = "🛍️"),
    ACHIEVEMENTS(title = "Achievements", icon = "🎖️"),
    DAILY_SPIN(title = "Daily Lucky Spin", icon = "🎡")
}

data class LeaderboardEntry(
    val rank: Int,
    val playerName: String,
    val avatarEmoji: String,
    val squadName: String,
    val totalEarnings: String,
    val winsCount: Int,
    val isUser: Boolean = false
)

data class DailyChallengeItem(
    val id: String,
    val title: String,
    val description: String,
    val reward: String,
    val progress: Float, // 0.0 to 1.0
    val progressText: String,
    val isCompleted: Boolean = false
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val rewardPoints: Int,
    val isUnlocked: Boolean = false
)

data class ShopItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconEmoji: String,
    val priceCash: Double?,
    val pricePoints: Int?,
    val isPurchased: Boolean = false
)

data class SpinReward(
    val id: String,
    val label: String,
    val valueCash: Double,
    val valuePoints: Int,
    val iconEmoji: String,
    val colorHex: Long
)


