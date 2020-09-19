package com.teyyub.listes

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.teyyub.listes.model.Thing
import com.teyyub.listes.repository.ThingRepository

private const val TAG = "AddFragment"

//DialogFragment for adding Thing to do
class AddFragment : DialogFragment() {

    private lateinit var cancelButton: MaterialButton
    private lateinit var addButton: MaterialButton
    private lateinit var nameEditText: TextInputEditText
    private lateinit var nameTextInput: TextInputLayout
    private lateinit var detailsEditText: TextInputEditText
    private lateinit var detailsTextInput: TextInputLayout
    private lateinit var toolbar: MaterialToolbar

    //what property of Thing object which we will add to database
    private lateinit var what: String

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

        cancelButton = view.findViewById(R.id.cancel_button)
        addButton = view.findViewById(R.id.add_button)
        nameEditText = view.findViewById(R.id.name_edit_text)
        nameTextInput = view.findViewById(R.id.name_text_input)
        detailsEditText = view.findViewById(R.id.details_edit_text)
        detailsTextInput = view.findViewById(R.id.details_text_input)
        toolbar = view.findViewById(R.id.toolbar)

        configureToolbar()

        addButton.setOnClickListener {
            if (!isNameValid(nameEditText.text!!)) {
                //Showing error message is written name is not valid
                nameTextInput.error = resources.getString(R.string.input_error)
            } else {
                //Clear the error
                nameTextInput.error = null
                //Adding new Thing object to database
                addThing(nameEditText.text.toString(), detailsEditText.text.toString())
                //Closing this dialog fragment
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            //Closing this dialog fragment if clicked cancel
            dismiss()
        }

        // Clear the error if name is valid
        nameEditText.setOnKeyListener { _, _, _ ->
            if (isNameValid(nameEditText.text!!)) {
                // Clear the error.
                nameTextInput.error = null
                Log.i("Teyyubc", "a")
            }
            false
        }

        return view
    }

    //style for this fragment
    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    //Checking if name written by user is valid
    private fun isNameValid(text: Editable?): Boolean {
        return text != null && text.length > 0
    }

    //Adding new Thing object to database
    private fun addThing(name: String, details: String) {
        val newThing = Thing(what, name, details, false)
        ThingRepository.get().addThing(newThing)
    }

    private fun configureToolbar() {
        //Setting title for toolbar
        //depending on passed what value
        toolbar.title = resources.getString(
            when (what) {
                goal -> R.string.add_goal
                book -> R.string.add_book
                movie -> R.string.add_movie
                else -> R.string.something
            }
        )
        toolbar.setNavigationOnClickListener {
            dismiss()
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