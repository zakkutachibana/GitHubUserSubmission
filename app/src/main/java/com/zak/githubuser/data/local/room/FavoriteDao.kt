package com.zak.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zak.githubuser.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * from FavoriteEntity")
    fun getFavorite() : LiveData<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: FavoriteEntity)

    @Delete
    fun delete(favorite: FavoriteEntity)

    @Query("SELECT * from FavoriteEntity WHERE username = :username")
    fun isFavorite(username: String): LiveData<FavoriteEntity>
}