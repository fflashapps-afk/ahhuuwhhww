package com.example.data.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class WebResult(
    val title: String,
    val snippet: String,
    val url: String
)

data class UnifiedSearchResponse(
    val aiTrendHeading: String,
    val aiSummary: String,
    val results: List<WebResult>,
    val error: String? = null
)

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun searchWeb(
        query: String,
        language: String,
        isSafeSearch: Boolean
    ): UnifiedSearchResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext UnifiedSearchResponse(
                aiTrendHeading = "Loyiha Kaliti Topilmadi (No Key)",
                aiSummary = "Iltimos, Google AI Studio-da o'zingizning shaxsiy GEMINI_API_KEY kalitingizni sozlang. Hozirda namoyish qilish rejimi (Demo Mode) ishlamoqda.",
                results = getFallbackResults(query, language)
            )
        }

        val systemLanguagePrompt = when (language) {
            "uz" -> "Javoblarni va ma'lumotlarni o'zbek tilida taqdim et. Tizim nomi: CLL SHRPV 1.0"
            "ru" -> "Предоставляй ответы и информацию на русском языке. Название системы: CLL SHRPV 1.0"
            else -> "Provide all answers and search snippets strictly in English. System name: CLL SHRPV 1.0"
        }

        val safeSearchPrompt = if (isSafeSearch) {
            "Strictly filter out any explicit, adult, illegal, extreme, or unsafe contents from your output summaries and results list."
        } else {
            "Standard safe filtering."
        }

        val prompt = """
            You are the core engine of CLL SHRPV 1.0, an advanced search tool and global search aggregator. 
            Act as an intelligent agent performing live index queries on the web.
            Execute search indexing for this query: "$query".
            
            $systemLanguagePrompt
            $safeSearchPrompt
            
            Return your response strictly as a single raw JSON object. 
            Do NOT warp your response in markdown tags (such as ```json ... ```), write only valid JSON.
            The JSON MUST conform to this exact schema structure:
            {
              "aiTrendHeading": "A cool short trending headline summarizing the topic in the selected language",
              "aiSummary": "A highly detailed, sophisticated, Perplexity-style AI compiled answer summarizing the findings with rich structure and formatted points. Keep it highly relevant and accurate.",
              "results": [
                {
                  "title": "Clean concise webpage title for search result",
                  "snippet": "A rich 2-3 sentence snippet detailing exactly what the user is looking for on this site",
                  "url": "https://example.com/some-relevant-url"
                }
              ]
            }
            
            Provide at least 4 realistic high-quality search results corresponding to the query. Make sure the URLs look valid and point to major platforms (like Wikipedia, GitHub, StackOverflow, Medium, BBC, Kun.uz for Uzbekistan topics, etc.) related to the subject.
        """.trimIndent()

        // Construct Request Body JSON using standard JSONObject
        val contentsArray = JSONArray().put(
            JSONObject().put(
                "parts", JSONArray().put(
                    JSONObject().put("text", prompt)
                )
            )
        )
        val requestJson = JSONObject().put("contents", contentsArray)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val url = "$BASE_URL?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                Log.e(TAG, "API call failed with code ${response.code}: $responseBodyString")
                return@withContext UnifiedSearchResponse(
                    aiTrendHeading = "Aloqa Xatoligi / Connection Problem",
                    aiSummary = "API tarmoq so'rovida xatolik yuz berdi (${response.code}). CLL SHRPV indekslari mahalliy rejimga o'tkazildi.",
                    results = getFallbackResults(query, language),
                    error = "HTTP ${response.code}"
                )
            }

            val jsonObject = JSONObject(responseBodyString)
            val candidates = jsonObject.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            var rawText = parts?.optJSONObject(0)?.optString("text")?.trim() ?: ""

            // Strip markdown block symbols if returned
            if (rawText.startsWith("```json")) {
                rawText = rawText.removePrefix("```json")
            }
            if (rawText.startsWith("```")) {
                rawText = rawText.removePrefix("```")
            }
            if (rawText.endsWith("```")) {
                rawText = rawText.removeSuffix("```")
            }
            rawText = rawText.trim()

            val parsedJson = JSONObject(rawText)
            val aiTrendHeading = parsedJson.optString("aiTrendHeading", "Trending News")
            val aiSummary = parsedJson.optString("aiSummary", "")
            val resultsArray = parsedJson.optJSONArray("results") ?: JSONArray()
            val resultsList = mutableListOf<WebResult>()

            for (i in 0 until resultsArray.length()) {
                val item = resultsArray.getJSONObject(i)
                resultsList.add(
                    WebResult(
                        title = item.optString("title", "Result"),
                        snippet = item.optString("snippet", ""),
                        url = item.optString("url", "#")
                    )
                )
            }

            UnifiedSearchResponse(
                aiTrendHeading = aiTrendHeading,
                aiSummary = aiSummary,
                results = resultsList
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error performing search", e)
            UnifiedSearchResponse(
                aiTrendHeading = "Autonom qidiruv rejimi / Offline Mode",
                aiSummary = "Global qidiruv so'rovingiz tahlil qilindi. Tizim internetga yuklana olmaganligi sababli, CLL SHRPV 1.0 avtonom intellektual natijalarini ko'rsatmoqda. Xatolik: ${e.localizedMessage}",
                results = getFallbackResults(query, language),
                error = e.localizedMessage
            )
        }
    }

    private fun getFallbackResults(query: String, language: String): List<WebResult> {
        val cleanQuery = query.lowercase().trim()
        val results = mutableListOf<WebResult>()

        when (language) {
            "uz" -> {
                results.add(
                    WebResult(
                        title = "Wikipedia - '$query' haqida to'liq ma'lumotlar",
                        snippet = "CLL SHRPV 1.0 global ensiklopediya indeksi orqali '$query' bo'yicha eng muhim tushunchalar va ilmiy maqolalar to'plami. Tarixi, ta'rifi va batafsil tahlili.",
                        url = "https://uz.wikipedia.org/wiki/Search?search=$query"
                    )
                )
                results.add(
                    WebResult(
                        title = "Kun.uz - '$query' so'roviga doir so'nggi yangiliklar",
                        snippet = "O'zbekiston va jahonda yuz berayotgan dolzarb xabarlar hamda '$query' mavzusi bo'yicha intervyular, o'rganishlar va siyosiy tahlillar.",
                        url = "https://kun.uz/search?q=$query"
                    )
                )
                results.add(
                    WebResult(
                        title = "ZiyoNet - Ta'limiy resurslar va darsliklar",
                        snippet = "'$query' atamasi asosida O'zbekiston Milliy Ta'lim Tarmog'ida chop etilgan slaydlar, referatlar, dars soatlari konspektlari va ilmiy loyihalar.",
                        url = "http://ziyonet.uz/uz/search?text=$query"
                    )
                )
                results.add(
                    WebResult(
                        title = "GitHub - Ochiq kodli dasturiy ta'minotlar",
                        snippet = "'$query' kalit so'zi ostida yaratilgan ochiq manbali kutubxonalar, loyihalar va Kotlin/Jetpack professional namunalari jadvali.",
                        url = "https://github.com/search?q=$query"
                    )
                )
            }
            "ru" -> {
                results.add(
                    WebResult(
                        title = "Википедия - энциклопедия информации о '$query'",
                        snippet = "Подробная историческая справка, определение и научные факты по запросу '$query' на русском поисковом зеркале CLL SHRPV.",
                        url = "https://ru.wikipedia.org/wiki/Search?search=$query"
                    )
                )
                results.add(
                    WebResult(
                        title = "Хабр (Habr) - научно-технические блоги",
                        snippet = "Публикации, разборы кейсов, опыт разработчиков и новейшие алгоритмы, связанные прямо с контекстом '$query'.",
                        url = "https://habr.com/ru/search/?q=$query"
                    )
                )
                results.add(
                    WebResult(
                        title = "Яндекс Поиск - лучшие результаты",
                        snippet = "Найденные веб-страницы, медиаматериалы и региональные сниппеты для '$query' по алгоритму умного ранжирования.",
                        url = "https://yandex.ru/search/?text=$query"
                    )
                )
            }
            else -> {
                results.add(
                    WebResult(
                        title = "Wikipedia - All about '$query'",
                        snippet = "Discover definitions, dynamic timeline history, structural models, and global references about '$query' updated in real-time.",
                        url = "https://en.wikipedia.org/wiki/Search?search=$query"
                    )
                )
                results.add(
                    WebResult(
                        title = "Medium - Analysis & Personal Stories on '$query'",
                        snippet = "Professional evaluations, in-depth reports, and tutorials written by global industry practitioners about '$query'.",
                        url = "https://medium.com/search?q=$query"
                    )
                )
                results.add(
                    WebResult(
                        title = "GitHub Open Source Registry",
                        snippet = "Developer repositories, automation scripts, and documentation code assets revolving around '$query'.",
                        url = "https://github.com/search?q=$query"
                    )
                )
            }
        }
        return results
    }
}
