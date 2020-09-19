package com.teyyub.listes

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

//Adapter for viewPager from MainActivity
//It gets what(property of model Thing) to pass it to fragment
//First fragment will show things to do(so isDone is false),
//second fragment - done things(isDone is true)
class ListesViewPagerAdapter(
    activity: AppCompatActivity,
    private val what: String
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ThingsListFragment.newInstance(what, false)
            else -> ThingsListFragment.newInstance(what, true)
        }
    }
}