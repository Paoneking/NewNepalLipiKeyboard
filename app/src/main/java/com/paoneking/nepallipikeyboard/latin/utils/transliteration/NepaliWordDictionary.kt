package com.paoneking.nepallipikeyboard.latin.utils.transliteration

import android.content.Context

object NepaliWordDictionary {

    private const val DEFAULT_FREQUENCY = 100
    private const val FILE_DEFAULT_FREQUENCY = 150

    // Per-word frequencies for the most common entries.
    // Words not listed here get DEFAULT_FREQUENCY.
    private val builtInFrequencies = mapOf(
        // Particles / connectors
        "ra" to 250, "ma" to 250, "tapai" to 248, "hami" to 240,
        "ke" to 245, "ko" to 242, "timi" to 235, "uni" to 230,
        "yo" to 240, "tyo" to 240, "ki" to 235, "nai" to 230,
        "pani" to 230, "aba" to 225, "chai" to 228, "ali" to 220,
        "kehi" to 228, "sabai" to 228, "kohi" to 222, "afno" to 228,
        "tara" to 232, "bhane" to 225, "vane" to 220,
        "jasle" to 210, "tyasle" to 208, "yesle" to 208,
        // Common verbs
        "garnu" to 235, "garchha" to 228, "garchhu" to 222, "garne" to 225,
        "bhayo" to 228, "huncha" to 228, "hunchha" to 222,
        "jancha" to 218, "janchu" to 212, "aaucha" to 218, "aauchu" to 212,
        "bolnu" to 205, "khanu" to 215, "khancha" to 208, "khanchhu" to 205,
        "dekhnu" to 205, "sunu" to 200, "lekhnu" to 200, "padhnu" to 200,
        "gareko" to 218, "gardai" to 215, "bhayeko" to 218,
        "dinu" to 205, "linu" to 200, "paaunu" to 200,
        // Common nouns
        "ghar" to 232, "naam" to 228, "din" to 222, "paani" to 225,
        "khana" to 225, "maanche" to 218, "manche" to 218, "kaam" to 225,
        "desh" to 215, "shahar" to 210, "gaun" to 210,
        "bihana" to 205, "raat" to 210, "saanjh" to 200,
        "saathi" to 215, "parivar" to 205, "parivaar" to 205,
        "kitab" to 195, "skul" to 190, "school" to 190, "nadi" to 192,
        "samaya" to 215, "samaaya" to 215, "khabar" to 205, "samachar" to 200,
        // Family
        "aama" to 235, "buwa" to 235, "bua" to 228, "didi" to 225,
        "daju" to 220, "bhai" to 225, "bahini" to 220, "babu" to 215,
        "nani" to 210, "chhora" to 215, "chhori" to 215,
        "kaka" to 205, "kaki" to 200, "mama" to 205, "maiju" to 198,
        // Question words
        "kaha" to 228, "kahaa" to 228, "kasari" to 225, "kina" to 225,
        "kati" to 220, "kahile" to 218, "kun" to 215,
        // Greetings
        "namaste" to 235, "dhanyabad" to 230, "dhanyabaad" to 225,
        "hajur" to 225, "sanchai" to 215, "namaskar" to 228,
        // Adjectives
        "ramro" to 228, "thulo" to 220, "saano" to 220, "naya" to 215,
        "dherai" to 225, "thorai" to 210, "thikai" to 210,
        "khushi" to 210, "ramailo" to 205, "naramro" to 200,
        // Locations
        "yaha" to 225, "tyaha" to 220, "mathi" to 215, "tala" to 215,
        "bhitra" to 215, "bahira" to 210, "agadi" to 210, "pachi" to 210,
        // Places
        "nepal" to 235, "kathmandu" to 230,
        // Numbers
        "ek" to 225, "dui" to 225, "teen" to 220, "char" to 220,
        "paanch" to 215, "chha" to 215, "saat" to 210, "aath" to 210,
        "nau" to 205, "das" to 215, "hajar" to 205, "saya" to 200,
        // Time
        "aaja" to 230, "bholi" to 225, "hijo" to 225, "parsi" to 215,
        "asti" to 215, "pahile" to 220, "pachhi" to 218, "ahile" to 225,
        "abhi" to 218,
        // Misc common
        "chhaina" to 225, "kasto" to 220, "pheri" to 220,
        "paisa" to 215, "paisaa" to 215,
    )

