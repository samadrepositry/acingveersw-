package com.example.data.catalog

object CatalogDepartmentsFtoJ {
    val departments = listOf(
        Department(
            id = "emotion",
            code = "DEPT F",
            title = "Emotional Expression Lab",
            urduTitle = "جذباتی اظہار کی لیبارٹری",
            description = "Explore 25 emotional states through safe imagination, physicality, voice, and camera.",
            safetyNote = "Use safe imagination, breath, and character objectives. Never relive personal trauma or use unsafe psychological techniques."
        ),
        Department(
            id = "facial",
            code = "DEPT G",
            title = "Facial & Body Language",
            urduTitle = "چہرے کے تاثرات اور باڈی لینگویج",
            description = "Refine micro-expressions, eye contact, stillness, hand precision, and character walks."
        ),
        Department(
            id = "improvisation",
            code = "DEPT H",
            title = "Improvisation Training",
            urduTitle = "فی البدیہہ اداکاری",
            description = "Sharpen spontaneity with random locations, characters, conflicts, and yes-and drills."
        ),
        Department(
            id = "camera",
            code = "DEPT I",
            title = "Camera & Screen Acting",
            urduTitle = "کیمرہ اور اسکرین اداکاری",
            description = "Master self-tapes, eye-line discipline, framing shots, slates, and natural screen truth."
        ),
        Department(
            id = "monologue",
            code = "DEPT J",
            title = "Monologues & Scripts",
            urduTitle = "مونولاگ اور اسکرپٹ مشق",
            description = "Original practice scripts across 11 genres with beat analysis and memorization tools."
        )
    )

    private fun emotionPack(
        id: String,
        name: String,
        face: String,
        body: String,
        voice: String,
        dialogue: String,
        silent: String,
        camera: String
    ) = EmotionExercisePack(id, name, face, body, voice, dialogue, silent, camera)

