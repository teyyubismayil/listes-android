package com.teyyub.listes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

//Possible 'what' property values of Thing object
const val goal = "Goal"
const val book = "Book"
const val movie = "Movie"

class MainActivity : AppCompatActivity() {

    //titles of tabs of tabLayout
    private lateinit var tabTitles: List<String>

    //viewModel of MainActivity
    private lateinit var viewModel: MainActivityViewModel

    //what property of showed Things list
    private lateinit var what: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initializing viewModel
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        setNavigationListener()

        if (viewModel.selectedNavigationItem == null) {
            bottom_navigation_view.selectedItemId = R.id.goals_item
        } else {
            //If activity recreated after configuration change
            bottom_navigation_view.selectedItemId =
                viewModel.selectedNavigationItem ?: R.id.goals_item
        }
    }

    private fun setNavigationListener() {
        bottom_navigation_view.setOnNavigationItemSelectedListener { item ->
            //Saving selected navigation item in view model
            viewModel.selectedNavigationItem = item.itemId
            //Handling item selection
            when (item.itemId) {
                R.id.goals_item -> {
                    what = goal
                    selectNavigationItem()
                    true
                }
                R.id.books_item -> {
                    what = book
                    selectNavigationItem()
                    true
                }
                R.id.movies_item -> {
                    what = movie
                    selectNavigationItem()
                    true
                }
                else -> false
            }
        }
    }

    private fun selectNavigationItem() {
        configureViewPager()
        configureToolbar()
    }

    private fun configureToolbar() {
        toolbar.title = when (what) {
            goal -> resources.getString(R.string.goals)
            movie -> resources.getString(R.string.movies)
            book -> resources.getString(R.string.books)
            else -> throw IllegalArgumentException()
        }
    }

    private fun configureViewPager() {
        setPagerAdapter()

        initializeTabTitles()

        //Linking the tabLayout and the viewPager together
        TabLayoutMediator(tab_layout, view_pager) { tab, position ->
            tab.text = tabTitles[position]
            view_pager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun setPagerAdapter() {
        //Setting adapter for viewPager
        //It gets what(property of model Thing) to pass it to fragment
        //First fragment will show things to do(so isDone is false),
        //second fragment - done things(isDone is true)
        view_pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> ThingsListFragment.newInstance(what, false)
                    1 -> ThingsListFragment.newInstance(what, true)
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    private fun initializeTabTitles() {
        //Initializing list for titles of tab depending on selected bottomNavigationView item
        tabTitles = when (what) {
            goal -> resources.getStringArray(R.array.achieve).toList()
            book -> resources.getStringArray(R.array.read).toList()
            movie -> resources.getStringArray(R.array.watch).toList()
            else -> throw IllegalArgumentException()
        }
    }
}