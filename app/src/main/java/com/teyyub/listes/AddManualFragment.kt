package com.teyyub.listes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_manual.view.*

private const val TAG = "AddManualFragment"

class AddManualFragment: Fragment() {

    //what property of Thing object which we will add to database
    private lateinit var what: String

    private lateinit var viewModel: AddManualViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrieving values from arguments bundle
        what = arguments?.getString(TAG) ?: ""

        //viewModel
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.NewInstanceFactory() {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return AddManualViewModel(what) as T
                }
            }
        ).get(AddManualViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_manual, container, false)

        if (what == goal) {
            view.add_manual_text.visibility = View.GONE
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        configureListeners()
    }

    private fun configureListeners() {
        val view = requireView()

        view.add_button.setOnClickListener {
            if (!viewModel.isNameValid(view.name_edit_text.text!!)) {
                //Showing error message is written name is not valid
                view.name_text_input.error = resources.getString(R.string.input_error)
            } else {
                //Clear the error
                view.name_text_input.error = null
                //Adding new Thing object to database
                viewModel.addThing(view.name_edit_text.text.toString(), view.details_edit_text.text.toString())
                //Closing this dialog fragment
                (parentFragment as DialogFragment).dismiss()
            }
        }

        view.cancel_button.setOnClickListener {
            //Closing this dialog fragment if clicked cancel
            (parentFragment as DialogFragment).dismiss()
        }

        // Clear the error if name is valid
        view.name_edit_text.setOnKeyListener { _, _, _ ->
            if (viewModel.isNameValid(view.name_edit_text.text!!)) {
                // Clear the error.
                view.name_text_input.error = null
            }
            false
        }
    }

    companion object{
        //Static method for creating an instance of AddManualFragment
        //and putting passed arguments in fragment arguments bundle
        fun newInstance(what: String): AddManualFragment {
            val args = Bundle().apply {
                putString(TAG, what)
            }
            return AddManualFragment().apply {
                arguments = args
            }
        }
    }
}