    val emotionPacks = listOf(
        emotionPack("em1", "Happiness", "Warm crinkle around eyes, natural lifted cheeks.", "Buoyant chest, open palms, light stride.", "Bright forward resonance, lifted tempo.", "\"You actually came—I didn't think you'd make it!\"", "Watch someone arrive from across the room and brighten quietly.", "Close-up smile emerging slowly in the eyes first."),
        emotionPack("em2", "Sadness", "Softened brow, downward gaze stillness.", "Heavy shoulders dropping gently on exhale.", "Quiet, low breath-supported tone.", "\"It's okay. We both knew summer had to end.\"", "Fold a jacket left behind and sit quietly for 20 seconds.", "Hold still in medium close-up letting eyes speak without blinking fast."),
        emotionPack("em3", "Anger", "Focused brow, steady jaw without clenching teeth.", "Rooted feet, forward leaning intent.", "Grounded chest projection, crisp consonants.", "\"Do not tell me to calm down when you broke the promise!\"", "Stand rigidly holding a door closed silently.", "Controlled low-volume intensity into lens eye-line—less is more."),
        emotionPack("em4", "Fear", "Widened alert eyes, slight breath intake.", "Weight shifting subtly backward, defensive posture.", "Higher, clipped phrasing with quick breath.", "\"Did you hear footsteps on the stairs just now?\"", "Freeze mid-step listening to an unseen sound.", "Micro-flinch and rapid eye scan in tight close-up."),
        emotionPack("em5", "Surprise", "Lifted eyebrows, suspended breath for 1 beat.", "Sudden freeze or quick half-step turn.", "Sharp upward inflection.", "\"Wait—that envelope has my name on it?\"", "Open a box expecting papers and discover a key.", "Capture the exact 1-second micro-beat before words arrive."),
        emotionPack("em6", "Disgust", "Subtle nose bridge wrinkle, narrowed eyes.", "Slight recoil away from stimulus.", "Clipped, nasal aversion tone.", "\"Take that away from the table immediately.\"", "Taste spoiled milk without exaggerating.", "Subtle nostril flare and glance-away in close-up."),
        emotionPack("em7", "Jealousy", "Guarded side-glance, tight polite smile.", "Crossed arms or fidgeting ring.", "Controlled even pitch hiding sharp edge.", "\"Oh, I'm thrilled for you. Truly.\"", "Watch an award go to a rival while clapping politely.", "Keep polite smile while eyes cool noticeably."),
        emotionPack("em8", "Confusion", "Tilted head, searching eye movement.", "Paused gesture mid-air.", "Questioning upward cadence.", "\"Hold on—you said Tuesday, didn't you?\"", "Read a message twice trying to make sense of it.", "Subtle squint and blink while processing."),
        emotionPack("em9", "Nervousness", "Rapid blinking, quick glance down.", "Shifting weight, rubbing thumb on fingers.", "Slightly hurried pace, light throat catch.", "\"I'm ready, I just—let me check my notes once more.\"", "Wait outside an audition room holding a script.", "Keep frame still while hands adjust collar."),
        emotionPack("em10", "Excitement", "Wide, bright eyes, animated brow.", "Forward lean, springy energy.", "Crisp, rising pitch and rhythmic speed.", "\"We got greenlit! Pack your bags tonight!\"", "Read an acceptance email silently then celebrate silently.", "Contain physical bounce inside medium-shot frame."),
        emotionPack("em11", "Frustration", "Compressed lips, slow exhale through nose.", "Hands raking gently back or resting on hips.", "Tight clipped cadence.", "\"I've explained this three times already!\"", "Try unlocking a jammed door while running late.", "Show internal restraint before speaking."),
        emotionPack("em12", "Loneliness", "Distant soft focus, unhurried blinks.", "Curled posture, self-hugging arms.", "Soft, unhurried tone fading slightly.", "\"I left the porch light on anyway.\"", "Set two cups on a table, then put one back.", "Wide-shot isolation contrasting empty room space."),
        emotionPack("em13", "Confidence", "Level gaze, relaxed unforced brow.", "Balanced spine, unhurried purposeful movement.", "Steady mid-low resonance, unhurried pace.", "\"Leave the presentation to me. We're ready.\"", "Walk to center mark and pause calmly for 5 seconds.", "Direct eye-line poise without over-smiling."),
        emotionPack("em14", "Shame", "Averted downward eyes, warm flush.", "Shoulders rounding inward protecting chest.", "Soft, hesitant volume.", "\"I knew better, and I still went through with it.\"", "Hide an object quickly as someone enters.", "Eyes drop just past lens before slowly returning."),
        emotionPack("em15", "Guilt", "Troubled brow, sideways check toward partner.", "Restless hand clasp.", "Quiet apologetic pitch.", "\"Tell me how to make this right.\"", "Stare at an unanswered phone call.", "Micro-hesitation before speaking the truth."),
        emotionPack("em16", "Relief", "Softened eyes, slow downward blink.", "Long exhale dropping shoulders 2 inches.", "Warm sighing breath behind words.", "\"Thank goodness—you're finally safe.\"", "Hear good test results and sink onto a chair.", "Let camera catch the full exhale release."),
        emotionPack("em17", "Suspicion", "Slightly narrowed steady eyes.", "Still torso, slow calculated turn.", "Low, probing cadence.", "\"Why were you still in the office at midnight?\"", "Inspect a misplaced notebook silently.", "Hold steady eye-line 3 seconds after asking the question."),
        emotionPack("em18", "Curiosity", "Raised inner brow, attentive lean.", "Head tilted slightly forward.", "Light, inquisitive inflection.", "\"Where did you learn how to play that tune?\"", "Examine an unfamiliar locked trunk.", "Follow an object smoothly across frame."),
        emotionPack("em19", "Love", "Soft warm eyes, unguarded expression.", "Relaxed open posture leaning slightly toward partner.", "Warm, velvety mid-tone.", "\"No matter how far you go, I'm in your corner.\"", "Adjust someone's scarf gently with care.", "Gentle eye softness in tight close-up."),
        emotionPack("em20", "Grief", "Quiet heaviness, unfocused gaze.", "Slow weighted movements.", "Breath-held pauses between simple words.", "\"The house is far too quiet without them.\"", "Touch an empty chair and sit slowly.", "Zero melodrama—pure quiet stillness on camera."),
        emotionPack("em21", "Disappointment", "Slow blink, corners of mouth softening downward.", "Deflated exhale, lowered hands.", "Flat, low-energy inflection.", "\"I really thought you'd keep your word.\"", "Fold up a carefully prepared speech.", "Subtle shift from hope to quiet acceptance."),
        emotionPack("em22", "Determination", "Steady jaw, unwavering eye focus.", "Squared shoulders, decisive step forward.", "Clear, rhythmic, unshakeable delivery.", "\"We start again at dawn. No excuses.\"", "Lace up boots and stand resolutely.", "Lock eye-line firmly onto camera mark."),
        emotionPack("em23", "Anxiety", "Darting glances, tight lower lid.", "Tapping heel, restless finger clasp.", "Fast, uneven breath cadence.", "\"What if the flight gets cancelled before noon?\"", "Check watch three times in 15 seconds.", "Controlled inner urgency without waving hands out of frame."),
        emotionPack("em24", "Compassion", "Soft empathetic brow, attentive stillness.", "Gentle forward nod, open arms.", "Soothing, unhurried pace.", "\"Take your time. I'm right here listening.\"", "Sit beside someone grieving and offer water.", "Warm receptive listening on camera."),
        emotionPack("em25", "Emotional Conflict", "Warm eyes battling tight jaw.", "Torn step forward then halt.", "Voice wavering gently between firmness and tenderness.", "\"I want to forgive you—I just don't know how yet.\"", "Pack a suitcase while glancing back at the room.", "Play both opposing urges visibly in close-up.")
    )

    private fun facialEx(id: String, title: String, diff: String, secs: Int, obj: String, inst: String) =
        TrainingExercise(
            id = id,
            departmentId = "facial",
            title = title,
            difficulty = diff,
            durationSeconds = secs,
            objective = obj,
            instructions = inst,
            correctTechnique = "Keep movements grounded in genuine thought; avoid theatrical mugging on camera.",
            commonMistakes = "Over-acting eyebrows or fidgeting hands continuously."
        )

