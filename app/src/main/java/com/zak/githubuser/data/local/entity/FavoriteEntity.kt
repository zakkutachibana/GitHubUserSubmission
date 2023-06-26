package com.zak.githubuser.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "username")
    var login: String = "",
    @ColumnInfo(name = "avatarUrl")
    var avatarUrl: String? = null,
) : Parcelable
