package com.example.data

enum class SearchCategory(val key: String, val labelUz: String, val labelEn: String, val labelRu: String) {
    ALL("all", "Barchasi", "All", "Все"),
    GENERAL("general", "Umumiy", "General", "Общие"),
    PRIVACY("privacy", "Maxfiylik", "Privacy", "Приватность"),
    AI_BASED("ai", "Intelektual (AI)", "AI Search", "ИИ-Поиск"),
    DEV("dev", "Dasturchilar", "Developers", "Разработчикам"),
    REGIONAL("regional", "Mahalliy/CIS", "Regional/CIS", "Региональные")
}

data class SearchEngine(
    val id: String,
    val name: String,
    val category: SearchCategory,
    val globalRank: Int,
    val speedIndex: Float, // out of 10.0
    val privacyIndex: Float, // out of 10.0
    val databaseIndex: Float, // out of 10.0
    val searchUrlTemplate: String,
    val homeUrl: String,
    val descriptionUz: String,
    val descriptionEn: String,
    val descriptionRu: String,
    val iconPlaceholderLetter: Char
) {
    fun localizedDescription(languageCode: String): String {
        return when (languageCode) {
            "uz" -> descriptionUz
            "ru" -> descriptionRu
            else -> descriptionEn
        }
    }
}

