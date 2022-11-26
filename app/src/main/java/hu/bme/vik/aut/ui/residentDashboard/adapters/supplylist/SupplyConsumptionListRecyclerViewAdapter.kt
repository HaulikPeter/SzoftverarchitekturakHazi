package hu.bme.vik.aut.ui.admindashboard.adapters.supplylist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Supply

class SupplyConsumptionListRecyclerViewAdapter(private val listener: SupplyConsumptionListRecyclerViewListener): RecyclerView.Adapter<SupplyConsumptionListViewHolder>() {
    private val supplies = mutableListOf<Supply>()

    interface  SupplyConsumptionListRecyclerViewListener {
        fun onSupplyConsumptionChangeClicked(supply: Supply, changeAmount: Long, onResult: (Boolean) -> Unit)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplyConsumptionListViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_household_supply_consumption_list, parent, false)
        return SupplyConsumptionListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SupplyConsumptionListViewHolder, position: Int) {
        val supply = supplies[position]

        holder.name.text = supply.name
        holder.calories.text = "${supply.calorie} kcal"
        holder.supplyStock.text = supply.stock.toString()
        if (supply.consumption != null) {
            holder.totalConsumption.text = supply.consumption.toString()
        }

        if (supply.consumptionByUser != null) {
            holder.currentConsumptionByUser.text = supply.consumptionByUser.toString()
        }
        setImageButtons(supply, holder)

        holder.addConsumptionButton.setOnClickListener{
            onSupplyConsumptionChangeButtonClicked(holder, supply, 1)
        }
        holder.removeConsumptionButton.setOnClickListener{
            onSupplyConsumptionChangeButtonClicked(holder, supply, -1)
        }

    }

    private fun setImageButtons(supply: Supply, holder: SupplyConsumptionListViewHolder) {
        enableImageButton(holder.addConsumptionButton)
        enableImageButton(holder.removeConsumptionButton)

        if (supply.consumptionByUser == null) {
            disableImageButton(holder.addConsumptionButton)
            disableImageButton(holder.removeConsumptionButton)
            return
        }

        if (supply.consumptionByUser != null && supply.consumptionByUser!! <= 0 ){
            disableImageButton(holder.removeConsumptionButton)
        }

        if (supply.consumptionByUser != null && supply.consumption != null  && supply.consumption!! >= supply.stock!!) {
            disableImageButton(holder.addConsumptionButton)
        }
    }

    private fun disableImageButton(button: ImageButton) {
        button.isClickable = false
        button.setColorFilter(Color.rgb(100,100,100))
    }
    private fun enableImageButton(button: ImageButton) {
        button.isClickable = true
        button.setColorFilter(Color.rgb(0,0,0))
    }

    private fun onSupplyConsumptionChangeButtonClicked(
        holder: SupplyConsumptionListViewHolder,
        supply: Supply,
        changeAmount: Long
    ) {
        disableImageButton(holder.addConsumptionButton)
        disableImageButton(holder.removeConsumptionButton)
        listener.onSupplyConsumptionChangeClicked(supply, changeAmount) {
            if (it) {
                changeConsumptionAtPosition(supplies.indexOf(supply), changeAmount)
            }
            setImageButtons(supply, holder)
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

    fun clearSupplies() {
        supplies.clear()
        notifyDataSetChanged()
    }

    fun changeSupply(oldSupply: Supply, newSupply: Supply) {
        val position = supplies.indexOf(oldSupply)
        supplies[position] = newSupply
        notifyItemChanged(position)
    }

    private fun changeConsumptionAtPosition(position: Int, amount: Long) {
        val supply = supplies[position]
        supply.consumption = supply.consumption!! + amount
        supply.consumptionByUser = supply.consumptionByUser!! + amount
        changeSupply(supply, supply)
    }
}