    val facialExercises = listOf(
        facialEx("g1", "Facial Muscle Awareness", "Beginner", 90, "Isolate forehead, cheek, and jaw zones.", "Raise brows gently, relax; flare nostrils lightly, relax; smile gently with eyes, relax."),
        facialEx("g2", "Eye Expression (Smize & Focus)", "Beginner", 120, "Communicate subtext purely through the eyes.", "Keep mouth neutral while thinking: curiosity, warmth, suspicion, and determination."),
        facialEx("g3", "Eyebrow Movement Control", "Beginner", 60, "Prevent involuntary eyebrow bouncing on lines.", "Deliver 4 dramatic lines in front of camera keeping forehead calm until a deliberate emotional shift."),
        facialEx("g4", "Smile Control", "Beginner", 90, "Distinguish social polite smile vs. genuine joy.", "Practice transitioning slowly from neutral face to a subtle half-smile, then full genuine warmth."),
        facialEx("g5", "Jaw Expression & Release", "Beginner", 60, "Use subtle jaw set for resolve without grinding teeth.", "Contrast a soft receptive jaw with a determined set jaw across two character lines."),
        facialEx("g6", "Micro-Expression Awareness", "Intermediate", 120, "Capture fleeting half-second emotional truths.", "Allow a brief 0.5s flicker of surprise or hurt before masking it with composure."),
        facialEx("g7", "Neutral Face Practice", "Beginner", 90, "Establish a clean slate before takes.", "Hold 60 seconds of alert, relaxed neutral presence with steady breathing and open eyes."),
        facialEx("g8", "Eye Contact Discipline", "Beginner", 120, "Anchor steady eye-line without staring aggressively.", "Maintain calm eye contact on a single mark while delivering a persuasive monologue."),
        facialEx("g9", "Purposeful Hand Gestures", "Intermediate", 120, "Eliminate repetitive hand flapping.", "Speak for 60s allowing hands to move ONLY when punctuating a clear objective."),
        facialEx("g10", "Walking Styles Exploration", "Beginner", 120, "Discover rhythm, weight, and stride length.", "Walk across the room at light airy tempo, heavy grounded tempo, and brisk urgent tempo."),
        facialEx("g11", "Standing Posture Archetypes", "Beginner", 90, "Project character instantly upon entrance.", "Compare heroic open stance, cautious narrow stance, and relaxed grounded stance."),
        facialEx("g12", "Sitting Posture & Framing", "Beginner", 90, "Sit actively without slouching out of frame.", "Practice seated interview and dramatic table scenes keeping spine buoyant."),
        facialEx("g13", "Confident Body Language", "Beginner", 120, "Own space calmly without arrogance.", "Enter, take your mark, breathe once, and speak with unhurried open posture."),
        facialEx("g14", "Nervous Body Language", "Intermediate", 120, "Portray vulnerability cleanly without distracting clutter.", "Choose ONE clear physical tell (rubbing thumb) rather than five simultaneous fidgets."),
        facialEx("g15", "Character Walking Lab", "Intermediate", 150, "Lead physicality from different body centers.", "Walk leading with nose (curious), sternum (proud), or knees (hesitant)."),
        facialEx("g16", "Physical Storytelling", "Intermediate", 150, "Convey a clear narrative with zero dialogue.", "Act out entering an empty apartment expecting a friend, discovering a note, and reacting."),
        facialEx("g17", "Power of Stillness", "Advanced", 120, "Command attention by doing less.", "Perform an intense confrontation standing completely still from chest down."),
        facialEx("g18", "Movement Control & Precision", "Intermediate", 120, "Hit marks cleanly for cinema continuity.", "Walk 6 steps to an exact floor mark, turn smoothly on a specific word, and hold."),
        facialEx("g19", "Spatial Awareness", "Intermediate", 120, "Maintain awareness of set boundaries and camera frame.", "Navigate props and furniture smoothly while keeping eye-line clear.")
    )

    private fun improvEx(id: String, title: String, diff: String, secs: Int, obj: String, inst: String) =
        TrainingExercise(
            id = id,
            departmentId = "improvisation",
            title = title,
            difficulty = diff,
            durationSeconds = secs,
            objective = obj,
            instructions = inst,
            correctTechnique = "Say 'Yes, and...' to every impulse; commit boldly without judging ideas.",
            commonMistakes = "Blocking the premise, asking endless stalling questions, or apologizing mid-scene."
        )

