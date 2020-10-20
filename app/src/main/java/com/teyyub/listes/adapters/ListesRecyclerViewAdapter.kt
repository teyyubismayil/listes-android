package com.teyyub.listes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.teyyub.listes.R
import com.teyyub.listes.book
import com.teyyub.listes.goal
import com.teyyub.listes.model.Thing
import com.teyyub.listes.movie

//Adapter for recycler view
class ListesRecyclerViewAdapter(private var thingList: List<Thing>) :
    RecyclerView.Adapter<ListesRecyclerViewAdapter.ListesRecyclerViewHolder>() {

    private lateinit var listener: Listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListesRecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listes_card_view, parent, false)
        return ListesRecyclerViewHolder(view)
    }

    override fun getItemCount() = thingList.size

    override fun onBindViewHolder(holder: ListesRecyclerViewHolder, position: Int) {
        //Binding holder
        holder.bind(thingList[position])
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    //Listener for clicks which Activity or Fragment will implement
    interface Listener {
        fun onDidButtonClick(thing: Thing)
        fun onDeleteButtonClick(thing: Thing)
    }

    //ViewHolder for recycler view
    inner class ListesRecyclerViewHolder(private val cardView: View) :
        RecyclerView.ViewHolder(cardView),
        View.OnClickListener {

        private val name: TextView = cardView.findViewById(R.id.name)
        private val details: TextView = cardView.findViewById(R.id.details)
        private val buttonLayout: LinearLayout = cardView.findViewById(R.id.buttons_layout)
        private val deleteButton: MaterialButton = cardView.findViewById(R.id.delete_button)

        //Button which user presses when user done this thing
        private val didButton: MaterialButton = cardView.findViewById(R.id.did_button)

        //Thing object which this cardView will represent
        private lateinit var thing: Thing

        init {
            //Setting listener for cardView
            cardView.setOnClickListener(this)
        }

        //function for binding this ViewHolder
        fun bind(thing: Thing) {
            this.thing = thing

            configureTextViews()
            configureDidButton()
            configureDeleteButton()
        }

        private fun configureTextViews() {
            name.text = thing.name

            if (thing.details.isEmpty()) {
                details.visibility = View.GONE
            } else {
                details.text = thing.details
            }
        }

        private fun configureDidButton() {
            if (this.thing.isDone) {
                //Hiding didButton if this thing is done(isDone is true)
                didButton.visibility = View.GONE
            } else {
                //Setting text for didButton text
                //depending on what value of thing object
                didButton.text = cardView.resources.getString(
                    when (thing.what) {
                        goal -> R.string.achieved
                        book -> R.string.readed
                        movie -> R.string.watched
                        else -> R.string.something
                    }
                )
                //Update this thing in database with isDone property true
                //if user clicked didButton
                didButton.setOnClickListener {
                    listener.onDidButtonClick(thing)
                }
            }
        }

        private fun configureDeleteButton() {
            //Delete Thing from database when
            //deleteButton is pressed
            deleteButton.setOnClickListener {
                listener.onDeleteButtonClick(thing)
            }
        }

        //Showing and hiding buttons if cardView is clicked
        override fun onClick(v: View?) {
            if (buttonLayout.visibility == View.GONE) {
                buttonLayout.visibility = View.VISIBLE
                name.maxLines = Integer.MAX_VALUE
                details.maxLines = Integer.MAX_VALUE
            } else {
                buttonLayout.visibility = View.GONE
                name.maxLines = 1
                details.maxLines = 1
            }
        }
    }
}