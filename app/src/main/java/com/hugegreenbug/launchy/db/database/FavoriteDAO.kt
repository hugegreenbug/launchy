package com.hugegreenbug.launchy.db.database

import androidx.room.*
import com.hugegreenbug.launchy.db.model.Favorite

@Dao
interface FavoriteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorite:Favorite)

    @Update
    fun update(favorite:Favorite)

    @Query("DELETE FROM FAVORITES WHERE packageName==:packageName")
    fun delete(packageName:String)

    @Query("SELECT * FROM FAVORITES")
    fun getAllFavorites():List<Favorite>

    @Query("SELECT * FROM FAVORITES WHERE packageName==:packageName")
    fun getFavorite(packageName:String):Favorite?
}