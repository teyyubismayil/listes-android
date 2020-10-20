package com.teyyub.listes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.teyyub.listes.adapters.ListesRecyclerViewAdapter
import com.teyyub.listes.model.Thing
import kotlinx.android.synthetic.main.fragment_things_list.view.*

private const val WHATTAG = "thing-what"
private const val ISDONETAG = "thing-isdone"

//Fragment which shows list of things
class ThingsListFragment : Fragment() {

    //Adapter for recyclerView
    //First we pass emptyList until we get list of Things from LiveData
    private var adapter: ListesRecyclerViewAdapter =
        ListesRecyclerViewAdapter(emptyList())

    //what and isDone properties of Thing objects to show
    //They are passed to newInstance(..) function
    // and stored in fragment's arguments bundle
    private lateinit var what: String
    private var isDone: Boolean = false

    private lateinit var viewModel: ThingsListViewModel

    private lateinit var addFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrieving values from arguments bundle
        what = arguments?.getString(WHATTAG) ?: ""
        isDone = arguments?.getBoolean(ISDONETAG) ?: false

        //viewModel
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.NewInstanceFactory() {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return ThingsListViewModel(what, isDone) as T
                }
            }
        ).get(ThingsListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_things_list, container, false)

        addFab = view.add_fab
        configureAddFab()

        //Configuring recyclerView
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Getting list of Thing objects from database
        //We scope lifetime of Observer to lifetime
        // of lifecycle of fragment view
        viewModel.thingsLiveData.observe(
            viewLifecycleOwner,
            Observer { things ->
                things?.let {
                    //We update adapter to show Things
                    // which we got from database
                    updateAdapter(things)
                }
            }
        )
    }

    private fun configureAddFab() {
        //We show floatButton if this fragment shows list
        // of things to do(isDone is false)
        if (isDone) {
            addFab.hide()
        } else {
            addFab.setOnClickListener {
                //Show full screen dialog for adding Thing to do
                AddFragment.newInstance(what).show(parentFragmentManager, null)
            }
        }
    }

    private fun updateAdapter(list: List<Thing>) {
        adapter = ListesRecyclerViewAdapter(list)

        adapter.setListener(object : ListesRecyclerViewAdapter.Listener{
            override fun onDidButtonClick(thing: Thing) {
                viewModel.didThing(thing)
            }
            override fun onDeleteButtonClick(thing: Thing) {
                viewModel.deleteThing(thing)
            }

        })

        requireView().recycler_view.adapter = adapter
    }

    companion object {
        //Static method for creating an instance of ThingsListFragment
        //and putting passed arguments in fragment arguments bundle
        fun newInstance(what: String, isDone: Boolean): ThingsListFragment {
            val args = Bundle().apply {
                putString(WHATTAG, what)
                putBoolean(ISDONETAG, isDone)
            }

            return ThingsListFragment().apply {
                arguments = args
            }
        }
    }
}