    val improvExercises = listOf(
        improvEx("h1", "One-Word Storytelling", "Beginner", 90, "Build rapid listening and narrative momentum.", "Tell a coherent story speaking one word per beat or alternating aloud with a prompt cue."),
        improvEx("h2", "Random Object Acting", "Beginner", 120, "Transform mundane props into high-stakes items.", "Pick any nearby object and use it for 60s as something entirely different (a compass, a treasure, a ticking device)."),
        improvEx("h3", "Random Situation Acting", "Intermediate", 150, "Adapt instantly to unexpected circumstances.", "Use the Improv Generator below to get a Location + Conflict and act it out for 2 minutes."),
        improvEx("h4", "Character Improvisation", "Intermediate", 150, "Stay locked in character POV under surprises.", "Adopt a distinct physicality and voice, then answer 5 spontaneous interview questions in character."),
        improvEx("h5", "Two-Person Split Improv", "Intermediate", 180, "Practice contrasting tactics solo or with a partner.", "Play a negotiation where Character A wants something urgently and Character B protects a secret."),
        improvEx("h6", "Silent Improvisation", "Beginner", 120, "Tell stories purely through physical action.", "Perform a full beginning-middle-end scene in an airport or kitchen with zero spoken words."),
        improvEx("h7", "Emotional Improvisation", "Intermediate", 150, "Shift emotional gears fluidly on cue.", "Switch emotional intensity smoothly every 30 seconds while continuing the same story."),
        improvEx("h8", "Time-Limited 60s Scene", "Intermediate", 60, "Deliver setup, escalation, and button within 60 seconds.", "Start mid-action with high urgency and resolve the beat right as the timer hits zero."),
        improvEx("h9", "Random Location Immersion", "Beginner", 120, "Ground physical behavior in environment.", "Establish weather, temperature, and acoustics of the generated location through physical choices."),
        improvEx("h10", "Random Character Monologue", "Intermediate", 120, "Invent compelling character backstory on the spot.", "Speak for 90s as the generated character explaining why they arrived late."),
        improvEx("h11", "Conflict Escalation Drill", "Intermediate", 120, "Raise stakes naturally without yelling.", "Start with a small disagreement over a lost ticket and raise emotional stakes every 20 seconds."),
        improvEx("h12", "Unexpected Situation Pivot", "Advanced", 150, "Turn plot twists into fuel.", "Midway through your monologue, incorporate the generated Unexpected Twist seamlessly."),
        improvEx("h13", "Yes-And Exercises", "Beginner", 90, "Build generous collaborative momentum.", "Accept every premise out loud ('Yes, and because of that...') and add one concrete new detail."),
        improvEx("h14", "Story Continuation Sprint", "Beginner", 120, "Keep narrative threads cohesive.", "Continue a 3-sentence prompt into a vivid 90-second story with a clear ending."),
        improvEx("h15", "Reaction Games", "Beginner", 90, "Sharpen genuine spontaneous reactions.", "React honestly with body and voice to rapid imaginary headlines or sound cues.")
    )

    val improvLocations = listOf(
        "Midnight Train Platform in Lahore",
        "Rainy Rooftop Cafe in London",
        "Backstage Dressing Room before Opening Night",
        "Busy Mumbai Film Dubbing Booth",
        "Quiet Lighthouse Tower at Dawn",
        "Emergency Vet Clinic Waiting Room",
        "Luxury Auction House Vault",
        "Deserted Airport Gate 24B"
    )

    val improvCharacters = listOf(
        "A perfectionist pastry chef hiding a burnt cake",
        "A rookie detective on their first solo night shift",
        "An exhausted wedding planner with 10 minutes left",
        "A retired theatre legend returning for one cameo",
        "A nervous courier carrying a fragile heirloom",
        "A street musician auditioning for a film score"
    )

    val improvConflicts = listOf(
        "You have the wrong suitcase and your flight boards in 4 minutes.",
        "You must convince an old friend not to sell the family studio.",
        "You accidentally overheard a secret you weren't supposed to know.",
        "The power just went out right before the big reveal.",
        "You have 60 seconds to explain why you missed the premiere."
    )

    val improvTwists = listOf(
        "Suddenly you realize the person you're talking to is your new boss.",
        "A loud siren sounds outside and everyone freezes.",
        "You discover the key you've been looking for was in your pocket.",
        "They hand you a blank envelope and walk away."
    )

    private fun cameraEx(id: String, title: String, diff: String, secs: Int, obj: String, inst: String) =
        TrainingExercise(
            id = id,
            departmentId = "camera",
            title = title,
            difficulty = diff,
            durationSeconds = secs,
            objective = obj,
            instructions = inst,
            correctTechnique = "Keep head movements compact in close-ups; let eyes carry thought toward the lens eye-line.",
            commonMistakes = "Looking directly into the lens when scene requires off-camera eye-line, or pacing outside the frame."
        )

