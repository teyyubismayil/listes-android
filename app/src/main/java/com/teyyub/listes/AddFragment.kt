package com.teyyub.listes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.appbar.MaterialToolbar
import com.teyyub.listes.utils.hideKeyboard
import com.teyyub.listes.utils.hostFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_add.view.*

private const val TAG = "AddFragment"

//DialogFragment for adding Thing to do
class AddFragment : DialogFragment() {

    //Subject in which we will pass queries of user
    private val searchTextSubject: PublishSubject<String> = PublishSubject.create()

    //Observable for exposing query subject
    val searchStream: Observable<String>
        get() = searchTextSubject.hide()

    //Show which will emit when need to show populars list
    private val showPopularsSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    //Observable for exposing show populars subject
    val showPopularsStream: Observable<Unit>
        get() = showPopularsSubject.hide()

    //what property of Thing object which we will add to database
    private lateinit var what: String

    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrieving values from arguments bundle
        what = arguments?.getString(TAG) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        //hosting fragment
        if (savedInstanceState == null) {
            hostFragment(R.id.fragment_container, AddManualFragment.newInstance(what))
        }

        //If Thing is goal there is no search
        toolbar = view.toolbar
        if (what == goal) {
            toolbar.findViewById<LinearLayout>(R.id.search_bar).visibility = View.GONE
            toolbar.title = getString(R.string.add_goal)
        }

        return view
    }

    //style for this fragment
    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onStart() {
        super.onStart()

        //listeners for search bar
        if (what != goal) {
            configureSearchBarListeners()
        }
    }

    private fun configureSearchBarListeners() {
        val clearIcon = toolbar.findViewById<ImageButton>(R.id.clear_icon)
        val findEditText = toolbar.findViewById<EditText>(R.id.find_edittext)
        val findBackIcon = toolbar.findViewById<ImageButton>(R.id.find_back_icon)

        //Back button which clears focus of editText
        findBackIcon.setOnClickListener {
            if (findEditText.isFocused) {
                findEditText.clearFocus()
                findEditText.text.clear()
                hideKeyboard()
            } else {
                findEditText.requestFocus()
            }
        }

        //button which clears editText
        clearIcon.setOnClickListener {
            findEditText.text.clear()
            //show populars
            showPopularsSubject.onNext(Unit)
        }

        //changing icon and replacing fragment
        //depending on editText is focused
        findEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                findBackIcon.setImageResource(R.drawable.ic_back)
                //open list
                hostFragment(R.id.fragment_container, AddSearchFragment.newInstance(what))
                showPopularsSubject.onNext(Unit)
            } else {
                findBackIcon.setImageResource(R.drawable.ic_search)
                hostFragment(R.id.fragment_container, AddManualFragment.newInstance(what))
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
                searchTextSubject.onNext(editText.text.toString())
            }
            true
        }
    }

    companion object {
        //Static method for creating an instance of AddFragment
        //and putting passed arguments in fragment arguments bundle
        fun newInstance(what: String): AddFragment {
            val args = Bundle().apply {
                putString(TAG, what)
            }
            return AddFragment().apply {
                arguments = args
            }
        }
    }
}