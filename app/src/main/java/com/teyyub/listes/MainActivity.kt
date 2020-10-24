package com.teyyub.listes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.teyyub.listes.model.Thing
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ThingsListFragment.Listener {

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
                    what = Thing.THING_GOAL
                    selectNavigationItem()
                    true
                }
                R.id.books_item -> {
                    what = Thing.THING_BOOK
                    selectNavigationItem()
                    true
                }
                R.id.movies_item -> {
                    what = Thing.THING_MOVIE
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
            Thing.THING_GOAL -> resources.getString(R.string.goals)
            Thing.THING_MOVIE -> resources.getString(R.string.movies)
            Thing.THING_BOOK -> resources.getString(R.string.books)
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
                    0 -> ThingsListFragment.newInstance(what, false).apply { setListener(this@MainActivity) }
                    1 -> ThingsListFragment.newInstance(what, true).apply { setListener(this@MainActivity) }
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    //From ThingsListFragment.Listener
    //Called when add button clicked in ThingsListFragment
    override fun onAddButtonClick() {
        val intent = Intent(this, AddActivity::class.java).apply {
            putExtra(AddActivity.THING_WHAT, what)
        }
        startActivity(intent)
    }

    private fun initializeTabTitles() {
        //Initializing list for titles of tab depending on selected bottomNavigationView item
        tabTitles = when (what) {
            Thing.THING_GOAL -> resources.getStringArray(R.array.achieve).toList()
            Thing.THING_BOOK -> resources.getStringArray(R.array.read).toList()
            Thing.THING_MOVIE -> resources.getStringArray(R.array.watch).toList()
            else -> throw IllegalArgumentException()
        }
    }
}