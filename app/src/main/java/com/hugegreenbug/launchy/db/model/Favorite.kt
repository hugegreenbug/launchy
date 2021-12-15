package com.hugegreenbug.launchy.db.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite (
    @PrimaryKey
    var packageName: String = ""
)