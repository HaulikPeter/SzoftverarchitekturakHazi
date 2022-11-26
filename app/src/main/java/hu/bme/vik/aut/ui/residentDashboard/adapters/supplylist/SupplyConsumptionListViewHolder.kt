package hu.bme.vik.aut.ui.admindashboard.adapters.supplylist

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R

class SupplyConsumptionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val name: TextView
    val calories: TextView


    val removeConsumptionButton: ImageButton
    val addConsumptionButton: ImageButton

    val supplyStock: TextView
    val currentConsumptionByUser: TextView
    val totalConsumption: TextView

    init {
        name = itemView.findViewById(R.id.supply_name)
        calories = itemView.findViewById(R.id.supply_calories)
        removeConsumptionButton = itemView.findViewById(R.id.remove_supply_consumption_button)
        addConsumptionButton = itemView.findViewById(R.id.add_supply_consumption_button)

        supplyStock = itemView.findViewById(R.id.supply_stock)
        currentConsumptionByUser = itemView.findViewById(R.id.current_supply_consumption)
        totalConsumption = itemView.findViewById(R.id.supply_total_consumption)

    }
}