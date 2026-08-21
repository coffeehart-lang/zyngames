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
}
