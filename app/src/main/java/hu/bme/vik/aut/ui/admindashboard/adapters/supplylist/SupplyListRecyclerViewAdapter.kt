package hu.bme.vik.aut.ui.admindashboard.adapters.supplylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Resident
import hu.bme.vik.aut.data.Supply

class SupplyListRecyclerViewAdapter(val listener: SuppliesListRecyclerViewListener): RecyclerView.Adapter<SupplyListViewHolder>() {
    private val supplies = mutableListOf<Supply>()

    interface  SuppliesListRecyclerViewListener {
        fun deleteButtonClickedOnSupplyItem(supply: Supply, onResult: (Boolean)->Unit)
        fun onSupplyConsumptionChangeClicked(supply: Supply, changeAmount: Int, onResult: (Boolean) -> Unit)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplyListViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_household_supply_list, parent, false)
        return SupplyListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SupplyListViewHolder, position: Int) {
        val supply = supplies[position]

        holder.name.text = supply.name
        holder.calories.text = "${supply.calorie} kcal"
        holder.stock.text = supply.stock.toString()
        holder.deleteButton.setOnClickListener{
            listener.deleteButtonClickedOnSupplyItem(supply){
                if (it) {
                    removeSupplyAtPosition(supplies.indexOf(supply))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return supplies.size
    }

    fun addSupplies(suppliesToAdd: List<Supply>) {
        supplies.addAll(suppliesToAdd)
        notifyDataSetChanged()
    }

    fun addSupply(supply: Supply) {
        supplies.add(supply)
        notifyItemInserted(supplies.size - 1)
    }

    fun setSupplies(suppliesList: List<Supply>) {
        supplies.clear()
        addSupplies(suppliesList)
    }

    private fun removeSupplyAtPosition(position: Int) {
        supplies.removeAt(position)
        notifyItemRemoved(position)
    }
}