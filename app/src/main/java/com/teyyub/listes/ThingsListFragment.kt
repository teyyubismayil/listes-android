package com.teyyub.listes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.teyyub.listes.adapters.ListesRecyclerViewAdapter
import com.teyyub.listes.model.Thing
import com.teyyub.listes.repository.DatabaseRepository
import kotlinx.android.synthetic.main.fragment_things_list.view.*

private const val WHATTAG = "thing-what"
private const val ISDONETAG = "thing-isdone"

//Fragment which shows list of things
class ThingsListFragment : Fragment() {

    //Adapter for recyclerView
    //First we pass emptyList until we get list of Things from LiveData
    private var adapter: ListesRecyclerViewAdapter? =
        ListesRecyclerViewAdapter(emptyList())

    //what and isDone properties of Thing objects to show
    //They are passed to newInstance(..) function
    // and stored in fragment's arguments bundle
    private lateinit var what: String
    private var isDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrieving values from arguments bundle
        what = arguments?.getString(WHATTAG) ?: ""
        isDone = arguments?.getBoolean(ISDONETAG) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_things_list, container, false)

        //We show floatButton if this fragment shows list
        // of things to do(isDone is false)
        if (isDone) {
            view.add_fab.hide()
        } else {
            view.add_fab.setOnClickListener {
                //Show full screen dialog for adding Thing to do
                AddFragment.newInstance(what).show(parentFragmentManager, null)
            }
        }

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
        DatabaseRepository.get().getThings(what, isDone).observe(
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

    private fun updateAdapter(list: List<Thing>) {
        adapter = ListesRecyclerViewAdapter(list)
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