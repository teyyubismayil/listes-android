package com.teyyub.listes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.teyyub.listes.adapters.SearchRecyclerViewAdapter
import com.teyyub.listes.model.Doable
import com.teyyub.listes.utils.viewModelFactory
import kotlinx.android.synthetic.main.fragment_add_search.view.*

private const val TAG = "SearchFragment"

class AddSearchFragment : Fragment() {

    //what property of Doable object which we will search for
    private lateinit var what: String

    private lateinit var viewModel: AddViewModel

    private var adapter: SearchRecyclerViewAdapter =
        SearchRecyclerViewAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrieving values from arguments bundle
        what = arguments?.getString(TAG) ?: ""

        //Used viewModel instance of parent activity
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory { AddViewModel(what) }
        ).get(AddViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_search, container, false)

        view.recycler_view.adapter = adapter
        view.recycler_view.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Showing populars list
        viewModel.popularsLiveData.observe(
            viewLifecycleOwner,
            Observer { doables ->
                if (what == Doable.DOABLE_MOVIE) {
                    view.heading_text.text = getString(R.string.popular_movies)
                } else {
                    view.heading_text.text = getString(R.string.popular_books)
                }
                updateAdapter(doables)
            }
        )

        //Showing result of search
        viewModel.searchedLiveData.observe(
            viewLifecycleOwner,
            Observer { doables ->
                if (doables.isEmpty()) {
                    view.heading_text.text = getString(R.string.nothing_found)
                } else {
                    view.heading_text.text = getString(R.string.results)
                }
                updateAdapter(doables)
            }
        )

        //Showing error message
        viewModel.errorLiveData.observe(
            viewLifecycleOwner,
            Observer { view.heading_text.text = getString(R.string.something_went_wrong) }
        )

        //Showing loading indicator
        viewModel.loadingLiveData.observe(
            viewLifecycleOwner,
            Observer {
                view.loading.visibility = if (it) {
                    view.heading_text.text = ""
                    updateAdapter(emptyList())
                    View.VISIBLE
                } else View.GONE
            }
        )
    }

    override fun onStart() {
        super.onStart()

        requireView().done_fab.setOnClickListener {
            //Closing AddActivity
            requireActivity().finish()
        }
    }

    private fun updateAdapter(doables: List<Doable>) {
        adapter = SearchRecyclerViewAdapter(doables)
        adapter.setListener(object : SearchRecyclerViewAdapter.Listener {
            override fun onAddButtonClicked(doable: Doable) {
                viewModel.addDoable(doable)
            }
        })
        requireView().recycler_view.adapter = adapter
    }

    companion object {
        //Static method for creating an instance of SearchFragment
        //and putting passed arguments in fragment arguments bundle
        fun newInstance(what: String): AddSearchFragment {
            val args = Bundle().apply {
                putString(TAG, what)
            }
            return AddSearchFragment().apply {
                arguments = args
            }
        }
    }
}