    val cameraExercises = listOf(
        cameraEx("i1", "Front-Camera Presence Practice", "Beginner", 120, "Become comfortable with the lens.", "Open Camera Studio, frame a Medium Close-Up, and speak calmly for 60s without checking your reflection."),
        cameraEx("i2", "Video Recording Self-Review", "Beginner", 120, "Evaluate technical framing and honesty objectively.", "Record a 45s take, watch it back once for framing/audio and once for emotional truth."),
        cameraEx("i3", "Self-Tape Audition Workflow", "Intermediate", 180, "Execute professional self-tape standards.", "Set neutral backdrop, eye-level lens, chest-up framing, slate cleanly, then perform a 60s scene."),
        cameraEx("i4", "Eye-Line Practice", "Beginner", 120, "Place off-camera scene partners consistently.", "Pick a focal point 6 inches left of lens for Partner A and 6 inches right for Partner B; never drift into the lens."),
        cameraEx("i5", "Close-Up Acting", "Intermediate", 120, "Scale thoughts down for intimate framing.", "Deliver emotional lines with minimal head movement—let inner thought register in the eyes."),
        cameraEx("i6", "Medium-Shot Acting", "Beginner", 120, "Balance facial nuance with clean upper-body gestures.", "Frame from waist up; keep hand gestures inside chest frame."),
        cameraEx("i7", "Wide-Shot Acting", "Intermediate", 150, "Tell stories through full posture and spatial rhythm.", "Step back to show full body; establish character physicality before speaking."),
        cameraEx("i8", "Camera Awareness", "Beginner", 90, "Hit marks smoothly without looking down.", "Step into frame, pause cleanly on your mark, and begin only after settling."),
        cameraEx("i9", "Natural Dialogue Delivery", "Beginner", 120, "Sound conversational rather than stagey on mic.", "Deliver film lines at intimate conversational volume as if speaking 3 feet away."),
        cameraEx("i10", "Silent Reactions on Camera", "Intermediate", 90, "Master the close-up reaction shot.", "Hold 30 seconds of active silent listening and subtle reaction shifts."),
        cameraEx("i11", "Film Audition Practice", "Intermediate", 180, "Prepare grounded cinematic realism.", "Rehearse a dramatic scene twice: once whispered for intimacy, once energized for urgency."),
        cameraEx("i12", "Commercial Audition Practice", "Beginner", 120, "Deliver warm, relatable 15s–30s commercial beats.", "Practice bright natural smile, clear product beat, and clean button at the end."),
        cameraEx("i13", "Character Continuity", "Intermediate", 150, "Repeat physical props and gestures identically across takes.", "Drink or hold a prop on the exact same line across 3 consecutive takes."),
        cameraEx("i14", "Scene Continuity", "Advanced", 150, "Match emotional energy entering a scene mid-moment.", "Start a second take seamlessly from the emotional temperature of the previous beat."),
        cameraEx("i15", "Emotional Consistency", "Advanced", 180, "Deliver 3 clean takes without burning out.", "Take 1 grounded, Take 2 higher stakes, Take 3 restrained stillness."),
        cameraEx("i16", "Professional Slate Recording", "Beginner", 60, "Record a crisp, confident audition slate.", "Look directly into lens: state name, height/city if requested, role, and warm smile before transitioning."),
        cameraEx("i17", "Monologue Self-Tape", "Intermediate", 180, "Record a complete polished monologue self-tape.", "Use Camera Studio with Close-Up guide, record, save notes, and request optional AI Coach clarity feedback.")
    )

