package com.example.caps.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_monuments")
data class FavoriteMonument(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val history: String,
    val image_url: String
)