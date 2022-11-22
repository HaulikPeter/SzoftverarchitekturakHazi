package hu.bme.vik.aut.ui.admindashboard.adapters.supplyrestocklist

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R

class SupplyRestockListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val name: TextView
    val supplyOutOfStockInDays: TextView
    val restockButton: Button

    init {
        name = itemView.findViewById(R.id.supply_name)
        supplyOutOfStockInDays = itemView.findViewById(R.id.supply_stock_remaining_days)
        restockButton = itemView.findViewById(R.id.restock_supply_button)
    }
}