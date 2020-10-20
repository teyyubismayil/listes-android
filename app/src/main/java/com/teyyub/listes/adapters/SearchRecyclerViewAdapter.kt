package com.teyyub.listes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teyyub.listes.R
import com.teyyub.listes.model.Thing

//Adapter for recycler view
class SearchRecyclerViewAdapter(private var thingList: List<Thing>) :
    RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchRecyclerViewHolder>() {

    private lateinit var listener: Listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_card_view, parent, false)
        return SearchRecyclerViewHolder(view)
    }

    override fun getItemCount() = thingList.size

    override fun onBindViewHolder(holder: SearchRecyclerViewHolder, position: Int) {
        //Binding holder
        holder.bind(thingList[position])
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onAddButtonClicked(thing: Thing)
    }

    //ViewHolder for recycler view
    inner class SearchRecyclerViewHolder(private val cardView: View) :
        RecyclerView.ViewHolder(cardView),
        View.OnClickListener {

        private val name: TextView = cardView.findViewById(R.id.name)
        private val details: TextView = cardView.findViewById(R.id.details)
        private val addButton: Button = cardView.findViewById(R.id.add_button)

        //Thing object which this cardView will represent
        private lateinit var thing: Thing

        init {
            //Setting listener for cardView
            cardView.setOnClickListener(this)
        }

        //function for binding this ViewHolder
        fun bind(thing: Thing) {
            this.thing = thing

            name.text = thing.name
            details.text = thing.details

            //Add Thing from database when
            addButton.setOnClickListener {
                it.isEnabled = false
                (it as Button).text = cardView.resources.getString(R.string.added)
                listener.onAddButtonClicked(thing)
            }
        }

        //Showing and hiding buttons if cardView is clicked
        override fun onClick(v: View?) {
            if (details.maxLines == 1) {
                name.maxLines = Integer.MAX_VALUE
                details.maxLines = Integer.MAX_VALUE
            } else {
                name.maxLines = 1
                details.maxLines = 1
            }
        }
    }
}