    val monologues = listOf(
        MonologueItem(
            id = "m1",
            title = "The Last Train from Platform 4",
            genre = "Drama",
            medium = "Film",
            difficulty = "Beginner",
            characterDescription = "AYAAN / MAYA (20s–30s), a hopeful architect torn between family duty and an overseas fellowship.",
            givenCircumstances = "11:45 PM at a quiet train station; your sibling came to talk you out of leaving.",
            sceneObjective = "To make your sibling bless your departure without breaking their heart.",
            obstacles = "They feel abandoned; you feel guilty leaving the family business.",
            tactics = "Reassure warmly, challenge gently, make a promise.",
            beats = listOf("Beat 1: Acknowledge their sacrifice", "Beat 2: Reveal why staying shrinks you", "Beat 3: Promise to return stronger"),
            subtext = "I am terrified of failing abroad, too, so please don't make me doubt myself right now.",
            scriptLines = "Put the bag down for one second—just listen to me. I didn't buy this ticket to run away from home. Every brick in this house has your sweat on it, and I know that. But if I stay here just because it's safe, we'll both watch my dreams turn into excuses. Let me go build something we can both be proud of. Two years. That's all I'm asking—walk with me to the gate.",
            estimatedSeconds = 60
        ),
        MonologueItem(
            id = "m2",
            title = "Table for One, Actually",
            genre = "Comedy",
            medium = "Television",
            difficulty = "Beginner",
            characterDescription = "ZARA / SAM (20s–40s), an overly optimistic friend trying to salvage a disastrous blind date.",
            givenCircumstances = "A fancy restaurant where the date quietly slipped out through the kitchen exit.",
            sceneObjective = "To convince the skeptical waiter—and yourself—that dining solo is a luxury victory.",
            obstacles = "Embarrassment and the waiter holding a complimentary dessert meant for two.",
            tactics = "Charm, deflect, celebrate absurdly.",
            beats = listOf("Beat 1: Pretend the exit was planned", "Beat 2: Claim the extra breadsticks", "Beat 3: Order dessert boldly"),
            subtext = "Please don't pity me right now; just bring the chocolate cake.",
            scriptLines = "Oh, no, he didn't leave—he's just... intensely allergic to candlelight. Very sudden onset. Honestly, it's a relief, because now nobody is going to judge me for ordering the truffle fries AND the molten cake. Actually, keep the second chair right there. It gives me room for my elbows. Bring the check whenever—and maybe two spoons. One for each hand.",
            estimatedSeconds = 55
        ),
        MonologueItem(
            id = "m3",
            title = "The Unsent Postcard",
            genre = "Romance",
            medium = "Theatre",
            difficulty = "Intermediate",
            characterDescription = "ELENA / OMAR (25–40), returning a borrowed book seven years after a missed connection.",
            givenCircumstances = "A sunlit bookstore on a Sunday afternoon; rain just cleared outside.",
            sceneObjective = "To find out if the spark between you was real, before walking out the door.",
            obstacles = "Seven years of silence and fear of hearing 'I moved on.'",
            tactics = "Tease lightly, confess carefully, leave the door open.",
            beats = listOf("Beat 1: Return the dog-eared book", "Beat 2: Reveal the folded postcard inside", "Beat 3: Ask for five minutes of coffee"),
            subtext = "I kept this book just as an excuse to see you again.",
            scriptLines = "You forgot this on my bookshelf in 2019. Page eighty-four still has your coffee ring on it. I almost mailed it three times, especially the winter I moved to Karachi—no, wait, that's not true. I kept it because as long as I owed you a book, the story wasn't quite finished. You don't have to say anything right now. Just... if you're free for ten minutes, the café across the street still makes terrible tea.",
            estimatedSeconds = 65
        ),
        MonologueItem(
            id = "m4",
            title = "Signal Lost at Sector 9",
            genre = "Thriller",
            medium = "Film",
            difficulty = "Intermediate",
            characterDescription = "COMMANDER RHEA / KABIR (30s), a systems engineer discovering a deliberate sabotage in backup logs.",
            givenCircumstances = "3:00 AM inside a locked server room; footsteps are approaching outside.",
            sceneObjective = "To force your partner over the radio to wipe the compromised node before security breaches the door.",
            obstacles = "Radio static and your partner's hesitation to destroy 6 months of research.",
            tactics = "Command urgently, appeal to trust, count down.",
            beats = listOf("Beat 1: Prove the breach is inside", "Beat 2: Overcome partner's hesitation", "Beat 3: Execute final override"),
            subtext = "If you don't press that key in ten seconds, neither of us walks out clean.",
            scriptLines = "Stop talking and look at line four of the manifest! That wasn't a power surge—someone cloned my badge at 02:14. No, I don't care what the director promised you! If that packet leaves the building, every witness name goes public before sunrise. Trust me once in your life and pull the primary breaker. Now! Three... two... thank you.",
            estimatedSeconds = 60
        ),
        MonologueItem(
            id = "m5",
            title = "The Clockmaker's Alibi",
            genre = "Suspense",
            medium = "Television",
            difficulty = "Advanced",
            characterDescription = "FARAZ / CLAIRE (35–55), calmly dismantling a detective's timeline without raising their voice.",
            givenCircumstances = "An interrogation room with a single overhead lamp and a silent ticking wristwatch.",
            sceneObjective = "To plant reasonable doubt in the detective's certainty.",
            obstacles = "Circumstantial camera footage placing you near the alley.",
            tactics = "Redirect calmly, dissect logic, flip the question.",
            beats = listOf("Beat 1: Correct the detective's timestamp", "Beat 2: Explain the broken chime", "Beat 3: Challenge who benefits"),
            subtext = "You're looking at the clock on the wall when you should be watching the door behind you.",
            scriptLines = "You keep saying eleven-fifteen, Detective, because the streetlamp blinked out at eleven-fifteen. Have you checked when the city grid actually reset? No. Of course you haven't. Look at my watch—it stopped at eleven-twelve when the mains dropped, three minutes before anyone stepped into that alley. So before you close your neat little folder, ask yourself why the night guard turned off the lobby camera at eleven-oh-five.",
            estimatedSeconds = 65
        ),
        MonologueItem(
            id = "m6",
            title = "Ashes of the Courtyard",
            genre = "Tragedy",
            medium = "Theatre",
            difficulty = "Advanced",
            characterDescription = "NOOR / HAMZA (30s–50s), standing in an ancestral courtyard being sold to settle old debts.",
            givenCircumstances = "Early morning before buyers arrive; packing the last brass lantern.",
            sceneObjective = "To preserve dignity and memory in the face of irreversible loss.",
            obstacles = "Bitterness toward relatives who walked away years ago.",
            tactics = "Remember tenderly, confront truth, release peacefully.",
            beats = listOf("Beat 1: Recall childhood laughter here", "Beat 2: Face the silence of empty walls", "Beat 3: Carry the lantern forward"),
            subtext = "A house is only stone; what mattered was who stood inside it.",
            scriptLines = "Do you remember how the jasmine climbed all the way to the upper balcony in July? Everyone told us walls hold memories forever. They don't. Tomorrow someone else will paint over the height marks we carved by the kitchen door, and the gate won't even remember our footsteps. Take the keys, then. Keep the deed. I'm taking the lantern—because light belongs to whoever keeps walking.",
            estimatedSeconds = 70
        ),
        MonologueItem(
            id = "m7",
            title = "Sixty Seconds to Impact",
            genre = "Action",
            medium = "Film",
            difficulty = "Intermediate",
            characterDescription = "VIKRAM / ALEX (20s–40s), guiding an injured co-pilot through an emergency manual landing.",
            givenCircumstances = "Cockpit turbulence, warning alarms flashing, runway lights barely visible through fog.",
            sceneObjective = "To keep the co-pilot focused and steady through landing.",
            obstacles = "Panic, noise, and failing hydraulic pressure.",
            tactics = "Anchor breath, bark crisp steps, reassure fiercely.",
            beats = listOf("Beat 1: Cut through panic", "Beat 2: Lock eyes on horizon", "Beat 3: Execute touchdown"),
            subtext = "I'm terrified too, but if I blink, we lose it.",
            scriptLines = "Hey! Eyes on me—forget the alarm! Breathe with me right now: in, out. Good. Keep both hands on the yoke, ignore the red light on panel three, that sensor's dead. When I count three, pull nose up four degrees—not five, four! We've flown worse storms than this with half an engine. Hold steady... hold... now!",
            estimatedSeconds = 50
        ),
        MonologueItem(
            id = "m8",
            title = "The Audition Waiting Room",
            genre = "Emotional Drama",
            medium = "Film",
            difficulty = "Beginner",
            characterDescription = "SARA / DEV (20s), calling home after thirty rejections in a row.",
            givenCircumstances = "Sitting on a stairwell outside a casting studio; rain outside.",
            sceneObjective = "To ask for encouragement without sounding defeated.",
            obstacles = "Pride and fear of disappointing parents who believed in you.",
            tactics = "Joke weakly, confess exhaustion, find second wind.",
            beats = listOf("Beat 1: Try to sound upbeat", "Beat 2: Admit how hard today felt", "Beat 3: Commit to tomorrow's rehearsal"),
            subtext = "I just need to hear your voice say keep going.",
            scriptLines = "Hi Ma. Yeah, it went... fine. Well, the casting director smiled, which either means I nailed it or my shirt was inside out. Actually... can I be honest for thirty seconds? That was my thirty-first room this month, and sometimes I feel completely invisible out here. I don't want to quit. I just needed to hear somebody say my name like it still matters.",
            estimatedSeconds = 55
        )
    )

