package com.example.data

enum class EconomyMode(val label: String, val badge: String, val description: String) {
    REAL_CASH(
        label = "💰 Real Cash Arena",
        badge = "REAL MONEY (\$USD)",
        description = "Compete for guaranteed cash prize pools with verified instant payouts."
    ),
    FREE_PLAY(
        label = "🪙 Free Play & Points",
        badge = "PLAY POINTS (FREE)",
        description = "Casual competitive games with virtual ZynPoints and squad rank points."
    )
}

enum class GameCategory(val title: String) {
    TEAM_GAMES(title = "👥 Team Games"),
    SOLO_GAMES(title = "⚡ Solo Games")
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
        realCashPrizePool = 2500,
        freePointsPrizePool = 50000
    ),

    // 2. Team Game: The Price Is Right
    THE_PRICE_IS_RIGHT(
        title = "The Price Is Right",
        subtitle = "Showcase Pricing & Drop Zone",
        category = GameCategory.TEAM_GAMES,
        icon = "🏷️",
        description = "Guess the actual retail price without going over. Your team's hivemind consensus locks in the official bid!",
        realCashPrizePool = 5000,
        freePointsPrizePool = 100000
    ),

    // 3. Team Game: Wheel of Fortune
    WHEEL_OF_FORTUNE(
        title = "Wheel of Fortune",
        subtitle = "Spin, Guess Letters & Solve",
        category = GameCategory.TEAM_GAMES,
        icon = "🎡",
        description = "Spin the giant multiplier wheel, buy vowels, call consonants, and solve the hidden phrase board.",
        realCashPrizePool = 3500,
        freePointsPrizePool = 75000
    ),

    // 4. Solo Game: Jeopardy!
    JEOPARDY(
        title = "Jeopardy!",
        subtitle = "High-Stakes Category Trivia",
        category = GameCategory.SOLO_GAMES,
        icon = "⚡",
        description = "Solo buzzer action across $200–$1,000 category clues, Daily Doubles, and Final Jeopardy wagers.",
        realCashPrizePool = 10000,
        freePointsPrizePool = 200000
    ),

    // 5. Solo Game: 1 vs. 100
    ONE_VS_100(
        title = "1 vs. 100",
        subtitle = "Solo vs. The Online Mob",
        category = GameCategory.SOLO_GAMES,
        icon = "🧠",
        description = "One player on the hot seat against 100 mob members. Eliminate the mob to take home the whole prize pool!",
        realCashPrizePool = 15000,
        freePointsPrizePool = 300000
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
