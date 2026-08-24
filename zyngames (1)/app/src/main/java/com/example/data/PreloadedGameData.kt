package com.example.data

object PreloadedGameData {

    // 1. Preloaded Family Feud Rounds with Host Secrets
    val feudRounds = listOf(
        PreloadedFeudRound(
            id = "ff_1",
            question = "Name something people always forget to pack when going on vacation.",
            answers = listOf(
                FeudAnswer("Toothbrush / Toiletries", 38, false, 1),
                FeudAnswer("Phone Charger / Cables", 29, false, 2),
                FeudAnswer("Sunscreen / Sunglasses", 15, false, 3),
                FeudAnswer("Underwear / Extra Socks", 11, false, 4),
                FeudAnswer("Passport / ID", 7, false, 5)
            ),
            hostSecretNotes = "HOST CUE: Toothbrush is the top answer (#1, 38 pts). If Team A misses 3, Team B can steal all accumulated bank points with any remaining top answer."
        ),
        PreloadedFeudRound(
            id = "ff_2",
            question = "Name a reason someone might be late to work.",
            answers = listOf(
                FeudAnswer("Traffic Jam", 44, false, 1),
                FeudAnswer("Overslept / Alarm Didn't Go Off", 31, false, 2),
                FeudAnswer("Car Trouble / Flat Tire", 13, false, 3),
                FeudAnswer("Kids / School Drop-off", 8, false, 4),
                FeudAnswer("Spilled Coffee / Wardrobe Malfunction", 4, false, 5)
            ),
            hostSecretNotes = "HOST CUE: 'Traffic' is the overwhelming #1 (44 pts). Watch out for answers like 'Transit delay'—judge can accept or reject."
        ),
        PreloadedFeudRound(
            id = "ff_3",
            question = "Name something you would find in a superhero's secret lair.",
            answers = listOf(
                FeudAnswer("Super Computer / Monitors", 42, false, 1),
                FeudAnswer("Costumes / Super Suits", 28, false, 2),
                FeudAnswer("High-Tech Vehicles / Batmobile", 16, false, 3),
                FeudAnswer("Weapons / Gadgets Arsenal", 9, false, 4),
                FeudAnswer("Secret Escape Tunnel", 5, false, 5)
            ),
            hostSecretNotes = "HOST CUE: 'Super Computer' is top at 42 pts. Great fast-money style round!"
        ),
        PreloadedFeudRound(
            id = "ff_4",
            question = "Name a food people love to eat at the movie theater.",
            answers = listOf(
                FeudAnswer("Popcorn with Butter", 55, false, 1),
                FeudAnswer("Nachos & Cheese", 19, false, 2),
                FeudAnswer("Candy / Red Vines", 14, false, 3),
                FeudAnswer("Soda / Icee", 8, false, 4),
                FeudAnswer("Hot Dog", 4, false, 5)
            ),
            hostSecretNotes = "HOST CUE: 'Popcorn' dominates with 55 pts. Clear board favorite!"
        )
    )

    // 2. Preloaded The Price Is Right Items with Host Secrets
    val priceItems = listOf(
        PreloadedPriceItem(
            id = "pir_1",
            name = "Vintage 1968 Gibson Les Paul Custom Guitar",
            category = "Collector Music Gear",
            description = "All-original black beauty finish, gold hardware, dual PAF humbuckers, and original hard-shell case.",
            actualRetailPrice = 14850,
            hostSecretNotes = "HOST SECRET: Exact Actual Retail Price is $14,850. Any team guessing $14,851 or higher is BUSTED (over). Closest without going over wins!"
        ),
        PreloadedPriceItem(
            id = "pir_2",
            name = "Fully Restored 1974 Ford Bronco 4x4",
            category = "Classic Vehicles",
            description = "Custom coyote V8 engine swap, diamond stitch leather interior, 35-inch all-terrain tires, and removable hardtop.",
            actualRetailPrice = 89500,
            hostSecretNotes = "HOST SECRET: Exact price is $89,500. Remind teams that bids are averaged across their squad hivemind."
        ),
        PreloadedPriceItem(
            id = "pir_3",
            name = "Luxury 7-Night Private Overwater Villa in Bora Bora",
            category = "Vacation Packages",
            description = "Includes first-class roundtrip airfare, personal butler, private plunge pool, and daily yacht excursion.",
            actualRetailPrice = 22400,
            hostSecretNotes = "HOST SECRET: Exact price is $22,400. Build anticipation before revealing the envelope!"
        ),
        PreloadedPriceItem(
            id = "pir_4",
            name = "Commercial Espresso Machine & Coffee Bar Set",
            category = "Gourmet Kitchen Appliances",
            description = "Dual boiler Italian espresso machine, automated conical grinder, custom walnut portafilters, and 1-year artisan bean supply.",
            actualRetailPrice = 6750,
            hostSecretNotes = "HOST SECRET: Exact price is $6,750."
        )
    )

