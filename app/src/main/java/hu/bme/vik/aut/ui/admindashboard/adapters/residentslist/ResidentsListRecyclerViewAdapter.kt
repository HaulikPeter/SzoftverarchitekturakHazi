package hu.bme.vik.aut.ui.admindashboard.adapters.ResidentsList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Resident

class  ResidentsListRecyclerViewAdapter(val context: Context, val listener: ResidentsListRecyclerViewListener): RecyclerView.Adapter<ResidentsListViewHolder>() {
    interface  ResidentsListRecyclerViewListener {
        fun deleteButtonClickedOnResidentItem(resident: Resident, onResult: (Boolean)->Unit)
    }
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
    fun setResidents(residentsList: List<Resident>) {
        residents.clear()
        addResidents(residentsList)
    }
    private fun removeResidentAtPosition(position: Int) {
        val resident = residents.get(position)
        listener.deleteButtonClickedOnResidentItem(resident){
            if (it) {
                residents.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }
}