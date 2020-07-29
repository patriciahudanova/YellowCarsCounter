package com.example.yellowcarscounter.main.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.yellowcarscounter.main.tabs.fragments.CounterFragment
import com.example.yellowcarscounter.main.tabs.fragments.UsersFragment


class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return CounterFragment()
            }
            1 -> {
                return UsersFragment()
            }
            else -> return Fragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}