    // 3. Preloaded Wheel of Fortune Puzzles with Host Secrets
    val wheelPuzzles = listOf(
        PreloadedWheelPuzzle(
            id = "wof_1",
            category = "FAMOUS SAYING & PHRASE",
            secretPhrase = "A ROLLING STONE GATHERS NO MOSS",
            hostSecretNotes = "HOST SECRET PUZZLE: 'A ROLLING STONE GATHERS NO MOSS'. Vowels: A, O, I, E. Consonants: R, L, N, G, S, T, H, M."
        ),
        PreloadedWheelPuzzle(
            id = "wof_2",
            category = "ENTERTAINMENT & POP CULTURE",
            secretPhrase = "LIGHTS CAMERA ACTION ON STAGE",
            hostSecretNotes = "HOST SECRET PUZZLE: 'LIGHTS CAMERA ACTION ON STAGE'. Encourage players to buy vowels when stuck."
        ),
        PreloadedWheelPuzzle(
            id = "wof_3",
            category = "LIVING THINGS & NATURE",
            secretPhrase = "MIGHTY OAK TREES IN THE FOREST",
            hostSecretNotes = "HOST SECRET PUZZLE: 'MIGHTY OAK TREES IN THE FOREST'."
        )
    )

    // 4. Preloaded Jeopardy Clues with Host Secrets
    val jeopardyClues = listOf(
        PreloadedJeopardyClue(
            id = "j_1",
            category = "WORLD HISTORY",
            value = 200,
            clue = "In 1969, this astronaut became the first person to walk on the Moon.",
            secretAnswer = "Who is Neil Armstrong?",
            options = listOf("Neil Armstrong", "Buzz Aldrin", "Yuri Gagarin", "John Glenn"),
            correctIndex = 0,
            hostSecretNotes = "HOST ANSWER: Neil Armstrong (Apollo 11 mission)."
        ),
        PreloadedJeopardyClue(
            id = "j_2",
            category = "SCIENCE & TECH",
            value = 400,
            clue = "This elementary particle carries a negative electric charge.",
            secretAnswer = "What is an Electron?",
            options = listOf("Proton", "Neutron", "Electron", "Photon"),
            correctIndex = 2,
            hostSecretNotes = "HOST ANSWER: Electron. Discovered by J.J. Thomson in 1897."
        ),
        PreloadedJeopardyClue(
            id = "j_3",
            category = "MUSIC LEGENDS",
            value = 600,
            clue = "This legendary artist was nicknamed the 'King of Pop' and released the best-selling album 'Thriller'.",
            secretAnswer = "Who is Michael Jackson?",
            options = listOf("Prince", "Michael Jackson", "Stevie Wonder", "Elton John"),
            correctIndex = 1,
            hostSecretNotes = "HOST ANSWER: Michael Jackson (Released in 1982 by Quincy Jones & Epic)."
        ),
        PreloadedJeopardyClue(
            id = "j_4",
            category = "GEOGRAPHY",
            value = 800,
            clue = "This is the longest river in South America and discharges the largest volume of water in the world.",
            secretAnswer = "What is the Amazon River?",
            options = listOf("Nile River", "Amazon River", "Yangtze River", "Mississippi River"),
            correctIndex = 1,
            hostSecretNotes = "HOST ANSWER: Amazon River."
        ),
        PreloadedJeopardyClue(
            id = "j_5",
            category = "FINAL JEOPARDY",
            value = 1000,
            clue = "This ancient wonder was built in Alexandria, Egypt to guide sailors into the harbor.",
            secretAnswer = "What is the Lighthouse of Alexandria (Pharos)?",
            options = listOf("Colossus of Rhodes", "Lighthouse of Alexandria", "Hanging Gardens", "Temple of Artemis"),
            correctIndex = 1,
            hostSecretNotes = "HOST ANSWER: Lighthouse of Alexandria (Pharos). Players can wager up to their full balance."
        )
    )

