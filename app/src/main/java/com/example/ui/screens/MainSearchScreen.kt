package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.SearchCategory
import com.example.data.SearchEngine
import com.example.data.network.WebResult
import com.example.data.network.UnifiedSearchResponse
import com.example.ui.viewmodel.SearchUiState
import com.example.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Localized translation dictionaries
object Translations {
    private val dict = mapOf(
        "uz" to mapOf(
            "tab_search" to "Global Qidiruv",
            "tab_directory" to "Tizimlar Katalogi",
            "tab_companion" to "AI Maslahatchi",
            "search_placeholder" to "Qidirish yoki so'rov kiriting...",
            "search_btn" to "Qidirish",
            "ranking_title" to "Reyting & Jonli Indekslar",
            "speed" to "Qidiruv Tezligi",
            "privacy" to "Maxfiylik Darajasi",
            "db_size" to "Indeks Baza Hajmi",
            "global_rank" to "Jahon Reytingi",
            "category" to "Turkum",
            "sort_by" to "Saralash",
            "history" to "Qidiruvlar Tarixi",
            "bookmarks" to "Tanlangan Tizimlar",
            "settings" to "Sozlamalar",
            "dark_mode" to "Qorong'u Mavzu",
            "light_mode" to "Yorug' Mavzu",
            "system_mode" to "Tizim Sozlamasi",
            "safe_search" to "Xavfsiz Qidiruv (SafeSearch)",
            "layout_style" to "Katalog Dizayni",
            "llm_analysis" to "AI Tahlili (CLL SGE)",
            "show_results" to "Global Indeksdagi Natijalar",
            "bookmarks_only" to "Faqat Sevimlilar",
            "voice_title" to "Ovozli qidiruv tinglanmoqda...",
            "voice_tip" to "Suhbatlashish orqali tezkor qidiruvni amalga oshiring",
            "voice_demo" to "Tavsiya so'rovlar: 'O'zbekiston yutuqlari', 'Kotlin dasturlash yangiliklari'",
            "companion_tip" to "CLL SHRPV AI Maslahatchisi qidiruv so'rovlarini tozalaydi, optimallashtiradi va kalit so'zlar shakllantiradi.",
            "source" to "Manzil",
            "empty_history" to "Qidiruvlar tarixi hali bo'sh",
            "clear_all" to "Barchasini tozalash",
            "empty_results" to "Natijalar yo'q. Qidiruv boshlash uchun yuqoriga so'rov yozing.",
            "additional_features" to "Qo'shimcha Imkoniyatlar",
            "weather_forecast" to "Tezkor Ob-Havo (Toshkent)",
            "weather_temp" to "Toshkent: +31°C, Ochiq osmon",
            "trends" to "Kunlik Ommabop Mavzular",
            "about" to "Dastur Haqida",
            "about_desc" to "CLL SHRPV 1.0 — Global va xavfsiz qismlarga bo'lingan ilg'or qidiruv ekotizimi.",
            "search_suggest_1" to "Sun'iy intellekt yangiliklari 2026",
            "search_suggest_2" to "Jetpack Compose eng zo'r animatsiyalar",
            "search_suggest_3" to "O'zbekistonda startap loyihalar",
            "search_suggest_4" to "Dunyodagi eng tezkor qidiruv tizimlari"
        ),
        "en" to mapOf(
            "tab_search" to "Global Search",
            "tab_directory" to "Web Directory",
            "tab_companion" to "AI Companion",
            "search_placeholder" to "Search or enter query...",
            "search_btn" to "Search",
            "ranking_title" to "Ratings & Live Indices",
            "speed" to "Search Speed",
            "privacy" to "Privacy Level",
            "db_size" to "Index Database Size",
            "global_rank" to "Global Rank",
            "category" to "Category",
            "sort_by" to "Sort By",
            "history" to "Search History",
            "bookmarks" to "Bookmarked Engines",
            "settings" to "Settings",
            "dark_mode" to "Dark Theme",
            "light_mode" to "Light Theme",
            "system_mode" to "System Settings",
            "safe_search" to "Safe Search (SafeSearch)",
            "layout_style" to "Directory Layout",
            "llm_analysis" to "AI SGE Summary Analysis",
            "show_results" to "Global Web Index Matches",
            "bookmarks_only" to "Favorites Only",
            "voice_title" to "Listening to voice query...",
            "voice_tip" to "Talk to search global databases instantly",
            "voice_demo" to "Try: 'Global warm index details', 'Modern Kotlin features'",
            "companion_tip" to "CLL SHRPV AI Companion cleans, expands, and builds relevant query phrasing.",
            "source" to "Source",
            "empty_history" to "Search history is currently empty",
            "clear_all" to "Clear All",
            "empty_results" to "No results. Type a query above to explore the web.",
            "additional_features" to "Advanced Features",
            "weather_forecast" to "Quick Weather (Tashkent)",
            "weather_temp" to "Toshkent: +31°C, Sunny Sky",
            "trends" to "Global Search Trends",
            "about" to "About Application",
            "about_desc" to "CLL SHRPV 1.0 — Advanced dual-mode search aggregator & meta-search catalog.",
            "search_suggest_1" to "Artificial Intelligence news 2026",
            "search_suggest_2" to "Jetpack Compose best practices",
            "search_suggest_3" to "Startups in Central Asia",
            "search_suggest_4" to "Secured search tracking protocols"
        ),
        "ru" to mapOf(
            "tab_search" to "Глобальный Поиск",
            "tab_directory" to "Каталог Сетей",
            "tab_companion" to "ИИ Помощник",
            "search_placeholder" to "Поиск или ввод запроса...",
            "search_btn" to "Искать",
            "ranking_title" to "Рейтинги и Индексы",
            "speed" to "Скорость Поиска",
            "privacy" to "Приватность Данных",
            "db_size" to "Размер Индекс-Базы",
            "global_rank" to "Мировой Ранг",
            "category" to "Категория",
            "sort_by" to "Сортировка",
            "history" to "История Поисков",
            "bookmarks" to "Избранные Системы",
            "settings" to "Настройки",
            "dark_mode" to "Темная Тема",
            "light_mode" to "Светлая Тема",
            "system_mode" to "Системные Настройки",
            "safe_search" to "Безопасный Поиск (SafeSearch)",
            "layout_style" to "Вид Каталога",
            "llm_analysis" to "ИИ Аналитика (CLL SGE)",
            "show_results" to "Результаты из Глобального Индекса",
            "bookmarks_only" to "Только Избранное",
            "voice_title" to "Слушаю голосовой запрос...",
            "voice_tip" to "Говорите для мгновенного поиска по базам данных",
            "voice_demo" to "Примеры: 'достижения Узбекистана', 'события в сфере IT'",
            "companion_tip" to "ИИ Помощник CLL SHRPV корректирует, анализирует и формулирует точные поисковые комбинации.",
            "source" to "Источник",
            "empty_history" to "История поиска пока пуста",
            "clear_all" to "Очистить все",
            "empty_results" to "Нет результатов. Введите запрос выше для поиска.",
            "additional_features" to "Дополнительные функции",
            "weather_forecast" to "Быстрая Погода (Ташкент)",
            "weather_temp" to "Ташкент: +31°C, Ясно",
            "trends" to "Популярные Тренды Дня",
            "about" to "О программе",
            "about_desc" to "CLL SHRPV 1.0 — Интеллектуальный поисковый агрегатор и каталог живых рейтингов.",
            "search_suggest_1" to "Новости искусственного интеллекта 2026",
            "search_suggest_2" to "Jetpack Compose лучшие практики анимации",
            "search_suggest_3" to "Стартап-проекты в Ташкенте",
            "search_suggest_4" to "Безопасные протоколы обмена данными"
        )
    )

