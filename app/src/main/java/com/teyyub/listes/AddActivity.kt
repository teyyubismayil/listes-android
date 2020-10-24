package com.teyyub.listes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.teyyub.listes.model.Thing
import com.teyyub.listes.utils.hideKeyboard
import com.teyyub.listes.utils.viewModelFactory

//Activity for adding Thing to do
class AddActivity : AppCompatActivity() {

    //what property of Thing object which we will add to database
    private lateinit var what: String

    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPager: ViewPager2

    private lateinit var viewModel: AddViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.view_pager)

        //Retrieving values from arguments bundle
        what = intent.getStringExtra(THING_WHAT) ?: ""

        //viewModel
        viewModel = ViewModelProvider(
            this,
            viewModelFactory { AddViewModel(what) }
        ).get(AddViewModel::class.java)

        configureViewPager()

        //If Thing is goal there is no search
        if (what == Thing.THING_GOAL) {
            toolbar.findViewById<LinearLayout>(R.id.search_bar).visibility = View.GONE
            toolbar.title = getString(R.string.add_goal)
        }

        //listeners for search bar
        if (what != Thing.THING_GOAL) {
            configureSearchBarListeners()
        }
    }

    private fun configureViewPager() {
        //Disable swipe of viewPager
        viewPager.isUserInputEnabled = false

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> AddManualFragment.newInstance(what)
                    1 -> AddSearchFragment.newInstance(what)
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    private fun configureSearchBarListeners() {
        val clearIcon = toolbar.findViewById<ImageButton>(R.id.clear_icon)
        val findEditText = toolbar.findViewById<EditText>(R.id.find_edittext)
        val findBackIcon = toolbar.findViewById<ImageButton>(R.id.find_back_icon)

        //Back button which clears focus of editText
        findBackIcon.setOnClickListener {
            if (findEditText.isFocused) {
                hideKeyboard()
                findEditText.clearFocus()
                findEditText.text.clear()
            } else {
                findEditText.requestFocus()
            }
        }

        //button which clears editText
        clearIcon.setOnClickListener {
            findEditText.text.clear()
            //show populars
            viewModel.showPopulars()
        }

        //changing icon and replacing fragment
        //depending on editText is focused
        findEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                findBackIcon.setImageResource(R.drawable.ic_back)
                swipeToAddSearchFragment()
                viewModel.showPopulars()
            } else {
                findBackIcon.setImageResource(R.drawable.ic_search)
                swipeToAddManualFragment()
            }
        }

        //showing and hiding clear button
        findEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    clearIcon.visibility = View.GONE
                } else {
                    clearIcon.visibility = View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //When search button from keyboard was clicked
        findEditText.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                //Doing search
                viewModel.searchFor(editText.text.toString())
            }
            true
        }
    }

    private fun swipeToAddManualFragment() {
        viewPager.currentItem = 0
    }

    private fun swipeToAddSearchFragment() {
        viewPager.currentItem = 1
    }

    companion object {
        const val THING_WHAT = "thing what"
    }
}