    // 5. Preloaded 1 vs. 100 Questions with Host Secrets
    val mobQuestions = listOf(
        PreloadedMobQuestion(
            id = "mob_1",
            tier = 1,
            question = "Which planet in our solar system is known as the 'Red Planet'?",
            options = listOf("Venus", "Mars", "Jupiter", "Saturn"),
            correctIndex = 1,
            secretExplanation = "HOST SECRET: Mars gets its reddish color from iron oxide (rust) on its surface. Expect ~80% of mob to survive."
        ),
        PreloadedMobQuestion(
            id = "mob_2",
            tier = 2,
            question = "How many keys are on a standard acoustic concert grand piano?",
            options = listOf("66 Keys", "76 Keys", "88 Keys", "100 Keys"),
            correctIndex = 2,
            secretExplanation = "HOST SECRET: 88 Keys (52 white natural keys and 36 black accidentals). This should eliminate 30-40 mob players!"
        ),
        PreloadedMobQuestion(
            id = "mob_3",
            tier = 3,
            question = "What is the capital city of Australia?",
            options = listOf("Sydney", "Melbourne", "Canberra", "Brisbane"),
            correctIndex = 2,
            secretExplanation = "HOST SECRET: Canberra (often confused with Sydney or Melbourne). High elimination tier!"
        )
    )

