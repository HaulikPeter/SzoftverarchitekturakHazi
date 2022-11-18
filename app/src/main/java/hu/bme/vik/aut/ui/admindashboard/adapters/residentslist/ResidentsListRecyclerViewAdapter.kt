package hu.bme.vik.aut.ui.admindashboard.adapters.ResidentsList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.ui.admindashboard.data.Resident

class ResidentsListRecyclerViewAdapter(val context: Context): RecyclerView.Adapter<ResidentsListViewHolder>() {
    private val residents = mutableListOf<Resident>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentsListViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_household_residents_list, parent, false)
        return ResidentsListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ResidentsListViewHolder, position: Int) {
        val resident = residents[position]

        holder.name.text = resident.name
        holder.status.text = resident.status.toString()
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, resident.status.getColorId()))
        holder.deleteButton.setOnClickListener{
            removeResidentAtPosition(residents.indexOf(resident))
        }
    }

    override fun getItemCount(): Int {
        return residents.size
    }

    fun addResidents(residentsToAdd: List<Resident>) {
        residents.addAll(residentsToAdd)
        notifyDataSetChanged()
    }

    fun addResident(resident: Resident) {
        residents.add(resident)
        notifyItemInserted(residents.size - 1)
    }

    private fun removeResidentAtPosition(position: Int) {
        residents.removeAt(position)
        notifyItemRemoved(position)

    }
}