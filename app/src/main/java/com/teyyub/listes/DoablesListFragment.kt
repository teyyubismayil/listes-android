package com.teyyub.listes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.teyyub.listes.adapters.ListesRecyclerViewAdapter
import com.teyyub.listes.model.Doable
import com.teyyub.listes.utils.viewModelFactory
import kotlinx.android.synthetic.main.fragment_doables_list.view.*

private const val WHATTAG = "doable-what"
private const val ISDONETAG = "doable-isdone"

//Fragment which shows list of doables
class DoablesListFragment : Fragment() {

    //Adapter for recyclerView
    //First we pass emptyList until we get list of Doables from LiveData
    private var adapter: ListesRecyclerViewAdapter =
        ListesRecyclerViewAdapter(emptyList())

    //what and isDone properties of Doable objects to show
    //They are passed to newInstance(..) function
    // and stored in fragment's arguments bundle
    private lateinit var what: String
    private var isDone: Boolean = false

    private lateinit var viewModel: DoablesListViewModel

    private lateinit var addFab: FloatingActionButton

    private lateinit var listener: Listener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrieving values from arguments bundle
        what = arguments?.getString(WHATTAG) ?: ""
        isDone = arguments?.getBoolean(ISDONETAG) ?: false

        //viewModel
        viewModel = ViewModelProvider(
            this,
            viewModelFactory { DoablesListViewModel(what, isDone) }
        ).get(DoablesListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doables_list, container, false)

        addFab = view.add_fab
        configureAddFab()

        //Configuring recyclerView
        view.recycler_view.layoutManager = LinearLayoutManager(context)
        view.recycler_view.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Getting list of Doable objects from database
        //We scope lifetime of Observer to lifetime
        // of lifecycle of fragment view
        viewModel.doablesLiveData.observe(
            viewLifecycleOwner,
            Observer { doables ->
                doables?.let {
                    //We update adapter to show Doables
                    // which we got from database
                    updateAdapter(doables)
                }
            }
        )
    }

    private fun configureAddFab() {
        //We show floatButton if this fragment shows list
        // of doables to do(isDone is false)
        if (isDone) {
            addFab.hide()
        } else {
            addFab.setOnClickListener {
                listener.onAddButtonClick()
            }
        }
    }

    private fun updateAdapter(list: List<Doable>) {
        adapter = ListesRecyclerViewAdapter(list)

        adapter.setListener(object : ListesRecyclerViewAdapter.Listener{
            override fun onDidButtonClick(doable: Doable) {
                viewModel.didDoable(doable)
            }
            override fun onDeleteButtonClick(doable: Doable) {
                viewModel.deleteDoable(doable)
            }

        })

        requireView().recycler_view.adapter = adapter
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    //Interface which will be implemented by hosting activity
    interface Listener {
        fun onAddButtonClick()
    }

    companion object {
        //Static method for creating an instance of DoablesListFragment
        //and putting passed arguments in fragment arguments bundle
        fun newInstance(what: String, isDone: Boolean): DoablesListFragment {
            val args = Bundle().apply {
                putString(WHATTAG, what)
                putBoolean(ISDONETAG, isDone)
            }

            return DoablesListFragment().apply {
                arguments = args
            }
        }
    }
}