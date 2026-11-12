package com.captain.aipirate.data

import java.util.Calendar

/** Built-in offline content: a fresh riddle and Captain's Log every day. */
object PirateContent {

    data class Riddle(val question: String, val answer: String)
    data class LogEntry(val quote: String, val lesson: String)

    val riddles = listOf(
        Riddle("I have seas without water, coasts without sand, towns without people and mountains without land. What am I?", "A map, matey! Every treasure hunt starts with one."),
        Riddle("What can run but never walks, has a mouth but never talks, has a bed but never sleeps?", "A river, yar — it runs to the sea day and night."),
        Riddle("The more of me you take, the more you leave behind. What am I?", "Footsteps on the sand, dost!"),
        Riddle("I speak without a mouth and hear without ears. I have no body, but I come alive with wind. What am I?", "An echo — the sea caves love to repeat your words."),
        Riddle("What has keys but can't open a single lock?", "A piano, matey — or a keyboard. No treasure chest though!"),
        Riddle("What gets wetter the more it dries?", "A towel, yar. Even pirates need one after a storm."),
        Riddle("I am always hungry and must always be fed. The finger I touch will soon turn red. What am I?", "Fire — respect it, or it will eat your ship."),
        Riddle("What has a head and a tail but no body?", "A coin, dost! Heads or tails, the treasure decides."),
        Riddle("The person who makes it has no need of it. The person who buys it has no use for it. The person who uses it can neither see nor feel it. What is it?", "A coffin. Dark waters, matey — choose life instead."),
        Riddle("What can you catch but never throw?", "A cold, yar. Or a good idea — catch plenty of those!"),
        Riddle("What has many teeth but cannot bite?", "A comb, matey. Even a captain must look sharp."),
        Riddle("What goes up but never comes down?", "Your age, dost. So spend it on adventures."),
        Riddle("I have cities but no houses, forests but no trees, and water but no fish. What am I?", "A map again? No — a globe! The whole world in your hands."),
        Riddle("What has one eye but cannot see?", "A needle, yar. It still sews the sails together.")
    )

    val logs = listOf(
        LogEntry("The sea does not reward the fearful — it rewards the ones who set sail.", "Aaj ek chhota sa kadam uthao, chahe dar lage. Safe harbour mein koi kahani nahi banti."),
        LogEntry("A smooth sea never made a skilled sailor.", "Mushkil waqt tumhe mazboot banata hai. Storm se bhaago mat, us se seekho."),
        LogEntry("Treasure is not the gold — it is the crew that sails with you.", "Aaj kisi dost ko yaad karo aur uska shukriya ada karo."),
        LogEntry("Even the darkest night ends with a sunrise over the sea.", "Agar aaj mushkil hai to yaad rakho: ye bhi guzar jayega."),
        LogEntry("A captain who cannot listen will soon sail alone.", "Aaj kisi ki baat poori tawajjo se suno — bina interrupt kiye."),
        LogEntry("Small leaks sink great ships.", "Chhoti buri aadatein aaj hi theek karo, kal bahot der ho sakti hai."),
        LogEntry("The wind favours the ship with a destination.", "Aaj apna ek goal likho — sirf ek — aur uski taraf pehla kadam uthao."),
        LogEntry("Courage is not the absence of fear, it is sailing anyway.", "Dar ke bawajood aaj woh kaam karo jo tum taal rahe ho."),
        LogEntry("Every great voyage begins with a single decision to leave the shore.", "Shuru karna hi aadhi jeet hai. Bas shuru karo, yar."),
        LogEntry("A pirate shares the loot and the laughter in equal measure.", "Aaj kisi ke saath kuch achha share karo — waqt, khana ya ek achhi baat."),
        LogEntry("Storms make the wood stronger and the sailor wiser.", "Jo mushkil tumne paar ki, us par fakhar karo — tum pehle se zyada mazboot ho."),
        LogEntry("The best compass is a calm mind.", "Jab gussa ya pareshani ho, pehle gehra saans lo, phir faisla karo."),
        LogEntry("No one discovers new oceans without losing sight of the shore.", "Comfort zone se bahar niklo — wahi asli adventure hai."),
        LogEntry("A tidy ship is a happy ship.", "Aaj apni jagah, apna kaam ya apna phone — kuch ek saaf suthra karo.")
    )

    val jokes = listOf(
        "Why did the pirate go to school? To improve his arrr-ticulation, yar!",
        "What is a pirate's favourite letter? You'd think it's R... but his first love be the C!",
        "Why are pirates called pirates? Because they arrr, matey!",
        "What did the pirate say on his 80th birthday? Aye matey, I'm getting old!",
        "Why did the pirate buy an eye patch? Because he couldn't see the treasure without one!",
        "How much does a pirate pay for corn? A buccaneer, dost!",
        "What do you call a pirate who skips class? Captain Hooky!",
        "Why don't pirates shower before they walk the plank? Because they'll just wash up on shore anyway!"
    )

    val shers = listOf(
        "Samundar ki lehron se seekha hai jeena,\nToofan mein bhi muskurana, yehi hai apna tareeqa.",
        "Khazana dhoondne nikle the hum saat samundar paar,\nManzil mili to jaana, safar hi tha asal pyaar.",
        "Raaton ke andhere mein bhi sitaare raasta dikhate hain,\nJo himmat na haare, wohi kinara paate hain.",
        "Dil dariya hai, lehron mein behta hai,\nJo doob gaya ishq mein, wohi to kehta hai.",
        "Kashti toofan se ladti hai, kinara chhoda nahi,\nZindagi bhi aisi hai, himmat todi nahi."
    )

    val storyStarters = listOf(
        "The Map That Lied",
        "The Storm of Golden Lightning",
        "The Island That Appeared Only at Midnight",
        "The Parrot Who Knew the Future",
        "The Treasure of the Sleeping Whale"
    )

    private fun dayOfYear(): Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    fun todaysRiddle(): Riddle = riddles[dayOfYear() % riddles.size]

    fun todaysLog(): LogEntry = logs[dayOfYear() % logs.size]

    fun randomJoke(seed: Int): String = jokes[seed % jokes.size]

    fun randomSher(seed: Int): String = shers[seed % shers.size]

    /** Offline pirate-speak fallback when no API key is set. */
    fun translateOffline(text: String): String {
        var out = "Arrr, matey! $text"
        val swaps = mapOf(
            "hello" to "ahoy", "hi" to "ahoy", "friend" to "matey",
            "friends" to "me hearties", "yes" to "aye", "no" to "nay",
            "my" to "me", "your" to "yer", "you" to "ye",
            "money" to "treasure", "treasure" to "booty",
            "good" to "grand", "great" to "grand",
            "let's" to "let's set sail and", "okay" to "aye aye",
            "ok" to "aye aye", "sir" to "cap'n"
        )
        for ((k, v) in swaps) {
            out = out.replace(Regex("(?i)\\b$k\\b"), v)
        }
        return out
    }
}
