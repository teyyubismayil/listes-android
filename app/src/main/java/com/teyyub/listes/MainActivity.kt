package com.teyyub.listes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

const val goal = "Goal"
const val book = "Book"
const val movie = "Movie"

class MainActivity : AppCompatActivity() {

    private lateinit var toolBar: MaterialToolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    //titles of tabs of tabLayout
    private lateinit var tabTitles: List<String>

    //viewModel of MainActivity
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initializing viewModel
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        toolBar = findViewById(R.id.toolbar)

        configureBottomNavigationView()

        if (viewModel.selectedNavigationItem == null) {
            bottomNavigationView.selectedItemId = R.id.goals_item
        } else {
            //If activity recreated after configuration change
            bottomNavigationView.selectedItemId = viewModel.selectedNavigationItem ?: R.id.goals_item
        }
    }

    private fun configureBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            //Saving selected navigation item in view model
            viewModel.selectedNavigationItem = item.itemId
            //Handling item selection
            when (item.itemId) {
                R.id.goals_item -> {
                    configureViewPager(goal)
                    toolBar.title = resources.getString(R.string.goals)
                    true
                }
                R.id.books_item -> {
                    configureViewPager(book)
                    toolBar.title = resources.getString(R.string.books)
                    true
                }
                R.id.movies_item -> {
                    configureViewPager(movie)
                    toolBar.title = resources.getString(R.string.movies)
                    true
                }
                else -> false
            }
        }
    }

    private fun configureViewPager(what: String) {
        //Setting adapter for viewPager
        viewPager.adapter = ListesViewPagerAdapter(this, what)

        //Initializing list for titles of tab depending on selected bottomNavigationView item
        tabTitles = when (what) {
            goal -> resources.getStringArray(R.array.achieve).toList()
            book -> resources.getStringArray(R.array.read).toList()
            movie -> resources.getStringArray(R.array.watch).toList()
            else -> listOf("tab1", "tab2")
        }

        //Linking the tabLayout and the viewPager together
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }
}