    private val fileFrequencies = mutableMapOf<String, Int>()

    // Built-in hardcoded map (fallback if file not loaded)
    private val builtInWordMap = mapOf(
        // Places
        "nepal"         to "नेपाल",
        "nepali"        to "नेपाली",
        "nepaali"       to "नेपाली",
        "kathmandu"     to "काठमाडौं",
        "kaaThmaDauM"   to "काठमाडौं",
        "pokhara"       to "पोखरा",
        "lumbini"       to "लुम्बिनी",
        "everest"       to "एभरेस्ट",
        "himalaya"      to "हिमालय",

        // Greetings
        "namaste"       to "नमस्ते",
        "namaskar"      to "नमस्कार",
        "dhanyabad"     to "धन्यवाद",
        "dhanyabaad"    to "धन्यवाद",
        "shuvaprabhaat" to "शुभप्रभात",
        "subhaprabhaat" to "शुभप्रभात",
        "hajur"         to "हजुर",
        "sanchai"       to "सन्चै",

        // Days
        "aaitabaar"     to "आइतबार",
        "sombaar"       to "सोमबार",
        "mangalbaar"    to "मंगलबार",
        "budhbaar"      to "बुधबार",
        "bihibaar"      to "बिहीबार",
        "shukrabaar"    to "शुक्रबार",
        "shanibaar"     to "शनिबार",

        // Months
        "baisakh"       to "बैशाख",
        "jestha"        to "जेठ",
        "ashadh"        to "असार",
        "shrawan"       to "श्रावण",
        "bhadra"        to "भाद्र",
        "ashwin"        to "आश्विन",
        "kartik"        to "कार्तिक",
        "mangsir"       to "मंसिर",
        "poush"         to "पौष",
        "magh"          to "माघ",
        "falgun"        to "फाल्गुन",
        "chaitra"       to "चैत्र",

        // Numbers
        "ek"            to "एक",
        "dui"           to "दुई",
        "teen"          to "तीन",
        "char"          to "चार",
        "paanch"        to "पाँच",
        "chha"          to "छ",
        "saat"          to "सात",
        "aath"          to "आठ",
        "nau"           to "नौ",
        "das"           to "दस",
        "saya"          to "सय",
        "hajar"         to "हजार",

        // Pronouns
        "ma"            to "म",
        "tapai"         to "तपाईं",
        "tapaiko"       to "तपाईंको",
        "hami"          to "हामी",
        "timi"          to "तिमी",
        "uni"           to "उनी",
        "timro"         to "तिम्रो",
        "mero"          to "मेरो",
        "hamro"         to "हाम्रो",
        "yo"            to "यो",
        "tyo"           to "त्यो",

        // Common verbs
        "garnu"         to "गर्नु",
        "gareko"        to "गरेको",
        "gardai"        to "गर्दै",
        "garchhu"       to "गर्छु",
        "garchha"       to "गर्छ",
        "garne"         to "गर्ने",
        "bhayeko"       to "भएको",
        "bhayo"         to "भयो",
        "bhanne"        to "भन्ने",
        "bhaneko"       to "भनेको",
        "janchu"        to "जान्छु",
        "jancha"        to "जान्छ",
        "aauchu"        to "आउँछु",
        "aaucha"        to "आउँछ",
        "huncha"        to "हुन्छ",
        "hunchha"       to "हुन्छ",
        "hudaina"       to "हुँदैन",
        "gardaina"      to "गर्दैन",
        "khanchhu"      to "खान्छु",
        "khancha"       to "खान्छ",
        "dekhchhu"      to "देख्छु",
        "dekhchha"      to "देख्छ",
        "sunchu"        to "सुन्छु",
        "suncha"        to "सुन्छ",
        "bolchhu"       to "बोल्छु",
        "bolcha"        to "बोल्छ",
        "lekhchhu"      to "लेख्छु",
        "lekhchha"      to "लेख्छ",
        "padhchhu"      to "पढ्छु",
        "padhchha"      to "पढ्छ",

        // Common nouns
        "ghar"          to "घर",
        "maanche"       to "मान्छे",
        "manche"        to "मान्छे",
        "kehi"          to "केही",
        "kaha"          to "कहाँ",
        "kahaa"         to "कहाँ",
        "kasari"        to "कसरी",
        "kina"          to "किन",
        "paani"         to "पानी",
        "khana"         to "खाना",
        "naam"          to "नाम",
        "din"           to "दिन",
        "raat"          to "रात",
        "bihana"        to "बिहान",
        "saanjh"        to "साँझ",
        "saathi"        to "साथी",
        "dost"          to "दोस्त",
        "parivar"       to "परिवार",
        "parivaar"      to "परिवार",
        "desh"          to "देश",
        "shahar"        to "शहर",
        "gaun"          to "गाउँ",
        "pahad"         to "पहाड",
        "aakash"        to "आकाश",
        "aakaash"       to "आकाश",
        "surya"         to "सूर्य",
        "chandra"       to "चन्द्र",
        "tara"          to "तारा",
        "kitab"         to "किताब",
        "kitaab"        to "किताब",
        "kalam"         to "कलम",
        "skul"          to "स्कूल",
        "school"        to "स्कूल",
        "nadi"          to "नदी",

        // Adjectives
        "ramro"         to "राम्रो",
        "naramro"       to "नराम्रो",
        "thulo"         to "ठूलो",
        "saano"         to "सानो",
        "naya"          to "नयाँ",
        "purono"        to "पुरानो",
        "khushi"        to "खुशी",
        "dukhi"         to "दुखी",
        "thakeko"       to "थाकेको",
        "ramailo"       to "रमाइलो",
        "dherai"        to "धेरै",
        "thorai"        to "थोरै",
        "thikai"        to "ठिकै",

        // Location words
        "yaha"          to "यहाँ",
        "tyaha"         to "त्यहाँ",
        "mathi"         to "माथि",
        "tala"          to "तल",
        "bahira"        to "बाहिर",
        "bhitra"        to "भित्र",
        "agadi"         to "अगाडि",
        "agaadi"        to "अगाडि",
        "pachi"         to "पछि",

        // Misc common
        "ali"           to "अलि",
        "pheri"         to "फेरि",
        "chhaina"       to "छैन",
        "kasto"         to "कस्तो",
        "saMskrit"      to "संस्कृत",
        "aMgreji"       to "अंग्रेजी",
        "hiMdi"         to "हिंदी",
        "baMgaal"       to "बंगाल",
        "raMga"         to "रंग",
        "maMgal"        to "मंगल",
        "paMkha"        to "पंख",

        // Family relations
        "aama"          to "आमा",
        "buwa"          to "बुवा",
        "bua"           to "बुवा",
        "didi"          to "दिदी",
        "daju"          to "दाजु",
        "bhai"          to "भाइ",
        "bahini"        to "बहिनी",
        "babu"          to "बाबु",
        "nani"          to "नानी",
        "hajakur"       to "हजुर",
        "hajurbaa"      to "हजुरबा",
        "hajuraama"      to "हजुरआमा",
        "ram"           to "राम",
        "raam"          to "राम",
        "sita"          to "सीता",
        "siitaa"         to "सीता",
        "siita"         to "सीता",
        "sasura"        to "ससुरा",
        "saasu"         to "सासु",
        "jethaan"       to "जेठान",
        "dewar"         to "देवर",
        "bhauju"        to "भाउजु",
        "bhanja"        to "भान्जा",
        "bhanji"        to "भान्जी",
        "nati"          to "नाति",
        "natini"        to "नातिनी",
        "chhora"        to "छोरा",
        "chhori"        to "छोरी",
        "mamu"          to "मामु",
        "kaka"          to "काका",
        "kaki"          to "काकी",
        "mama"          to "मामा",
        "maiju"         to "माइजु",
        "fupu"          to "फुपू",
        "phupa"         to "फुपा",

        // Food
        "bhat"          to "भात",
        "roti"          to "रोटी",
        "dal"           to "दाल",
        "tarkari"       to "तरकारी",
        "achaar"        to "अचार",
        "dahi"          to "दही",
        "dudh"          to "दूध",
        "phal"          to "फल",
        "tarkara"       to "तरकारी",
        "masu"          to "मासु",
        "maachha"       to "माछा",
        "anda"          to "अण्डा",
        "aloo"          to "आलु",
        "saag"          to "साग",
        "chiura"        to "चिउरा",
        "sel"           to "सेल",
        "yomari"        to "योमरी",
        "dhedo"         to "ढेडो",
        "gundruk"       to "गुन्द्रुक",
        "sukuti"        to "सुकुटी",
        "chiya"         to "चिया",
        "kaafi"         to "कफी",
        "jus"           to "जुस",
        "pani"          to "पानी",
        "chamal"        to "चामल",
        "pitho"         to "पिठो",
        "noon"          to "नुन",
        "cheeni"        to "चिनी",
        "ghiu"          to "घिउ",
        "tel"           to "तेल",
        "lasun"         to "लसुन",
        "ada"           to "अदुवा",
        "khursaani"     to "खुर्सानी",
        "keraa"         to "केरा",
        "syaau"         to "स्याउ",
        "suntala"       to "सुन्तला",
        "mewa"          to "मेवा",
        "angur"         to "अंगुर",

        // Colors
        "raato"         to "रातो",
        "nilo"          to "नीलो",
        "pahelo"        to "पहेँलो",
        "hariyo"        to "हरियो",
        "kalo"          to "कालो",
        "seto"          to "सेतो",
        "suntali"       to "सुन्तली",
        "baigani"       to "बैजनी",
        "khैro"         to "खैरो",
        "khiro"         to "खैरो",
        "suntalo"       to "सुन्तली",
        "gulabi"        to "गुलाबी",

        // Body parts
        "aankhaa"       to "आँखा",
        "aankha"        to "आँखा",
        "kaan"          to "कान",
        "naak"          to "नाक",
        "muh"           to "मुख",
        "mukh"          to "मुख",
        "haath"         to "हात",
        "khutta"        to "खुट्टा",
        "tauko"         to "टाउको",
        "jiu"           to "जिउ",
        "pet"           to "पेट",
        "dhaad"         to "ढाड",
        "bhujo"         to "भुजो",
        "aauchhi"       to "औँठी",
        "angul"         to "औँला",

        // Common verbs (infinitive / conjugated forms)
        "khanu"         to "खानु",
        "pinu"          to "पिउनु",
        "aunu"          to "आउनु",
        "jaanu"         to "जानु",
        "basnu"         to "बस्नु",
        "uthnu"         to "उठ्नु",
        "sunu"          to "सुन्नु",
        "dekhnu"        to "देख्नु",
        "bolnu"         to "बोल्नु",
        "lekhnu"        to "लेख्नु",
        "padhnu"        to "पढ्नु",
        "gaunu"         to "गाउनु",
        "nachnu"        to "नाच्नु",
        "haasnu"        to "हाँस्नु",
        "runu"          to "रुनु",
        "sodhnu"        to "सोध्नु",
        "bhetnu"        to "भेट्नु",
        "dinu"          to "दिनु",
        "linu"          to "लिनु",
        "banaaunu"      to "बनाउनु",
        "bechnu"        to "बेच्नु",
        "kinnu"         to "किन्नु",
        "pahunchnu"     to "पुग्नु",
        "pugnu"         to "पुग्नु",
        "firnu"         to "फर्कनु",
        "rakhhnu"       to "राख्नु",
        "pathaaunu"     to "पठाउनु",
        "paaunu"        to "पाउनु",
        "bhulnu"        to "भुल्नु",
        "sochhnu"       to "सोच्नु",

        // Particles and connectors
        "ra"            to "र",
        "tara"          to "तर",
        "ki"            to "कि",
        "pani"          to "पनि",
        "nai"           to "नै",
        "chai"          to "चाहिँ",
        "bhane"         to "भने",
        "jasto"         to "जस्तो",
        "kasai"         to "कसैले",
        "kehi"          to "केही",
        "sabai"         to "सबै",
        "kohi"          to "कोही",
        "jasle"         to "जसले",
        "tyasle"        to "त्यसले",
        "yesle"         to "यसले",
        "aba"           to "अब",
        "phir"          to "फेरि",
        "afai"          to "आफै",
        "afno"          to "आफ्नो",
        "hami"          to "हामी",
        "vane"          to "भने",

        // Question words
        "ke"            to "के",
        "ko"            to "को",
        "kun"           to "कुन",
        "kati"          to "कति",
        "kahile"        to "कहिले",
        "kina"          to "किन",
        "kasari"        to "कसरी",

        // Common adjectives
        "naramilo"      to "नरमिलो",
        "majjako"       to "मज्जाको",
        "garibo"        to "गरिबो",
        "dhani"         to "धनी",
        "busho"         to "बुद्धो",
        "jawaan"        to "जवान",
        "budho"         to "बुढो",
        "yuwa"          to "युवा",
        "sushil"        to "सुशील",
        "sundar"        to "सुन्दर",
        "phurtiilo"     to "फुर्तिलो",
        "alchi"         to "अल्छी",
        "mehenatii"     to "मेहनती",
        "byasto"        to "व्यस्त",
        "khush"         to "खुश",
        "udaas"         to "उदास",
        "rog"           to "रोग",
        "tagaro"        to "तगारो",

        // Time words
        "aaja"          to "आज",
        "hijo"          to "हिजो",
        "bholi"         to "भोलि",
        "parsi"         to "पर्सि",
        "asti"          to "अस्ति",
        "sutia"         to "सुत्या",
        "pahile"        to "पहिले",
        "pachhi"        to "पछि",
        "abhi"          to "अहिले",
        "ahile"         to "अहिले",
        "samaaya"       to "समय",
        "samaya"        to "समय",
        "ghanta"        to "घण्टा",
        "minit"         to "मिनेट",
        "sekend"        to "सेकेन्ड",

        // Places / geography
        "bhaktapur"     to "भक्तपुर",
        "lalitpur"      to "ललितपुर",
        "janakpur"      to "जनकपुर",
        "butwal"        to "बुटवल",
        "biratnagar"    to "विराटनगर",
        "dharan"        to "धरान",
        "hetauda"       to "हेटौडा",
        "chitwan"       to "चितवन",
        "banke"         to "बाँके",
        "surkhet"       to "सुर्खेत",
        "dang"          to "दाङ",
        "mustang"       to "मुस्ताङ",
        "humla"         to "हुम्ला",
        "dolpa"         to "डोल्पा",

        // Technology / modern
        "fon"           to "फोन",
        "mobile"        to "मोबाइल",
        "computer"      to "कम्प्युटर",
        "internet"      to "इन्टरनेट",
        "facebook"      to "फेसबुक",
        "email"         to "इमेल",
        "message"       to "मेसेज",
        "photo"         to "फोटो",
        "video"         to "भिडियो",
        "news"          to "समाचार",
        "samachar"      to "समाचार",
        "khabar"        to "खबर",
        "sikshaa"       to "शिक्षा",
        "siksha"        to "शिक्षा",
        "kaam"          to "काम",
        "naukari"       to "नोकरी",
        "job"           to "जागिर",
        "jagir"         to "जागिर",
        "paisaa"        to "पैसा",
        "paisa"         to "पैसा",

        // Common phrases fragments
        "suru"          to "सुरु",
        "ant"           to "अन्त",
        "madhya"        to "मध्य",
        "raaj"          to "राज",
        "sarkar"        to "सरकार",
        "raajya"        to "राज्य",
        "prasashan"     to "प्रशासन",
        "kanun"         to "कानून",
        "adaalat"       to "अदालत",
        "shikhsha"      to "शिक्षा",
        "swasthya"      to "स्वास्थ्य",
        "ausadhi"       to "औषधि",
        "aspatal"       to "अस्पताल",
        "daktar"        to "डाक्टर",
        "nurse"         to "नर्स",
        "sainik"        to "सैनिक",
        "prahari"       to "प्रहरी",
    )

