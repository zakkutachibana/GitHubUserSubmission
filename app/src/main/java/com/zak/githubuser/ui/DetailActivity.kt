package com.zak.githubuser.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zak.githubuser.R
import com.zak.githubuser.data.local.SettingPreferences
import com.zak.githubuser.data.local.entity.FavoriteEntity
import com.zak.githubuser.data.remote.response.DetailUserResponse
import com.zak.githubuser.data.remote.response.ItemsItem
import com.zak.githubuser.databinding.ActivityDetailBinding


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private var isFav: Boolean = false

    companion object {
        const val EXTRA_FAV = "extra_fav"
        const val EXTRA_USER = "extra_user"
        private val TAB_TITLES = intArrayOf(
            R.string.following,
            R.string.follower
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_USER, ItemsItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_USER)
        }

        val fav = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_FAV, FavoriteEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_FAV)
        }

        val pref = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory(this.application, pref)

        detailViewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)

        detailViewModel.userDetail.observe(this) { userDetail ->
            setUserDetailData(userDetail)
        }

        val login = user?.login
        val avatarUrl = user?.avatarUrl
        val ivBookmark = binding.ivFavorite

        val sectionsPagerAdapter = user?.let { SectionsPagerAdapter(this, it.login) }
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()



        supportActionBar?.elevation = 0f
        supportActionBar?.hide()


        val favorite = FavoriteEntity()

        ivBookmark.setOnClickListener {
            if (avatarUrl != null) {
                favorite.avatarUrl = avatarUrl
            }
            favorite.login = login.toString()

            if (isFav) {
                detailViewModel.delete(favorite)
                Toast.makeText(this, "DELETED $login", Toast.LENGTH_SHORT).show()
            } else {
                detailViewModel.insert(favorite)
                Toast.makeText(this, "Favorited $login", Toast.LENGTH_SHORT).show()
            }

        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        if (user != null) {
            detailViewModel.findDetailUser(user.login)
        } else {
            if (fav != null) {
                detailViewModel.findDetailUser(fav.login)
            }
        }
    }

    private fun checkFavorite(response: DetailUserResponse) {
        detailViewModel.isFavorite(response.login).observe(this) { favorite ->
            if (favorite != null) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorited)
                isFav = true
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite)
                isFav = false
            }
        }
    }


    fun setUserDetailData(user: DetailUserResponse) {
        binding.tvUserUsername.text = user.login
        val followers = user.followers
        val followings = user.following
        binding.tvUserFollower.text = getString(R.string.follower_count, followers)
        binding.tvUserFollowing.text = getString(R.string.following_count, followings)
        if (user.name != null) {
            binding.tvUserName.text = user.name
        } else {
            binding.tvUserName.setTypeface(null, Typeface.ITALIC)
            binding.tvUserName.text = getString(R.string.null_name)
        }
        Glide.with(this)
            .load(user.avatarUrl)
            .into(findViewById(R.id.iv_detail_profile))
        checkFavorite(user)
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


}