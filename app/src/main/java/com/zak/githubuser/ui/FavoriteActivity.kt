package com.zak.githubuser.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.githubuser.R
import com.zak.githubuser.data.local.SettingPreferences
import com.zak.githubuser.data.local.entity.FavoriteEntity
import com.zak.githubuser.data.remote.response.ItemsItem
import com.zak.githubuser.databinding.ActivityFavoriteBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        supportActionBar?.title = "User Favorit"

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvFavorite.addItemDecoration(itemDecoration)

        val pref = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory(this.application, pref)
        favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        favoriteViewModel.getFavorite().observe(this) { users ->
            if (users != null) {
                findFavorite()
            }
        }
    }


    private fun findFavorite() {
        favoriteViewModel.getFavorite().observe(this) { users: List<FavoriteEntity>? ->
            val items = arrayListOf<ItemsItem>()
            if (users != null) {
                users.map {
                    val item = ItemsItem(login = it.login, avatarUrl = it.avatarUrl, id = 0, siteAdmin = false)
                    items.add(item)
                }
                val adapter = UserAdapter(items)
                binding.rvFavorite.adapter = adapter

                adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: ItemsItem) {
                        showSelectedUserDetail(data)
                    }
                })
            }
        }
    }

    private fun showSelectedUserDetail(userItems: ItemsItem) {
        val objectIntent = Intent(this, DetailActivity::class.java)
        objectIntent.putExtra(DetailActivity.EXTRA_USER, userItems)
        startActivity(objectIntent)
    }
}
