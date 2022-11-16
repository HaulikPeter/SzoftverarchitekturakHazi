package hu.bme.vik.aut.ui.admindashboard.adapters.residentslist

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R

class ResidentsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val name: TextView
    val status: TextView
    val deleteButton: ImageButton
    init {
        name = itemView.findViewById(R.id.resident_name)
        status = itemView.findViewById(R.id.resident_status)
        deleteButton =  itemView.findViewById(R.id.delete_resident_button)
    }
}