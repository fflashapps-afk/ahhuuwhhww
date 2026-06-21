package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SearchCategory
import com.example.data.SearchEngine
import com.example.data.SearchEngineProvider
import com.example.data.database.HistoryEntry
import com.example.data.database.SearchDatabase
import com.example.data.database.SearchRepository
import com.example.data.network.GeminiApiClient
import com.example.data.network.UnifiedSearchResponse
import com.example.data.preferences.SearchPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val data: UnifiedSearchResponse) : SearchUiState
    data class Error(val message: String, val fallbackData: UnifiedSearchResponse?) : SearchUiState
}

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SearchDatabase.getDatabase(application)
    private val repository = SearchRepository(db.searchDao())
    val preferences = SearchPreferences(application)

    // Observable Flows
    val searchHistory: StateFlow<List<HistoryEntry>> = repository.searchHistory.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _bookmarkedEngineIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedEngineIds: StateFlow<Set<String>> = _bookmarkedEngineIds.asStateFlow()

    // Preferences reactive flows
    private val _themeState = MutableStateFlow(preferences.theme)
    val themeState: StateFlow<String> = _themeState.asStateFlow()

    private val _languageState = MutableStateFlow(preferences.language)
    val languageState: StateFlow<String> = _languageState.asStateFlow()

    private val _isSafeSearch = MutableStateFlow(preferences.isSafeSearch)
    val isSafeSearch: StateFlow<Boolean> = _isSafeSearch.asStateFlow()

    private val _isGridLayout = MutableStateFlow(preferences.isGridLayout)
    val isGridLayout: StateFlow<Boolean> = _isGridLayout.asStateFlow()

    // Normal UI states
    private val _selectedTab = MutableStateFlow(0) // 0: Global Search, 1: Engine Index/Discovery, 2: AI Assistance
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    // Engine Directory Filters & Directory search
    private val _directorySearchQuery = MutableStateFlow("")
    val directorySearchQuery: StateFlow<String> = _directorySearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(SearchCategory.ALL)
    val selectedCategory: StateFlow<SearchCategory> = _selectedCategory.asStateFlow()

    private val _directorySortBy = MutableStateFlow("rank") // "rank", "speed", "privacy", "db_size"
    val directorySortBy: StateFlow<String> = _directorySortBy.asStateFlow()

    private val _showOnlyBookmarks = MutableStateFlow(false)
    val showOnlyBookmarks: StateFlow<Boolean> = _showOnlyBookmarks.asStateFlow()

    // Voice search popup dialog state
    private val _isVoiceSearchActive = MutableStateFlow(false)
    val isVoiceSearchActive: StateFlow<Boolean> = _isVoiceSearchActive.asStateFlow()

    // AI Companion state
    private val _companionHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList()) // Pair of raw user prompt and response
    val companionHistory: StateFlow<List<Pair<String, String>>> = _companionHistory.asStateFlow()

    private val _companionLoading = MutableStateFlow(false)
    val companionLoading: StateFlow<Boolean> = _companionLoading.asStateFlow()

    init {
        // Collect Bookmarks changes
        viewModelScope.launch {
            repository.allBookmarks.collectLatest { list ->
                _bookmarkedEngineIds.value = list.map { it.engineId }.toSet()
            }
        }
    }

    // Tab control
    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // Setters for preferences
    fun setTheme(theme: String) {
        preferences.theme = theme
        _themeState.value = theme
    }

    fun setLanguage(lang: String) {
        preferences.language = lang
        _languageState.value = lang
    }

    fun setSafeSearch(enabled: Boolean) {
        preferences.isSafeSearch = enabled
        _isSafeSearch.value = enabled
    }

    fun setGridLayout(enabled: Boolean) {
        preferences.isGridLayout = enabled
        _isGridLayout.value = enabled
    }

    // Query mutators
    fun setQuery(value: String) {
        _searchQuery.value = value
    }

    fun setDirectoryQuery(value: String) {
        _directorySearchQuery.value = value
    }

    fun setDirectoryCategory(cat: SearchCategory) {
        _selectedCategory.value = cat
    }

    fun setDirectorySort(sort: String) {
        _directorySortBy.value = sort
    }

    fun toggleShowOnlyBookmarks() {
        _showOnlyBookmarks.value = !_showOnlyBookmarks.value
    }

    // Bookmark Toggle in Room
    fun toggleBookmark(engineId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(engineId)
        }
    }

    // Delete history
    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // Core Global search execution
    fun performGlobalWebSearch(queryText: String) {
        val trimmed = queryText.trim()
        if (trimmed.isEmpty()) return

        _searchQuery.value = trimmed
        _searchUiState.value = SearchUiState.Loading

        // Persist to history
        viewModelScope.launch {
            repository.insertHistory(trimmed)
        }

        viewModelScope.launch {
            try {
                val response = GeminiApiClient.searchWeb(
                    query = trimmed,
                    language = _languageState.value,
                    isSafeSearch = _isSafeSearch.value
                )
                if (response.error != null) {
                    _searchUiState.value = SearchUiState.Error(response.error, response)
                } else {
                    _searchUiState.value = SearchUiState.Success(response)
                }
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error(e.localizedMessage ?: "Unknown Search Error", null)
            }
        }
    }

    // Reset Search View to general Google-Style idle interface
    fun resetSearch() {
        _searchQuery.value = ""
        _searchUiState.value = SearchUiState.Idle
    }

    // Filtered & Sorted Search Engine list
    fun getFilteredEngines(): List<SearchEngine> {
        val rawList = SearchEngineProvider.engines
        val query = _directorySearchQuery.value.trim().lowercase()
        val cat = _selectedCategory.value
        val showOnlyFavs = _showOnlyBookmarks.value
        val favs = _bookmarkedEngineIds.value

        var filtered = rawList.filter { engine ->
            // Search filter
            (engine.name.lowercase().contains(query) ||
             engine.descriptionUz.lowercase().contains(query) ||
             engine.descriptionEn.lowercase().contains(query) ||
             engine.descriptionRu.lowercase().contains(query)) &&
            // Category filter
            (cat == SearchCategory.ALL || engine.category == cat) &&
            // Bookmarks filter
            (!showOnlyFavs || favs.contains(engine.id))
        }

        // Sorting
        filtered = when (_directorySortBy.value) {
            "speed" -> filtered.sortedByDescending { it.speedIndex }
            "privacy" -> filtered.sortedByDescending { it.privacyIndex }
            "db_size" -> filtered.sortedByDescending { it.databaseIndex }
            else -> filtered.sortedBy { it.globalRank } // Sort by Rank
        }

        return filtered
    }

    // Simulated Voice Search
    fun triggerVoiceSearch() {
        _isVoiceSearchActive.value = true
    }

    fun completeVoiceSearch(text: String) {
        _isVoiceSearchActive.value = false
        if (text.isNotBlank()) {
            performGlobalWebSearch(text)
        }
    }

    fun cancelVoiceSearch() {
        _isVoiceSearchActive.value = false
    }

    // AI helper chat
    fun queryAiCompanion(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return

        _companionLoading.value = true
        _companionHistory.value = _companionHistory.value + Pair(prompt, "")

        viewModelScope.launch {
            try {
                val sysPrompt = when (_languageState.value) {
                    "uz" -> "Siz qidiruvni o'rganuvchi aqlli CLL SHRPV 1.0 AI asistentisiz. Foydalanuvchining savoliga qisqa va qulay tushuntirish va agar so'ralsa eng yaxshi qidiruv iboralarini taklif qil."
                    "ru" -> "Вы умный поисковый ИИ-ассистент CLL SHRPV 1.0. Предоставляйте емкие и полезные советы, предлагайте точные ключевые слова для поиска."
                    else -> "You are the helpful CLL SHRPV 1.0 Search Companion. Provide concise and constructive search advice, key phrases, and tips."
                }
                val response = GeminiApiClient.searchWeb(trimmed, _languageState.value, _isSafeSearch.value)
                val answer = response.aiSummary

                val updatedHistory = _companionHistory.value.toMutableList()
                val lastIdx = updatedHistory.lastIndex
                if (lastIdx >= 0) {
                    updatedHistory[lastIdx] = Pair(prompt, answer)
                }
                _companionHistory.value = updatedHistory
            } catch (e: Exception) {
                // error fallback
                val updatedHistory = _companionHistory.value.toMutableList()
                val lastIdx = updatedHistory.lastIndex
                if (lastIdx >= 0) {
                    updatedHistory[lastIdx] = Pair(prompt, "Xatolik: ${e.localizedMessage}")
                }
                _companionHistory.value = updatedHistory
            } finally {
                _companionLoading.value = false
            }
        }
    }

    fun clearCompanionHistory() {
        _companionHistory.value = emptyList()
    }
}

// Simple extension helper for StateFlow
private fun <T> kotlinx.coroutines.flow.Flow<T>.stateIn(
    scope: kotlinx.coroutines.CoroutineScope,
    started: kotlinx.coroutines.flow.SharingStarted,
    initialValue: T
): StateFlow<T> {
    val state = MutableStateFlow(initialValue)
    scope.launch {
        this@stateIn.collect {
            state.value = it
        }
    }
    return state.asStateFlow()
}
