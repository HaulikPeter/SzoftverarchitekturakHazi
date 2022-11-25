package hu.bme.vik.aut.ui.admindashboard.adapters.supplylist

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R

class SupplyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val name: TextView
    val calories: TextView
    val stock: TextView
    val deleteButton: ImageButton
    val addButton: ImageButton

    val tvConsumption: TextView
    val tvConsumptionAmount: TextView

    init {
        name = itemView.findViewById(R.id.supply_name)
        calories = itemView.findViewById(R.id.supply_calories)
        stock = itemView.findViewById(R.id.supply_stock)
        deleteButton = itemView.findViewById(R.id.delete_supply_button)
        addButton = itemView.findViewById(R.id.add_supply_button)

        tvConsumption = itemView.findViewById(R.id.tvConsumption)
        tvConsumptionAmount = itemView.findViewById(R.id.tvConsumptionAmount)
    }
}