    // 6. Preloaded Debate Topics (Presets for Debate Arena)
    val debateTopics = listOf(
        DebateTopic(
            id = "deb_1",
            title = "Artificial Intelligence will create far more jobs and prosperity than it replaces",
            category = "Tech & Innovation 🤖",
            description = "Will generative AI usher in an era of hyper-abundance and creativity, or create irreversible labor disruptions?",
            proStance = "PRO: AI automates drudgery, creates brand-new industries, and levels the playing field for creators.",
            conStance = "CON: Displacement happens faster than retraining, concentrating wealth in mega-corps and eroding job security.",
            votesPro = 62,
            votesCon = 38,
            moderatorNotes = "HOST CUE: Keep energy high! Remind the PRO squad to cite new economic sectors, and CON to address displacement timelines.",
            arguments = listOf(
                DebateArgument("deb_a1", "Alex (Tech Analyst)", "⚡", true, "Every industrial revolution in history created 10x more jobs than the buggy whips it replaced. AI gives everyone superpowers!"),
                DebateArgument("deb_a2", "Marcus (Economist)", "🛡️", false, "The velocity of AI disruption is exponential—human biology and retraining cycles cannot adapt in a 3-year span!"),
                DebateArgument("deb_a3", "Elena (Startup Founder)", "🚀", true, "Solo founders can now build billion-dollar companies using AI tools. It democratizes entrepreneurship worldwide!")
            )
        ),
        DebateTopic(
            id = "deb_2",
            title = "Pineapple on Pizza is a legitimate culinary masterpiece",
            category = "Food & Culture 🍕",
            description = "The ultimate culinary showdown: Sweet acidity cutting through salty cured ham and cheese vs. traditional pizza heresy.",
            proStance = "PRO: Acidic sweetness cuts rich fat and umami; Hawaiian pizza is globally celebrated for a reason.",
            conStance = "CON: Soggy moisture ruins the crust crispness and sweet fruit clashes with traditional tomato profiles.",
            votesPro = 54,
            votesCon = 46,
            moderatorNotes = "HOST CUE: Have fun with this! Joke about Italian pizza purists fainting, but keep the debate spicy and friendly!",
            arguments = listOf(
                DebateArgument("deb_a4", "Chef Mateo", "👨‍🍳", true, "Sweet and savory pairing is basic culinary science: prosciutto with melon, duck à l'orange, pineapple with salty bacon!"),
                DebateArgument("deb_a5", "Sofia (Napoli Purist)", "🤌", false, "Hot fruit on melted mozzarella is a sensory crime. Keep pizza crisp and savory!"),
                DebateArgument("deb_a6", "Dave (Foodie)", "🔥", true, "Add jalapeños with the pineapple and you get sweet, salty, and spicy perfection!")
            )
        ),
        DebateTopic(
            id = "deb_3",
            title = "Remote work is unequivocally superior to mandatory in-office work",
            category = "Work & Society 💼",
            description = "Commute elimination, flexible lifestyle, and global talent pools vs. spontaneous in-person collaboration and team culture.",
            proStance = "PRO: Zero 2-hour commutes, better family work-life balance, and output-based evaluation over performative presence.",
            conStance = "CON: Mentorship suffers, serendipitous whiteboarding disappears, and isolation degrades long-term company loyalty.",
            votesPro = 71,
            votesCon = 29,
            moderatorNotes = "HOST CUE: Challenge the PRO team on how junior teammates learn without senior osmosis in the hallway!",
            arguments = listOf(
                DebateArgument("deb_a7", "Rachel (Senior Engineer)", "💻", true, "I gained back 12 hours a week of my life not sitting in bumper-to-bumper traffic. My code commits doubled!"),
                DebateArgument("deb_a8", "David (VP of Culture)", "🏢", false, "High-stakes negotiations, hallway brainstorming, and apprentice learning all decay through a 13-inch laptop screen.")
            )
        ),
        DebateTopic(
            id = "deb_4",
            title = "The Marvel Cinematic Universe permanently peaked with Avengers: Endgame",
            category = "Pop Culture & Cinema 🎬",
            description = "A decade-long 22-movie crescendo vs. the expanding multiverse, Disney+ streaming shows, and new character arcs.",
            proStance = "PRO: A once-in-a-generation cinematic payoff that can never be replicated due to superhero fatigue and dilution.",
            conStance = "CON: The multiverse, X-Men integration, and new characters give Marvel limitless creative reinvention.",
            votesPro = 68,
            votesCon = 32,
            moderatorNotes = "HOST CUE: Ask the CON team if Secret Wars or Deadpool & Wolverine can ever top the portals scene in Endgame!",
            arguments = listOf(
                DebateArgument("deb_a9", "Chris (Film Buff)", "🍿", true, "Endgame gave a definitive 10-year emotional climax. Since then, the plotlines feel like homework!"),
                DebateArgument("deb_a10", "Maya (Comics Geek)", "🦸‍♀️", false, "With Fantastic Four and X-Men coming, the biggest storylines haven't even been adapted yet!")
            )
        ),
        DebateTopic(
            id = "deb_5",
            title = "Lionel Messi is the undisputed greatest soccer player in sports history",
            category = "Sports & Athletics ⚽",
            description = "8 Ballon d'Ors, World Cup champion, playmaker genius vs. Cristiano Ronaldo's goalscoring records, Pelé, and Maradona.",
            proStance = "PRO: Complete football genius—highest assists, incredible scoring efficiency, and won every major international trophy.",
            conStance = "CON: Pelé won 3 World Cups, Ronaldo dominated 3 different top European leagues, and Maradona carried underdog Napoli.",
            votesPro = 65,
            votesCon = 35,
            moderatorNotes = "HOST CUE: Remind both sides that this is the clash of the titans! Let both fanbases shine!",
            arguments = listOf(
                DebateArgument("deb_a11", "Lucas (Analyst)", "🏆", true, "Messi creates goals from midfield, dribbles past 5 defenders, and completed football in Qatar 2022."),
                DebateArgument("deb_a12", "CristianoFan7", "⚽", false, "Ronaldo has the all-time international scoring record, 5 Champions Leagues, and conquered England, Spain, and Italy.")
            )
        ),
        DebateTopic(
            id = "deb_6",
            title = "Video games provide greater artistic and storytelling depth than Hollywood movies",
            category = "Gaming & Arts 🎮",
            description = "Interactive 50-hour emotional immersion and branching player agency vs. passive 2-hour auteur cinematic direction.",
            proStance = "PRO: Agency and immersion make narrative impact vastly deeper—players live the story rather than just watch it.",
            conStance = "CON: Pacing is diluted by gameplay loops; cinema's focused brevity allows pure photographic and acting perfection.",
            votesPro = 59,
            votesCon = 41,
            moderatorNotes = "HOST CUE: Bring up games like The Last of Us and Red Dead Redemption 2 as evidence!",
            arguments = listOf(
                DebateArgument("deb_a13", "Jordan (Narrative Designer)", "🎮", true, "When YOU have to make the moral choices, the emotional weight hits 100 times harder than any film."),
                DebateArgument("deb_a14", "Claire (Film Director)", "🎥", false, "A 2-hour masterclass by Kubrick or Nolan is tight, uncompromised artistic vision without 20 hours of filler fetch quests.")
            )
        )
    )

    // Hilarious & Quirky Family Feud Last Names
    val funnyFamilyLastNames = listOf(
        "Wigglebottom",
        "McSillypants",
        "Flabbergast",
        "Dunderhead",
        "Butterfinger",
        "Snickerdoodle",
        "Noodlepuddle",
        "Quackenbush",
        "Bamboozle",
        "Gigglesworth",
        "Pickleheimer",
        "Dingledorf",
        "Cheesygrits",
        "Clumsyfoot",
        "Bananapants",
        "Wobblytoes",
        "Skedaddle",
        "Shenanigan",
        "Haphazard",
        "Pumpernickel",
        "Blunderbuss",
        "Fiddlesticks",
        "Gobbledegook",
        "Featherbrain",
        "Doodlebop",
        "Jellybelly",
        "Poppentickle",
        "Boondoggle",
        "Rumpelstiltskin",
        "Twiddlethumbs"
    )

