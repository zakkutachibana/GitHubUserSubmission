package com.zak.githubuser.data.local

import android.app.Application
import androidx.lifecycle.LiveData
import com.zak.githubuser.data.local.entity.FavoriteEntity
import com.zak.githubuser.data.local.room.FavoriteDao
import com.zak.githubuser.data.local.room.FavoriteDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application){
    private val mFavoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = FavoriteDatabase.getDatabase(application)
        mFavoriteDao = db.favoriteDao()
    }
    fun getFavorite(): LiveData<List<FavoriteEntity>> = mFavoriteDao.getFavorite()
    fun insert(favorite: FavoriteEntity) {
        executorService.execute { mFavoriteDao.insert(favorite) }
    }
    fun delete(favorite: FavoriteEntity) {
        executorService.execute { mFavoriteDao.delete(favorite) }
    }
    fun isFavorite(username: String) : LiveData<FavoriteEntity> = mFavoriteDao.isFavorite(username)

}