    // Words loaded from assets file at runtime
    private val fileWordMap = mutableMapOf<String, String>()

    // User-learned words are kept apart from asset words so that saving the user
    // dictionary does not copy the whole shipped asset into internal storage.
    private val userWordMap = mutableMapOf<String, String>()
    private val userFrequencies = mutableMapOf<String, Int>()
    private var userDirty = false

    // Tracks whether file has been loaded
    private var isFileLoaded = false
    private var loadError: String? = null

    // Cached union of built-in + asset + user words, rebuilt only when a source changes.
    private var mergedCache: Map<String, String>? = null

    // Roman keys with vowel length collapsed (aa->a, ii->i, uu->u) -> script word.
    // Backs the loose lookup below; rebuilt with mergedCache.
    private var normalizedCache: Map<String, List<String>>? = null

    private fun invalidateMerged() {
        mergedCache = null
        normalizedCache = null
    }

    /**
     * Collapses the long/short vowel distinction that Romanised input is inconsistent about:
     * users type "nepal" for "nepaal", "pani" for "paani". Applied to both the dictionary
     * keys and the query so the two meet in the middle.
     */
    private fun normalizeVowels(roman: String): String =
        roman.lowercase().replace("aa", "a").replace("ii", "i").replace("uu", "u")

    // One normalized form can map to several real words: in Nepali, vowel length is
    // meaningful -- "paani" (water) and "pani" (also) both normalize to "pani". So keep
    // every candidate and let the query pick the closest one rather than overwriting.
    private fun normalized(): Map<String, List<String>> = normalizedCache ?: buildMap<String, MutableList<String>> {
        merged().keys.forEach { roman -> getOrPut(normalizeVowels(roman)) { mutableListOf() }.add(roman) }
    }.also { normalizedCache = it }