object SearchEngineProvider {
    val engines = listOf(
        SearchEngine(
            id = "google",
            name = "Google",
            category = SearchCategory.GENERAL,
            globalRank = 1,
            speedIndex = 9.8f,
            privacyIndex = 4.5f,
            databaseIndex = 10.0f,
            searchUrlTemplate = "https://www.google.com/search?q=%s",
            homeUrl = "https://www.google.com",
            descriptionUz = "Dunyodagi eng katta va eng mashhur qidiruv tizimi. Yuqori tezlik va aniqlik.",
            descriptionEn = "The world's largest and most popular search engine. Unmatched speed and accuracy.",
            descriptionRu = "Крупнейшая и самая популярная поисковая система в мире. Непревзойденная скорость.",
            iconPlaceholderLetter = 'G'
        ),
        SearchEngine(
            id = "bing",
            name = "Bing",
            category = SearchCategory.GENERAL,
            globalRank = 2,
            speedIndex = 9.2f,
            privacyIndex = 5.0f,
            databaseIndex = 9.4f,
            searchUrlTemplate = "https://www.bing.com/search?q=%s",
            homeUrl = "https://www.bing.com",
            descriptionUz = "Microsoft tomonidan taqdim etilgan qidiruv tizimi. Rasmlar va korporativ qidiruv uchun qulay.",
            descriptionEn = "Search engine provided by Microsoft. Excellent image search and Microsoft integration.",
            descriptionRu = "Поисковая система от Microsoft. Отличный поиск изображений и интеграция.",
            iconPlaceholderLetter = 'B'
        ),
        SearchEngine(
            id = "duckduckgo",
            name = "DuckDuckGo",
            category = SearchCategory.PRIVACY,
            globalRank = 4,
            speedIndex = 8.9f,
            privacyIndex = 10.0f,
            databaseIndex = 8.5f,
            searchUrlTemplate = "https://duckduckgo.com/?q=%s",
            homeUrl = "https://duckduckgo.com",
            descriptionUz = "Shaxsiy ma'lumotlarni kuzatishdan himoya qiluvchi eng mashhur maxfiy qidiruv tizimi.",
            descriptionEn = "The most popular privacy-focused search engine that blocks trackers and advertising cookies.",
            descriptionRu = "Самая популярная поисковая система, ориентированная на конфиденциальность.",
            iconPlaceholderLetter = 'D'
        ),
        SearchEngine(
            id = "yandex",
            name = "Yandex",
            category = SearchCategory.REGIONAL,
            globalRank = 7,
            speedIndex = 9.4f,
            privacyIndex = 4.0f,
            databaseIndex = 9.1f,
            searchUrlTemplate = "https://yandex.com/search/?text=%s",
            homeUrl = "https://yandex.com",
            descriptionUz = "Rossiya va MDH davlatlarida eng ommabop qidiruv tizimi. Kuchli lokalizatsiya.",
            descriptionEn = "The most popular search engine in Russia and CIS countries. Superb localization.",
            descriptionRu = "Самая популярная поисковая система в России и странах СНГ. Великолепная локализация.",
            iconPlaceholderLetter = 'Y'
        ),
        SearchEngine(
            id = "phind",
            name = "Phind",
            category = SearchCategory.DEV,
            globalRank = 10,
            speedIndex = 8.5f,
            privacyIndex = 8.0f,
            databaseIndex = 8.0f,
            searchUrlTemplate = "https://www.phind.com/search?q=%s",
            homeUrl = "https://www.phind.com",
            descriptionUz = "Dasturchilar uchun maxsus AI qidiruv tizimi. Kodlar va texnik savollarga bevosita javob beradi.",
            descriptionEn = "An AI search engine specialized for developers. Directly answers technical questions with code.",
            descriptionRu = "Поисковая система ИИ для разработчиков. Напрямую отвечает на технические вопросы кодом.",
            iconPlaceholderLetter = 'P'
        ),
        SearchEngine(
            id = "startpage",
            name = "StartPage",
            category = SearchCategory.PRIVACY,
            globalRank = 8,
            speedIndex = 9.0f,
            privacyIndex = 9.5f,
            databaseIndex = 9.8f,
            searchUrlTemplate = "https://www.startpage.com/sp/search?query=%s",
            homeUrl = "https://www.startpage.com",
            descriptionUz = "Google qidiruv natijalarini to'liq maxfiylik va kuzatuvlarsiz taqdim etuvchi xizmat.",
            descriptionEn = "Delivers premium Google search results with total, certified privacy protection.",
            descriptionRu = "Предоставляет премиальные результаты поиска Google с полной конфиденциальностью.",
            iconPlaceholderLetter = 'S'
        ),
        SearchEngine(
            id = "ecosia",
            name = "Ecosia",
            category = SearchCategory.GENERAL,
            globalRank = 12,
            speedIndex = 8.8f,
            privacyIndex = 8.0f,
            databaseIndex = 8.3f,
            searchUrlTemplate = "https://www.ecosia.org/search?q=%s",
            homeUrl = "https://www.ecosia.org",
            descriptionUz = "Qidiruvlardan tushgan daromadlarni butun dunyoda daraxtlar ekishga sarflaydigan ekologik tizim.",
            descriptionEn = "An eco-friendly search engine that spends its profits planting trees around the globe.",
            descriptionRu = "Экологичная поисковая система, тратящая прибыль на посадку деревьев.",
            iconPlaceholderLetter = 'E'
        ),
        SearchEngine(
            id = "brave",
            name = "Brave Search",
            category = SearchCategory.PRIVACY,
            globalRank = 9,
            speedIndex = 9.1f,
            privacyIndex = 9.8f,
            databaseIndex = 8.7f,
            searchUrlTemplate = "https://search.brave.com/search?q=%s",
            homeUrl = "https://search.brave.com",
            descriptionUz = "Brave brauzeri ishlab chiquvchilarining mustaqil va to'liq reklamasiz maxfiy qidiruv tizimi.",
            descriptionEn = "An independent, tracker-free private search engine built on its own web index.",
            descriptionRu = "Независимая поисковая система без рекламы и трекеров от создателей Brave.",
            iconPlaceholderLetter = 'A'
        ),
        SearchEngine(
            id = "wolfram",
            name = "WolframAlpha",
            category = SearchCategory.DEV,
            globalRank = 15,
            speedIndex = 8.2f,
            privacyIndex = 9.0f,
            databaseIndex = 7.9f,
            searchUrlTemplate = "https://www.wolframalpha.com/input?i=%s",
            homeUrl = "https://www.wolframalpha.com",
            descriptionUz = "Matematik, ilmiy va hisoblash savollariga faktik javob beruvchi noyob bilim dvigateli.",
            descriptionEn = "A unique computational knowledge engine providing structured mathematical and scientific answers.",
            descriptionRu = "Уникальный вычислительный движок знаний, дающий структурированные ответы.",
            iconPlaceholderLetter = 'W'
        ),
        SearchEngine(
            id = "ziyonet",
            name = "ZiyoNet",
            category = SearchCategory.REGIONAL,
            globalRank = 22,
            speedIndex = 7.5f,
            privacyIndex = 8.5f,
            databaseIndex = 6.0f,
            searchUrlTemplate = "http://ziyonet.uz/uz/search?text=%s",
            homeUrl = "http://ziyonet.uz",
            descriptionUz = "O'zbekiston milliy ma'rifiy-ta'limiy portalining qidiruv qismi. Yoshlar uchun mo'ljallangan.",
            descriptionEn = "Search portion of the Uzbekistan national educational and informative network for young learners.",
            descriptionRu = "Поисковая часть национального образовательного портала Узбекистана ZiyoNet.",
            iconPlaceholderLetter = 'Z'
        ),
        SearchEngine(
            id = "you",
            name = "You.com",
            category = SearchCategory.AI_BASED,
            globalRank = 14,
            speedIndex = 8.4f,
            privacyIndex = 8.5f,
            databaseIndex = 8.2f,
            searchUrlTemplate = "https://you.com/search?q=%s",
            homeUrl = "https://you.com",
            descriptionUz = "Suhbatdosh qidiruv interfeysiga ega va ma'lumotlarni saralovchi sun'iy idrok tizimi.",
            descriptionEn = "A conversational search assistant powered by AI for personalized queries.",
            descriptionRu = "Инновационный ИИ-поиск с разговорным ассистентом для персонализированных запросов.",
            iconPlaceholderLetter = 'U'
        ),
        SearchEngine(
            id = "github",
            name = "GitHub Search",
            category = SearchCategory.DEV,
            globalRank = 11,
            speedIndex = 9.0f,
            privacyIndex = 8.0f,
            databaseIndex = 9.0f,
            searchUrlTemplate = "https://github.com/search?q=%s",
            homeUrl = "https://github.com",
            descriptionUz = "Butun dunyo bo'ylab ochiq kodli loyihalar, repozitoriylar va dasturchilarni qidiruv platformasi.",
            descriptionEn = "Search across public software repositories, code blocks, and developers globally.",
            descriptionRu = "Поиск по публичным репозиториям программного обеспечения, коду и разработчикам.",
            iconPlaceholderLetter = 'H'
        )
    )
}