    // Preloaded Friends Lists for "Play With Friends"
    val nearbyFriendsList = listOf(
        FriendPlayer("f_near_1", "Dave's Galaxy S24", "📱", FriendStatus.NEARBY_RADAR, proximityDistance = "3 ft away (Living Room)", gamesPlayedTogether = 14, winRatePercent = 71),
        FriendPlayer("f_near_2", "Sarah_PixelPro", "✨", FriendStatus.NEARBY_RADAR, proximityDistance = "8 ft away (Kitchen Table)", gamesPlayedTogether = 8, winRatePercent = 63),
        FriendPlayer("f_near_3", "LocalChallenger_Alex", "⚡", FriendStatus.NEARBY_RADAR, proximityDistance = "15 ft away (Nearby Wi-Fi)", gamesPlayedTogether = 2, winRatePercent = 50),
        FriendPlayer("f_near_4", "GamerCousin_Leo", "🎮", FriendStatus.NEARBY_RADAR, proximityDistance = "20 ft away (Hotspot Connected)", gamesPlayedTogether = 21, winRatePercent = 80)
    )

    val onlineFriendsList = listOf(
        FriendPlayer("f_on_1", "TriviaBeast_Max", "🦁", FriendStatus.ONLINE_AVAILABLE, gamesPlayedTogether = 32, winRatePercent = 78),
        FriendPlayer("f_on_2", "GamerGirl_Jess", "🎧", FriendStatus.ONLINE_AVAILABLE, gamesPlayedTogether = 19, winRatePercent = 65),
        FriendPlayer("f_on_3", "CyberSamurai_Ken", "⚔️", FriendStatus.ONLINE_AVAILABLE, gamesPlayedTogether = 9, winRatePercent = 55),
        FriendPlayer("f_on_4", "PuzzleMaster_Mia", "🧩", FriendStatus.ONLINE_AVAILABLE, gamesPlayedTogether = 24, winRatePercent = 83),
        FriendPlayer("f_on_5", "SpeedyBuzzer_Dan", "🔥", FriendStatus.ONLINE_AVAILABLE, gamesPlayedTogether = 11, winRatePercent = 45),
        FriendPlayer("f_on_6", "DebateChamp_Chloe", "🎙️", FriendStatus.ONLINE_AVAILABLE, gamesPlayedTogether = 15, winRatePercent = 73)
    )

    // Leaderboards Data
    val realCashLeaderboard = listOf(
        LeaderboardEntry(1, "JackpotKing_Rick", "👑", "Team Volt Blue", "48,950 🪙", 312),
        LeaderboardEntry(2, "QueenOfFeud_Sarah", "💎", "The Wigglebottoms", "36,400 🪙", 245),
        LeaderboardEntry(3, "BuzzerGod_Alex", "⚡", "Team Gold Pulse", "29,120 🪙", 198),
        LeaderboardEntry(4, "PriceHunter_Sam", "🎯", "The McSillypants", "21,800 🪙", 164),
        LeaderboardEntry(5, "DebateMaster_Chloe", "🎙️", "Team Emerald Synths", "18,450 🪙", 142),
        LeaderboardEntry(6, "You (Player)", "😎", "The Wigglebottoms", "1,450 🪙", 18, isUser = true)
    )

    val pointsLeaderboard = listOf(
        LeaderboardEntry(1, "RetroGamer_99", "👾", "Team Volt Blue", "2,840,000 PTS", 480),
        LeaderboardEntry(2, "TriviaValkyrie", "🛡️", "The Snickerdoodles", "1,950,000 PTS", 350),
        LeaderboardEntry(3, "LuckyWheel_Dan", "🎡", "Team Gold Pulse", "1,420,000 PTS", 290),
        LeaderboardEntry(4, "SpeedyBrain_Mia", "🧠", "The Bamboozles", "980,000 PTS", 210),
        LeaderboardEntry(5, "You (Player)", "😎", "The Wigglebottoms", "42,500 PTS", 18, isUser = true)
    )

