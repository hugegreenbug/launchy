package com.hugegreenbug.launchy.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hugegreenbug.launchy.db.model.Favorite

@Database(entities = [Favorite::class], version = 2, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favorites():FavoriteDAO

    companion object {
        private var INSTANCE: FavoriteDatabase? = null
        fun getAppDatabase(context: Context): FavoriteDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java, "favorite-db"
                ).fallbackToDestructiveMigration().build()
            }
            return INSTANCE
        }
    }
}