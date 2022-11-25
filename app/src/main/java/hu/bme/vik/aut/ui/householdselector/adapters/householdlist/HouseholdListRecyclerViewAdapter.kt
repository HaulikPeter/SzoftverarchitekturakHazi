package hu.bme.vik.aut.ui.householdselector.adapters.householdlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Household


class HouseholdListRecyclerViewAdapter  (val context: Context, val listener: HouseholdListRecyclerViewListener, private val should_show_delete_button: Boolean): RecyclerView.Adapter<HouseholdListViewHolder>() {
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

        if (should_show_delete_button) {
            holder.householdItemWrapper.setOnClickListener {
                listener.householdItemClicked(household)
            }
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener {
                removeHouseholdAtPosition(households.indexOf(household))
            }
        } else {
            holder.deleteButton.visibility = View.INVISIBLE
            holder.householdItemWrapper.setOnClickListener {
                listener.householdItemClicked(household)
            }
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