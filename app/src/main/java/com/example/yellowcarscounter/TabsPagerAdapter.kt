package com.example.yellowcarscounter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Counter Fragment")
                val counterFragment = CounterFragment()
                counterFragment.arguments = bundle
                return counterFragment
            }
            1 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Users Fragment")
                val usersFragment = UsersFragment()
                usersFragment.arguments = bundle
                return usersFragment
            }
            else -> return Fragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}