    fun get(key: String, lang: String): String {
        return dict[lang]?.get(key) ?: dict["en"]?.get(key) ?: key
    }
}

@Composable
fun MainSearchScreen(viewModel: SearchViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Collect States
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val language by viewModel.languageState.collectAsStateWithLifecycle()

    // Dialog states
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    // Floating UI notifications (Toast / Snack)
    val isSafeSearchState by viewModel.isSafeSearch.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Web,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "CLL SHRPV 1.0",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showHistoryDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .testTag("history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = Translations.get("history", language)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = Translations.get("settings", language)
                            )
                        }
                    }
                }

                // Modern Pill Tab Navigation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            Triple(0, Icons.Filled.Search, "tab_search"),
                            Triple(1, Icons.Filled.TravelExplore, "tab_directory"),
                            Triple(2, Icons.Filled.Assistant, "tab_companion")
                        ).forEach { (idx, icon, translationKey) ->
                            val isSelected = selectedTab == idx
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                label = "bg_color"
                            )
                            val tintColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                label = "tint_color"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(bgColor)
                                    .clickable { viewModel.selectTab(idx) }
                                    .testTag("tab_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = tintColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Translations.get(translationKey, language),
                                        color = tintColor,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Dispatcher
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState > initialState) width else -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> if (targetState > initialState) -width else width } + fadeOut()
                },
                label = "tab_content"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> GlobalSearchTab(viewModel, language)
                    1 -> WebDirectoryTab(viewModel, language)
                    2 -> AICompanionTab(viewModel, language)
                }
            }

            // Simulated Voice Search Popup
            val isVoiceSearchActive by viewModel.isVoiceSearchActive.collectAsStateWithLifecycle()
            if (isVoiceSearchActive) {
                VoiceQueryDialog(viewModel, language)
            }

            // Settings popup
            if (showSettingsDialog) {
                SettingsDialog(
                    viewModel = viewModel,
                    language = language,
                    onDismiss = { showSettingsDialog = false }
                )
            }

            // History popup
            if (showHistoryDialog) {
                HistoryDialog(
                    viewModel = viewModel,
                    language = language,
                    onDismiss = { showHistoryDialog = false }
                )
            }
        }
    }
}

