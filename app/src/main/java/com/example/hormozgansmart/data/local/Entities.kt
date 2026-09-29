package com.example.hormozgansmart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val itemType: String, // WORD, POI, KNOWLEDGE
    val title: String,
    val subtitle: String,
    val extraData: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_notes")
data class CustomNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)
