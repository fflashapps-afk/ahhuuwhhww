package com.example.data.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "search_history")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "query_text") val queryText: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "engine_bookmarks")
data class EngineBookmark(
    @PrimaryKey val engineId: String,
    @ColumnInfo(name = "bookmarked_at") val bookmarkedAt: Long = System.currentTimeMillis()
)

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 40")
    fun getSearchHistory(): Flow<List<HistoryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entry: HistoryEntry)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

    @Query("DELETE FROM search_history")
    suspend fun clearAllHistory()

    // Bookmarks
    @Query("SELECT * FROM engine_bookmarks")
    fun getAllBookmarks(): Flow<List<EngineBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: EngineBookmark)

    @Query("DELETE FROM engine_bookmarks WHERE engineId = :engineId")
    suspend fun removeBookmark(engineId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM engine_bookmarks WHERE engineId = :engineId)")
    suspend fun isBookmarked(engineId: String): Boolean
}

@Database(entities = [HistoryEntry::class, EngineBookmark::class], version = 1, exportSchema = false)
abstract class SearchDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao

    companion object {
        @Volatile
        private var INSTANCE: SearchDatabase? = null

        fun getDatabase(context: Context): SearchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchDatabase::class.java,
                    "cll_shrpv_search_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class SearchRepository(private val searchDao: SearchDao) {
    val searchHistory: Flow<List<HistoryEntry>> = searchDao.getSearchHistory()
    val allBookmarks: Flow<List<EngineBookmark>> = searchDao.getAllBookmarks()

    suspend fun insertHistory(queryText: String) {
        if (queryText.isNotBlank()) {
            searchDao.insertHistory(HistoryEntry(queryText = queryText.trim()))
        }
    }

    suspend fun deleteHistory(id: Int) {
        searchDao.deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        searchDao.clearAllHistory()
    }

    suspend fun toggleBookmark(engineId: String) {
        if (searchDao.isBookmarked(engineId)) {
            searchDao.removeBookmark(engineId)
        } else {
            searchDao.addBookmark(EngineBookmark(engineId))
        }
    }
}