    // Daily Challenges
    val dailyChallenges = listOf(
        DailyChallengeItem("dc_1", "Face-Off Champion", "Win 3 Face-Off buzzers in Family Feud", "150 🪙 & 5,000 PTS", 1.0f, "3/3 Completed", isCompleted = true),
        DailyChallengeItem("dc_2", "Price Guru", "Guess within 5% of actual price in The Price Is Right", "200 🪙 & 10,000 PTS", 0.7f, "2/3 Items", isCompleted = false),
        DailyChallengeItem("dc_3", "Wheel Master", "Spin and solve at least 2 hidden phrase puzzles", "350 🪙 & 15,000 PTS", 0.5f, "1/2 Puzzles", isCompleted = false),
        DailyChallengeItem("dc_4", "Debate Orator", "Submit 5 winning arguments in the Great Debate Arena", "500 🪙 & 25,000 PTS", 0.8f, "4/5 Arguments", isCompleted = false),
        DailyChallengeItem("dc_5", "Mob Survivor", "Eliminate at least 50 mob members in 1 vs 100", "1,000 🪙 & 50,000 PTS", 0.6f, "30/50 Mob", isCompleted = false)
    )

    // Achievements
    val achievements = listOf(
        AchievementItem("ach_1", "First Blood Buzzer", "Buzz in first during any Family Feud Face-Off", "⚡", 2500, isUnlocked = true),
        AchievementItem("ach_2", "Grand Steal", "Steal the entire bank with 3 strikes on the opposing family", "🚨", 5000, isUnlocked = true),
        AchievementItem("ach_3", "Perfect Showcase", "Guess the exact dollar value in Price Is Right", "🏆", 10000, isUnlocked = true),
        AchievementItem("ach_4", "Million Dollar Wheel", "Spin the maximum multiplier and solve in one turn", "🎡", 15000, isUnlocked = false),
        AchievementItem("ach_5", "Lone Survivor", "Defeat all 100 mob members in a single run", "👑", 25000, isUnlocked = false),
        AchievementItem("ach_6", "Master Debater", "Win 10 live crowd verdicts in the Debate Arena", "🎙️", 20000, isUnlocked = false),
        AchievementItem("ach_7", "Squad Hivemind", "Win 5 matches with friends using Nearby Radar", "👥", 12000, isUnlocked = false)
    )

    // Reward Shop Items
    val shopItems = listOf(
        ShopItem("shop_1", "Golden VIP Host Microphone", "Avatar Accessory", "Gives golden animated soundwaves in all team games", "🎙️", 500.0, 15000),
        ShopItem("shop_2", "Crown of Champions", "Avatar Hat", "Sparkling royalty crown visible on stage podiums", "👑", 750.0, 25000),
        ShopItem("shop_3", "5x Bank Multiplier Ticket", "Game Booster", "Multiplies your next win bank by 5x in any match", "⚡", 250.0, 8000),
        ShopItem("shop_4", "Diamond Confetti Cannon", "Victory FX", "Triggers diamond confetti burst upon winning matches", "💎", 400.0, 12000),
        ShopItem("shop_5", "Hilarious Name Customizer Pass", "Game Pass", "Create custom custom family names anytime", "🎲", 150.0, 5000),
        ShopItem("shop_6", "Instant 100k ZynPoints Booster", "Currency Pack", "Adds 100,000 PTS immediately to your account", "🪙", 1000.0, null)
    )

    // Daily Spin Wheel Lucrative Rewards (Super High Rewards, Zero Cooldown, Zero Ads)
    val spinRewards = listOf(
        SpinReward("sr_1", "500 ZYNGOLD JACKPOT", 500.0, 0, "👑", 0xFF00E676),
        SpinReward("sr_2", "250,000 ZYNPOINTS", 0.0, 250000, "🪙", 0xFFFFD700),
        SpinReward("sr_3", "250 ZYNGOLD REWARD", 250.0, 0, "💰", 0xFF00E5FF),
        SpinReward("sr_4", "GOLDEN MYSTERY BOX", 150.0, 100000, "🎁", 0xFFFF4081),
        SpinReward("sr_5", "5X REWARD MULTIPLIER", 100.0, 50000, "⚡", 0xFF7C4DFF),
        SpinReward("sr_6", "100 ZYNGOLD PRIZE", 100.0, 0, "✨", 0xFF00B0FF),
        SpinReward("sr_7", "100,000 ZYNPOINTS", 0.0, 100000, "🪙", 0xFFFFAB00),
        SpinReward("sr_8", "DIAMOND VIP CHEST", 300.0, 150000, "💎", 0xFFE040FB)
    )
}
