package com.teyyub.listes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.teyyub.listes.R
import com.teyyub.listes.model.Doable
import com.teyyub.listes.utils.setImageUrl

//Adapter for recycler view
class ListesRecyclerViewAdapter(private var doableList: List<Doable>) :
    RecyclerView.Adapter<ListesRecyclerViewAdapter.ListesRecyclerViewHolder>() {

    private lateinit var listener: Listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListesRecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.listes_card_view, parent, false)
        return ListesRecyclerViewHolder(view)
    }

    override fun getItemCount() = doableList.size

    override fun onBindViewHolder(holder: ListesRecyclerViewHolder, position: Int) {
        //Binding holder
        holder.bind(doableList[position])
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    //Listener for clicks which Activity or Fragment will implement
    interface Listener {
        fun onDidButtonClick(doable: Doable)
        fun onDeleteButtonClick(doable: Doable)
    }

    //ViewHolder for recycler view
    inner class ListesRecyclerViewHolder(private val cardView: View)
        : RecyclerView.ViewHolder(cardView), View.OnClickListener {

        private val name: TextView = cardView.findViewById(R.id.name)
        private val details: TextView = cardView.findViewById(R.id.details)
        private val buttonLayout: LinearLayout = cardView.findViewById(R.id.buttons_layout)
        private val deleteButton: MaterialButton = cardView.findViewById(R.id.delete_button)
        private val poster: ImageView = cardView.findViewById(R.id.poster_imageview)

        //Button which user presses when user done this doable
        private val didButton: MaterialButton = cardView.findViewById(R.id.did_button)

        //Doable object which this cardView will represent
        private lateinit var doable: Doable

        init {
            //Setting listener for cardView
            cardView.setOnClickListener(this)
        }

        //Function for binding this ViewHolder
        fun bind(doable: Doable) {
            this.doable = doable

            //Setting poster image
            poster.setImageUrl(doable.imageUrl)

            configureTextViews()
            configureDidButton()
            configureDeleteButton()
        }

        private fun configureTextViews() {
            name.text = doable.name

            if (doable.details.isEmpty()) {
                details.visibility = View.GONE
            } else {
                details.text = doable.details
            }
        }

        private fun configureDidButton() {
            if (this.doable.isDone) {
                //Hiding didButton if this doable is done(isDone is true)
                didButton.visibility = View.GONE
            } else {
                //Setting text for didButton text
                //depending on 'what' value of doable object
                didButton.text = cardView.resources.getString(
                    when (doable.what) {
                        Doable.DOABLE_GOAL -> R.string.achieved
                        Doable.DOABLE_BOOK -> R.string.readed
                        Doable.DOABLE_MOVIE -> R.string.watched
                        else -> R.string.something
                    }
                )
                //Update this doable in database with isDone property true
                //if user clicked didButton
                didButton.setOnClickListener {
                    listener.onDidButtonClick(doable)
                }
            }
        }

        private fun configureDeleteButton() {
            //Delete doable from database when
            //deleteButton is pressed
            deleteButton.setOnClickListener {
                listener.onDeleteButtonClick(doable)
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