    val trainingPlans = listOf(
        TrainingPlan("plan_7", "7-Day Beginner Challenge", 7, "Breath, Posture, Voice Warm-up & First Monologue", "free", 20,
            listOf("Day 1: Diaphragmatic Breath & Neutral Stance", "Day 2: Neck/Jaw Release & Humming", "Day 3: Lip Trills & Crisp Consonants", "Day 4: Observation & Listening Craft", "Day 5: Neutral Face & Camera Eye-Line", "Day 6: Emotion Lab + One-Word Improv", "Day 7: Record Your First 60s Monologue")),
        TrainingPlan("plan_14", "14-Day Voice Challenge", 14, "Resonance, Projection, Pacing & Multilingual Diction", "free", 25,
            listOf("Week 1: Rib Expansion, Lip/Tongue Trills, Pitch Sirens & Chest Resonance", "Week 2: Stage Whisper, Pauses, English/Urdu/Hindi Diction & Studio Playback")),
        TrainingPlan("plan_21", "21-Day Acting Challenge", 21, "Objectives, Tactics, Subtext & Emotional Truth", "pro", 30,
            listOf("Week 1: Given Circumstances, Objectives & Listening", "Week 2: Obstacles, Tactics, Action Verbs & Subtext", "Week 3: Emotional Lab, Monologue Beats & Self-Tape")),
        TrainingPlan("plan_30_screen", "30-Day Screen Acting Program", 30, "Close-Up Nuance, Eye-Lines, Self-Tapes & Continuity", "pro", 35,
            listOf("Phase 1: Lens Comfort & Close-Up Stillness", "Phase 2: Film vs. Commercial Auditions & Slates", "Phase 3: Multi-Take Continuity & AI Clarity Review")),
        TrainingPlan("plan_30_voice", "30-Day Voice Development Program", 30, "Voice-Over Mastery, Character Voices & Stamina", "pro", 30,
            listOf("Phase 1: Breath Economy & Open Throat", "Phase 2: Accent Awareness & Modulation", "Phase 3: Character Voices & Mic Technique")),
        TrainingPlan("plan_60", "60-Day Professional Acting Program", 60, "Complete Conservatory Craft + Audition Portfolio", "premium", 45,
            listOf("Month 1: Instrument Mastery (Breath, Body, Voice, Diction)", "Month 2: Scene Study, Emotional Conflict, Camera Mastery & Career Reel")),
        TrainingPlan("plan_90", "90-Day Complete Actor Development", 90, "Elite Daily Discipline Across All 10 Departments", "premium", 60,
            listOf("Months 1–3: Holistic Physical, Vocal, Emotional, Improv, Screen & Audition Readiness"))
    )