@Composable
fun GlobalSearchTab(viewModel: SearchViewModel, language: String) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    var inputVal by remember { mutableStateOf(searchQuery) }

    // Sync input when VM search query is directly modified (e.g. from clicking history or suggestions)
    LaunchedEffect(searchQuery) {
        inputVal = searchQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Underline visual branding
        item {
            Spacer(modifier = Modifier.height(20.dp))
            if (searchUiState is SearchUiState.Idle) {
                // Large Google-style Hub
                Image(
                    painter = painterResource(id = R.drawable.img_search_logo),
                    contentDescription = "CLL SHRPV Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "CLL SHRPV Search Engine",
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Search Input Area
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )

                    TextField(
                        value = inputVal,
                        onValueChange = { inputVal = it },
                        placeholder = {
                            Text(
                                Translations.get("search_placeholder", language),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        trailingIcon = {
                            if (inputVal.isNotEmpty()) {
                                IconButton(onClick = {
                                    inputVal = ""
                                    viewModel.resetSearch()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )

                    IconButton(
                        onClick = { viewModel.triggerVoiceSearch() },
                        modifier = Modifier.testTag("voice_search_mic")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Ovozli qidiruv",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { viewModel.performGlobalWebSearch(inputVal) },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("search_submit_button")
                    ) {
                        Text(Translations.get("search_btn", language), fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Loading, Success, Error feeds
        item {
            when (val state = searchUiState) {
                is SearchUiState.Idle -> {
                    // Beautiful suggestion bubbles and widgets
                    Text(
                        text = Translations.get("trends", language),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )

                    // Staggered list of trendy ideas
                    listOf("search_suggest_1", "search_suggest_2", "search_suggest_3", "search_suggest_4").forEach { key ->
                        val text = Translations.get(key, language)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    inputVal = text
                                    viewModel.performGlobalWebSearch(text)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.ArrowOutward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weather widget
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WbCloudy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = Translations.get("weather_forecast", language),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = Translations.get("weather_temp", language),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                is SearchUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "CLL SHRPV global algoritmlari indekslanmoqda...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is SearchUiState.Success -> {
                    SearchResultsView(state.data, language)
                }

                is SearchUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.errorContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        state.fallbackData?.let {
                            SearchResultsView(it, language)
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SearchResultsView(data: UnifiedSearchResponse, language: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // AI Answer (SGE Analysis)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = data.aiTrendHeading,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = data.aiSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Web matches heading
        Text(
            text = Translations.get("show_results", language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Web Matches List
        data.results.forEach { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Suppress
                        }
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = result.url,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = result.snippet,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Directory and rankings screen
@Composable
fun WebDirectoryTab(viewModel: SearchViewModel, language: String) {
    val context = LocalContext.current
    val isGrid by viewModel.isGridLayout.collectAsStateWithLifecycle()
    val searchFavsOnly by viewModel.showOnlyBookmarks.collectAsStateWithLifecycle()
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val sortBy by viewModel.directorySortBy.collectAsStateWithLifecycle()
    val query by viewModel.directorySearchQuery.collectAsStateWithLifecycle()
    val favsSet by viewModel.bookmarkedEngineIds.collectAsStateWithLifecycle()

    val engines = viewModel.getFilteredEngines()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.setDirectoryQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("directory_search_input"),
            placeholder = { Text(Translations.get("search_placeholder", language)) },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            shape = RoundedCornerShape(20.dp),
            singleLine = true
        )

        // Filters bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bookmarks only Filter chip
            FilterChip(
                selected = searchFavsOnly,
                onClick = { viewModel.toggleShowOnlyBookmarks() },
                label = { Text(Translations.get("bookmarks_only", language), fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = if (searchFavsOnly) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Categories
            SearchCategory.values().forEach { cat ->
                val isSelected = selectedCat == cat
                val text = when (language) {
                    "uz" -> cat.labelUz
                    "ru" -> cat.labelRu
                    else -> cat.labelEn
                }
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setDirectoryCategory(cat) },
                    label = { Text(text, fontSize = 11.sp) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        // Sorting Option and layout switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${Translations.get("sort_by", language)}: ",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Sort Options List
                listOf(
                    Pair("rank", "global_rank"),
                    Pair("speed", "speed"),
                    Pair("privacy", "privacy")
                ).forEach { (sortKey, transKey) ->
                    val active = sortBy == sortKey
                    Text(
                        text = Translations.get(transKey, language),
                        fontSize = 12.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { viewModel.setDirectorySort(sortKey) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }

            // Layout Toggle
            IconButton(
                onClick = { viewModel.setGridLayout(!isGrid) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isGrid) Icons.Filled.GridOn else Icons.Filled.List,
                    contentDescription = "Toggle Grid/List",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid vs List display
        if (isGrid) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(engines, key = { it.id }) { engine ->
                    val isFav = favsSet.contains(engine.id)
                    EngineCardGridItem(engine, isFav, language, {
                        viewModel.toggleBookmark(engine.id)
                    }, {
                        // Open Home URL
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(engine.homeUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {}
                    })
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(engines, key = { it.id }) { engine ->
                    val isFav = favsSet.contains(engine.id)
                    EngineCardListItem(engine, isFav, language, {
                        viewModel.toggleBookmark(engine.id)
                    }, {
                        // Open Home URL
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(engine.homeUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {}
                    })
                }
            }
        }
    }
}

@Composable
fun EngineCardListItem(
    engine: SearchEngine,
    isFav: Boolean,
    language: String,
    onFavToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("engine_list_item_${engine.id}"),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = engine.iconPlaceholderLetter.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = engine.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "#${engine.globalRank} ${Translations.get("global_rank", language)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                IconButton(onClick = onFavToggle) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = engine.localizedDescription(language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Ratings Indices Display
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MetricRatingRow(Translations.get("speed", language), engine.speedIndex, Color(0xFF4CAF50))
                MetricRatingRow(Translations.get("privacy", language), engine.privacyIndex, Color(0xFF2196F3))
                MetricRatingRow(Translations.get("db_size", language), engine.databaseIndex, Color(0xFF9C27B0))
            }
        }
    }
}

@Composable
fun EngineCardGridItem(
    engine: SearchEngine,
    isFav: Boolean,
    language: String,
    onFavToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("engine_grid_item_${engine.id}"),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = engine.iconPlaceholderLetter.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                IconButton(onClick = onFavToggle, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = engine.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Rank: #${engine.globalRank}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ratings indices display simplified
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MiniMetricRow(Translations.get("speed", language), engine.speedIndex, Color(0xFF4CAF50))
                MiniMetricRow(Translations.get("privacy", language), engine.privacyIndex, Color(0xFF2196F3))
            }
        }
    }
}

@Composable
fun MetricRatingRow(label: String, score: Float, barColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${score}/10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = barColor)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = score / 10.0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun MiniMetricRow(label: String, score: Float, barColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label.take(8), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box(
            modifier = Modifier
                .background(barColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text("${score}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = barColor)
        }
    }
}

// AI Companion layout
@Composable
fun AICompanionTab(viewModel: SearchViewModel, language: String) {
    val companionHistory by viewModel.companionHistory.collectAsStateWithLifecycle()
    val companionLoading by viewModel.companionLoading.collectAsStateWithLifecycle()
    var promptVal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = Translations.get("companion_tip", language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // History container list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (companionHistory.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Forum,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = Translations.get("voice_demo", language),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(companionHistory) { (userPrompt, aiAnswer) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // User prompt bubble
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
                                )
                                .padding(12.dp, 10.dp)
                        ) {
                            Text(
                                text = userPrompt,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // AI assistant bubble
                        if (aiAnswer.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = aiAnswer,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(vertical = 4.dp))

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptVal,
                onValueChange = { promptVal = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("companion_prompt_input"),
                placeholder = { Text(Translations.get("search_placeholder", language), fontSize = 12.sp) },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                singleLine = false
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (promptVal.isNotBlank() && !companionLoading) {
                        viewModel.queryAiCompanion(promptVal)
                        promptVal = ""
                    }
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .size(46.dp)
                    .testTag("companion_submit")
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Custom UI Dialogs
@Composable
fun SettingsDialog(viewModel: SearchViewModel, language: String, onDismiss: () -> Unit) {
    val theme by viewModel.themeState.collectAsStateWithLifecycle()
    val isSafeSearch by viewModel.isSafeSearch.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = Translations.get("settings", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Language Select Options Block
                Text(
                    text = Translations.get("language", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair("uz", "O'zbek"),
                        Pair("en", "English"),
                        Pair("ru", "Русский")
                    ).forEach { (langCode, langName) ->
                        val active = language == langCode
                        Button(
                            onClick = { viewModel.setLanguage(langCode) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text(langName, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Theme selection blocks
                Text(
                    text = Translations.get("dark_mode", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("system", "system_mode", Icons.Filled.SettingsSuggest),
                        Triple("light", "light_mode", Icons.Filled.LightMode),
                        Triple("dark", "dark_mode", Icons.Filled.DarkMode)
                    ).forEach { (themeKey, labelKey, icon) ->
                        val selected = theme == themeKey
                        Button(
                            onClick = { viewModel.setTheme(themeKey) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(Translations.get(labelKey, language), fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Safe search toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(Translations.get("safe_search", language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            Translations.get("about_desc", language),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Switch(
                        checked = isSafeSearch,
                        onCheckedChange = { viewModel.setSafeSearch(it) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct clean Dismiss
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Composable
fun HistoryDialog(viewModel: SearchViewModel, language: String, onDismiss: () -> Unit) {
    val history by viewModel.searchHistory.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 400.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Translations.get("history", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (history.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearAllHistory() }) {
                            Text(
                                Translations.get("clear_all", language),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Translations.get("empty_history", language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(history) { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.performGlobalWebSearch(entry.queryText)
                                        viewModel.selectTab(0)
                                        onDismiss()
                                    }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = entry.queryText,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteHistoryItem(entry.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

// Glowing Pulse Simulated Voice Input
@Composable
fun VoiceQueryDialog(viewModel: SearchViewModel, language: String) {
    var dictatingText by remember { mutableStateOf("") }
    var dotsCount by remember { mutableStateOf(1) }

    // Pulsating animation scale
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Simulate real speech to text process
    LaunchedEffect(Unit) {
        val options = when (language) {
            "uz" -> listOf(
                "O'", "O'zbek", "O'zbekiston", "O'zbekiston milliy", "O'zbekiston milliy yangiliklari"
            )
            "ru" -> listOf(
                "Ан", "Андроид", "Андроид раз", "Андроид разработка", "Андроид разработка СЛЛ"
            )
            else -> listOf(
                "Jet", "Jetpack", "Jetpack Comp", "Jetpack Compose", "Jetpack Compose Animation"
            )
        }

        // Animated dots indicator increment
        launch {
            while (true) {
                delay(330)
                dotsCount = (dotsCount % 3) + 1
            }
        }

        // Dictate phrases incrementally
        for (phrase in options) {
            delay(500)
            dictatingText = phrase
        }
        delay(600)
        // Complete speaking
        viewModel.completeVoiceSearch(dictatingText)
    }

    Dialog(onDismissRequest = { viewModel.cancelVoiceSearch() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Translations.get("voice_title", language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Pulsating Mic Ring
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .drawBehind {
                                drawCircle(
                                    color = Color(0xFF2196F3).copy(alpha = 0.2f),
                                    radius = size.minDimension / 2 * scale
                                )
                            }
                    )
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dictated Text preview
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (dictatingText.isEmpty()) "..." else dictatingText,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ".".repeat(dotsCount),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = Translations.get("voice_tip", language),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = { viewModel.cancelVoiceSearch() }) {
                    Text("Yopish / Cancel", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
