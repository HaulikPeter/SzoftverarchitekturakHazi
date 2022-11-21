package hu.bme.vik.aut.ui.householdselector.adapters.householdlist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Household
import hu.bme.vik.aut.ui.admindashboard.AdminDashboardActivity


class HouseholdListRecyclerViewAdapter  (val context: Context, val listener: HouseholdListRecyclerViewListener): RecyclerView.Adapter<HouseholdListViewHolder>() {
    interface  HouseholdListRecyclerViewListener {
        fun deleteButtonClickedOnHouseholdItem(household: Household, onResult: (Boolean)->Unit)
        fun householdItemClicked(household: Household)
    }

    private val households = mutableListOf<Household>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseholdListViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_household_list, parent, false)
        return HouseholdListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HouseholdListViewHolder, position: Int) {
        val household = households[position]

        holder.name.text = household.name
        holder.householdItemWrapper.setOnClickListener{
            listener.householdItemClicked(household)
        }

        holder.deleteButton.setOnClickListener{
            removeHouseholdAtPosition(households.indexOf(household))
        }
    }

    override fun getItemCount(): Int {
        return households.size
    }

    fun addHouseholds(householdsToAdd: List<Household>) {
        households.addAll(householdsToAdd)
        notifyDataSetChanged()
    }

    fun addHousehold(household: Household) {
        households.add(household)
        notifyItemInserted(households.size - 1)
    }
    fun setHouseholds(householdsList: List<Household>) {
        households.clear()
        addHouseholds(householdsList)
    }

    private fun removeHouseholdAtPosition(position: Int) {
        val household = households.get(position)
        listener.deleteButtonClickedOnHouseholdItem(household){
            if (it) {
                households.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }
}