    val courses = listOf(
        CourseModule("course_1", "Foundations of Screen Presence", "Beginner", 6, "camera",
            "Learn how the camera reads thought, stillness, and authentic eye-line.",
            listOf("1. Lens as Scene Partner", "2. Close-Up Economy", "3. Mastering the Slate", "4. Eye-Line Anchors", "5. Listening on Camera", "6. Self-Tape Lighting & Sound")),
        CourseModule("course_2", "Vocal Power & Resonance Studio", "Intermediate", 8, "voice",
            "Transform vocal strain into effortless projection and rich tonal range.",
            listOf("1. Rib Support", "2. Lip & Tongue Trills", "3. Chest vs Head Resonance", "4. Vocal Sirens", "5. Articulation Drills", "6. Pacing & Pauses", "7. Stage Whisper", "8. Character Voices")),
        CourseModule("course_3", "Script Analysis & Subtext Mastery", "Intermediate", 7, "monologue",
            "Break down any scene into actionable objectives, obstacles, and beats.",
            listOf("1. Given Circumstances", "2. Objective & Stakes", "3. Identifying Obstacles", "4. Transitive Verbs", "5. Beat Transitions", "6. Playing Subtext", "7. Memorization Without Patterning")),
        CourseModule("course_4", "South Asian & Global Diction Lab", "All Levels", 6, "diction",
            "Polish English, Urdu, Hindi, Punjabi, Sindhi, and Arabic clarity for film & theatre.",
            listOf("1. Breath & Consonant Release", "2. English Plosives", "3. American vs British Vowels", "4. Urdu Makhraj & Nuqta", "5. Hindi Aspirates", "6. Cold Reading Rhythm"))
    )

    val achievements = listOf(
        AchievementDef("first_exercise", "First Exercise", "Complete your first timed training drill.", 50),
        AchievementDef("first_recording", "First Recording", "Save your first Voice or Camera Studio take.", 75),
        AchievementDef("streak_7", "7-Day Streak", "Maintain 7 consecutive days of actor discipline.", 200),
        AchievementDef("streak_30", "30-Day Streak", "Maintain 30 consecutive days of practice.", 750),
        AchievementDef("voice_explorer", "Voice Explorer", "Complete 5 Voice Training sessions.", 120),
        AchievementDef("breath_beginner", "Breath Control Beginner", "Complete 3 Breathing sessions.", 100),
        AchievementDef("diction_master", "Diction Master", "Complete 5 Diction & Pronunciation drills.", 150),
        AchievementDef("monologue_performer", "Monologue Performer", "Practice or analyze 3 monologues.", 150),
        AchievementDef("camera_confidence", "Camera Confidence", "Record 3 Camera Studio self-tapes.", 180),
        AchievementDef("improv_champion", "Improvisation Champion", "Complete 5 Improv challenges.", 150),
        AchievementDef("consistent_actor", "Consistent Actor", "Reach 10 completed training exercises.", 250),
        AchievementDef("pro_milestone", "Professional Milestone", "Reach 1,000+ total XP across departments.", 500)
    )

    val currencies = listOf(
        CountryCurrency("Pakistan", "PKR", "Rs ", "1,490", "2,990"),
        CountryCurrency("United States", "USD", "$", "9.99", "19.99"),
        CountryCurrency("United Kingdom", "GBP", "£", "7.99", "15.99"),
        CountryCurrency("Canada", "CAD", "C$", "13.49", "26.99"),
        CountryCurrency("Europe", "EUR", "€", "8.99", "17.99"),
        CountryCurrency("UAE / Middle East", "AED", "AED ", "36", "72")
    )

    val faqs = listOf(
        "How quickly can a complete beginner start?" to "Within 30 seconds: pick any 60-second Breathing or Voice Warm-Up exercise, tap Start Exercise, and follow the guided timer.",
        "Does ACTORVERSE judge or guarantee casting?" to "Never. All pacing, clarity, and AI Coach feedback is supportive craft guidance—never a casting judgment or role guarantee.",
        "Are my voice and camera recordings private?" to "Yes. Recordings stay stored privately on your device and your private Firestore subcollection unless you explicitly share a post in Community.",
        "How does Urdu and regional diction training work?" to "Department D provides targeted makhraj, aspirated consonant, and rhythm drills across English, American English, British English, Urdu, Hindi, Punjabi, Sindhi, and Arabic, plus full Urdu UI support.",
        "How do subscriptions and billing work?" to "Free includes core exercises, smart timers, and starter plans. Pro and Premium plans unlock expanded AI coaching and studio tools once merchant billing is activated."
    )

    val allExercises: List<TrainingExercise> by lazy {
        CatalogDepartmentsAtoE.breathingExercises +
            CatalogDepartmentsAtoE.bodyExercises +
            CatalogDepartmentsAtoE.voiceExercises +
            CatalogDepartmentsAtoE.dictionExercises +
            CatalogDepartmentsAtoE.actingExercises +
            facialExercises +
            improvExercises +
            cameraExercises
    }

    val allDepartments: List<Department> by lazy {
        CatalogDepartmentsAtoE.departments + departments
    }
}
