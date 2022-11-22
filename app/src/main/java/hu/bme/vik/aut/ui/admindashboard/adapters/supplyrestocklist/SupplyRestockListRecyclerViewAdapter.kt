package hu.bme.vik.aut.ui.admindashboard.adapters.supplyrestocklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Supply
import hu.bme.vik.aut.ui.admindashboard.adapters.supplylist.SupplyListViewHolder
import kotlin.math.ceil

class SupplyRestockListRecyclerViewAdapter(val listener: SupplyRestockListRecyclerViewListener): RecyclerView.Adapter<SupplyRestockListViewHolder>() {
    interface  SupplyRestockListRecyclerViewListener {
        fun restockButtonClickedOnSupplyItem(supply: Supply, onResult: (Boolean)->Unit)
    }
    private val supplies = mutableListOf<Supply>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplyRestockListViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_household_supply_restock_list, parent, false)
        return SupplyRestockListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SupplyRestockListViewHolder, position: Int) {
        val supply = supplies[position]

        holder.name.text = supply.name

        if (supply.stock <= 0) {
            holder.supplyOutOfStockInDays.text = " is out of stock"
            holder.restockButton.isEnabled = true
        } else if (supply.consumption == null) {
            holder.supplyOutOfStockInDays.text = " loading..."
            holder.restockButton.isEnabled = false

        } else if (supply.consumption!! <= 0) {
            holder.supplyOutOfStockInDays.text = " has no consumption"
            holder.restockButton.isEnabled = true
        } else {
            val daysTillOutOfStock: Long = ceil(supply.stock.toDouble() / supply.consumption!!.toDouble()).toLong()
            holder.supplyOutOfStockInDays.text = " will be out of stock in ${daysTillOutOfStock} day${ if(daysTillOutOfStock <= 1) "" else "s"}"
            holder.restockButton.isEnabled = true
        }
        holder.restockButton.setOnClickListener{
            listener.restockButtonClickedOnSupplyItem(supply){
                if (it) {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return supplies.size
    }
    fun addSupply(supply: Supply) {
        supplies.add(supply)
        notifyItemInserted(supplies.size - 1)
    }

    fun addSupplies(suppliesToAdd: List<Supply>) {
        supplies.addAll(suppliesToAdd)
        notifyDataSetChanged()
    }

    fun changeSupply(oldSupply: Supply, newSupply: Supply) {
        val position = supplies.indexOf(oldSupply)
        supplies[position] =  newSupply
        notifyItemChanged(position)
    }

    fun setSupplies(suppliesList: List<Supply>) {
        supplies.clear()
        addSupplies(suppliesList)
    }

    fun clearSupplies() {
        supplies.clear()
        notifyDataSetChanged()
    }
}