package com.zak.githubuser.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zak.githubuser.data.local.FavoriteRepository
import com.zak.githubuser.data.local.entity.FavoriteEntity

class FavoriteViewModel(application: Application): ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun getFavorite()  : LiveData<List<FavoriteEntity>> = mFavoriteRepository.getFavorite()

}

