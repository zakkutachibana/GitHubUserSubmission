package com.zak.githubuser.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zak.githubuser.data.remote.response.ItemsItem
import com.zak.githubuser.databinding.FragmentFollowBinding

class FollowFragment : Fragment() {
    companion object {
        const val ARG_USERNAME = "username"
        const val ARG_POSITION = "position"
    }
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!
    private val followViewModel by viewModels<FollowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        val followViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            FollowViewModel::class.java)

        followViewModel.user.observe(viewLifecycleOwner) { user ->
            setFollowData(user)
        }

        followViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.rvFollow.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding.rvFollow.addItemDecoration(itemDecoration)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString(ARG_USERNAME)
        val position = arguments?.getInt(ARG_POSITION)
        if (position == 1){
            if (username != null) {
                followViewModel.findFollowing(username)
            }
        } else {
            if (username != null) {
                followViewModel.findFollowers(username)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setFollowData(user: List<ItemsItem>) {
        val listUser = ArrayList<ItemsItem>()
        for (userItem in user) {
            listUser.add(userItem)
        }
        val adapter = UserAdapter(listUser)
        binding.rvFollow.adapter = adapter

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                showSelectedUserDetail(data)
            }
        })
    }
    private fun showSelectedUserDetail(userItems: ItemsItem) {
        val objectIntent = Intent(activity, DetailActivity::class.java)
        objectIntent.putExtra(DetailActivity.EXTRA_USER, userItems)
        startActivity(objectIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}