    /** Levenshtein distance, only ever run on a lookup miss over short words. */
    private fun editDistance(a: String, b: String): Int {
        if (a == b) return 0
        var prev = IntArray(b.length + 1) { it }
        val curr = IntArray(b.length + 1)
        for (i in 1..a.length) {
            curr[0] = i
            for (j in 1..b.length) {
                val sub = prev[j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = minOf(curr[j - 1] + 1, prev[j] + 1, sub)
            }
            prev = curr.copyOf()
        }
        return prev[b.length]
    }

    private fun merged(): Map<String, String> = mergedCache ?: buildMap {
        putAll(builtInWordMap)
        putAll(fileWordMap)
        putAll(userWordMap)
    }.also { mergedCache = it }

    /**
     * Load words from assets/nepali_words.txt
     * File format — one entry per line:
     *   nepal=नेपाल
     *   ghar=घर
     *
     * Lines starting with # are treated as comments.
     * Blank lines are ignored.
     * Duplicate keys from file OVERRIDE built-in map.
     *
     * Call this once from Application.onCreate() or LatinIME.onCreate()
     */
    @JvmStatic
    @JvmOverloads
    fun loadFromAssets(context: Context, fileName: String = "nepali_words.txt") {
        if (isFileLoaded) return
        try {
            var loadedCount = 0
            var skippedCount = 0

            context.assets.open(fileName).bufferedReader().forEachLine { rawLine ->
                val line = rawLine.trim()
                when {
                    line.isEmpty()          -> { /* skip blank lines */ }
                    line.startsWith("#")    -> { /* skip comments */ }
                    line.contains("=")      -> {
                        val eqIndex = line.indexOf("=")
                        val roman = line.substring(0, eqIndex).trim().lowercase()
                        val rest  = line.substring(eqIndex + 1).trim()
                        // Support optional frequency suffix: roman=देवनागरी:200
                        val colonIdx = rest.lastIndexOf(':')
                        val (devanagari, freq) =
                            if (colonIdx > 0 && rest.substring(colonIdx + 1).all { it.isDigit() })
                                rest.substring(0, colonIdx).trim() to rest.substring(colonIdx + 1).toInt()
                            else
                                rest to FILE_DEFAULT_FREQUENCY
                        if (roman.isNotEmpty() && devanagari.isNotEmpty()) {
                            fileWordMap[roman] = devanagari
                            fileFrequencies[roman] = freq
                            loadedCount++
                        } else {
                            skippedCount++
                        }
                    }
                    else -> skippedCount++ // malformed line
                }
            }

            isFileLoaded = true
            invalidateMerged()
            android.util.Log.d("NepaliDict",
                "Loaded $loadedCount words from $fileName, skipped $skippedCount lines")

        } catch (e: java.io.FileNotFoundException) {
            loadError = "File not found: $fileName"
            android.util.Log.w("NepaliDict", loadError!!)
            isFileLoaded = true // mark as attempted so we don't retry
        } catch (e: Exception) {
            loadError = "Error loading $fileName: ${e.message}"
            android.util.Log.e("NepaliDict", loadError!!, e)
            isFileLoaded = true
        }
    }


    /**
     * Look up a word with common Romanization variations (e.g. a instead of aa).
     */
    fun lookupLoose(roman: String): String? {
        lookup(roman)?.let { return it }
        // Fall back to a vowel-length-insensitive match. The previous implementation did
        // replace("a", "aa") on the whole word, which turned "kathmandu" into
        // "kaathmaandu" and matched nothing; only single-'a' words ever worked.
        val low = roman.lowercase()
        val candidates = normalized()[normalizeVowels(roman)] ?: return null
        // Closest spelling wins, so "paanii" resolves to "paani" and not to "pani".
        // Frequency breaks ties.
        val best = candidates.minWithOrNull(
            compareBy({ editDistance(low, it) }, { -getFrequency(it) }, { it })
        ) ?: return null
        return lookup(best)
    }

    /**
     * Lookup priority:
     * 1. File words (override built-in if same key)
     * 2. Built-in words
     * 3. null if not found
     */
    fun lookup(romanWord: String): String? {
        val lower = romanWord.trim().lowercase()
        return userWordMap[lower]
            ?: fileWordMap[lower]
            ?: builtInWordMap[lower]
            ?: userWordMap[romanWord]      // try original case too
            ?: fileWordMap[romanWord]
            ?: builtInWordMap[romanWord]
    }

    fun hasDictionaryEntry(romanWord: String): Boolean = lookup(romanWord) != null

    /** Frequency for a roman key — file overrides built-in, default if unknown. */
    fun getFrequency(roman: String): Int {
        val lower = roman.trim().lowercase()
        return userFrequencies[lower] ?: fileFrequencies[lower] ?: builtInFrequencies[lower] ?: DEFAULT_FREQUENCY
    }

    /**
     * Get all entries that start with the given prefix, sorted by frequency descending.
     * Returns (roman, devanagari) pairs.
     */
    fun getPrefixMatches(prefix: String, maxResults: Int = 30): List<Pair<String, String>> {
        return getPrefixMatchesWithFrequency(prefix, maxResults).map { (r, d, _) -> Pair(r, d) }
    }

    /**
     * Like getPrefixMatches but also returns the frequency as the third element.
     * Sorted by frequency descending, then key length, then alphabetically.
     */
    fun getPrefixMatchesWithFrequency(prefix: String, maxResults: Int = 30): List<Triple<String, String, Int>> {
        val lower = prefix.lowercase()
        // Resolve the frequency once per candidate instead of twice per comparison:
        // the old comparator called getFrequency() inside sortedWith, so an n-entry
        // prefix match did O(n log n) map lookups on every keystroke.
        return merged().entries
            .filter { it.key.startsWith(lower) }
            .map { Triple(it.key, it.value, getFrequency(it.key)) }
            .sortedWith(
                compareByDescending<Triple<String, String, Int>> { it.third }
                    .thenBy { it.first.length }
                    .thenBy { it.first }
            )
            .take(maxResults)
    }

    /** Total word count across both maps (deduped) */
    fun totalWordCount(): Int = merged().size

    /** Whether file was loaded successfully */
    fun isLoaded(): Boolean = isFileLoaded

    /** Any error from file loading */
    fun getLoadError(): String? = loadError

    /** Add a word at runtime (e.g. user-learned word). Persisted by [saveIfDirty]. */
    fun addWord(roman: String, devanagari: String, frequency: Int = FILE_DEFAULT_FREQUENCY) {
        val lower = roman.lowercase()
        if (userWordMap[lower] == devanagari && userFrequencies[lower] == frequency) return
        userWordMap[lower] = devanagari
        userFrequencies[lower] = frequency
        userDirty = true
        invalidateMerged()
    }

    /** Remove a user-learned word (asset and built-in entries cannot be removed) */
    fun removeWord(roman: String) {
        val lower = roman.lowercase()
        if (userWordMap.remove(lower) != null) {
            userFrequencies.remove(lower)
            userDirty = true
            invalidateMerged()
        }
    }

    /** Writes the user dictionary only if something was learned since the last save. */
    @JvmStatic
    fun saveIfDirty(context: Context) {
        if (userDirty) saveToInternalStorage(context)
    }

    /**
     * Save current file words back to internal storage
     * (so user-added words persist across sessions)
     */
    fun saveToInternalStorage(context: Context, fileName: String = "nepali_words_user.txt") {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
                writer.write("# Nepali user dictionary — words learned from corrections\n")
                writer.write("# Format: roman=देवनागरी:frequency\n\n")
                userWordMap.entries
                    .sortedBy { it.key }
                    .forEach { (roman, devanagari) ->
                        val freq = userFrequencies[roman] ?: FILE_DEFAULT_FREQUENCY
                        writer.write("$roman=$devanagari:$freq\n")
                    }
            }
            userDirty = false
            android.util.Log.d("NepaliDict", "Saved ${userWordMap.size} user words to $fileName")
        } catch (e: Exception) {
            android.util.Log.e("NepaliDict", "Error saving dictionary: ${e.message}", e)
        }
    }

    /**
     * Load from internal storage (user-saved words)
     * Call this alongside loadFromAssets() for full coverage
     */
    @JvmStatic
    @JvmOverloads
    fun loadFromInternalStorage(context: Context, fileName: String = "nepali_words_user.txt") {
        try {
            context.openFileInput(fileName).bufferedReader().forEachLine { rawLine ->
                val line = rawLine.trim()
                if (line.isNotEmpty() && !line.startsWith("#") && line.contains("=")) {
                    val eqIndex = line.indexOf("=")
                    val roman = line.substring(0, eqIndex).trim().lowercase()
                    val rest  = line.substring(eqIndex + 1).trim()
                    val colonIdx = rest.lastIndexOf(':')
                    val (devanagari, freq) =
                        if (colonIdx > 0 && rest.substring(colonIdx + 1).all { it.isDigit() })
                            rest.substring(0, colonIdx).trim() to rest.substring(colonIdx + 1).toInt()
                        else
                            rest to FILE_DEFAULT_FREQUENCY
                    if (roman.isNotEmpty() && devanagari.isNotEmpty()) {
                        userWordMap[roman] = devanagari
                        userFrequencies[roman] = freq
                    }
                }
            }
            invalidateMerged()
            userDirty = false
            android.util.Log.d("NepaliDict", "Loaded ${userWordMap.size} user words from $fileName")
        } catch (e: java.io.FileNotFoundException) {
            // No user file yet — that's fine
        } catch (e: Exception) {
            android.util.Log.e("NepaliDict", "Error loading user dictionary: ${e.message}", e)
        }
    }
}
