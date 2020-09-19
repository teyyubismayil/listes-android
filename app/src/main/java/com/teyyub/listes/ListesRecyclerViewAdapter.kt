package com.teyyub.listes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.teyyub.listes.model.Thing
import com.teyyub.listes.repository.ThingRepository

//Adapter for recycler view
class ListesRecyclerViewAdapter(private var thingList: List<Thing>) :
    RecyclerView.Adapter<ListesRecyclerViewAdapter.ListesRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListesRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ListesRecyclerViewHolder(view)
    }

    override fun getItemCount() = thingList.size

    override fun onBindViewHolder(holder: ListesRecyclerViewHolder, position: Int) {
        //Binding holder
        holder.bind(thingList[position])
    }

    //ViewHolder for recycler view
    class ListesRecyclerViewHolder(private val cardView: View) : RecyclerView.ViewHolder(cardView),
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
            //Delete Thing from database when
            //deleteButton is pressed
            deleteButton.setOnClickListener {
                ThingRepository.get().deleteThing(thing)
            }
        }

        //function for binding this ViewHolder
        fun bind(thing: Thing) {
            this.thing = thing

            name.text = thing.name

            if (thing.details.isEmpty()) {
                details.visibility = View.GONE
            } else {
                details.text = thing.details
            }

            configureDidButton()
        }

        //Showing and hiding buttons if cardView is clicked
        override fun onClick(v: View?) {
            if (buttonLayout.visibility == View.GONE) {
                buttonLayout.visibility = View.VISIBLE
                details.maxLines = Integer.MAX_VALUE
            } else {
                buttonLayout.visibility = View.GONE
                details.maxLines = 1
            }
        }

        private fun configureDidButton() {
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

            if (this.thing.isDone) {
                //Hiding didButton if this thing is done(isDone is true)
                didButton.visibility = View.GONE
            } else {
                //Update this thing in database with isDone property true
                //if user clicked didButton
                didButton.setOnClickListener {
                    val updatedThing = thing
                    updatedThing.isDone = true
                    ThingRepository.get().updateThing(thing)
                }
            }
        }
    }
}