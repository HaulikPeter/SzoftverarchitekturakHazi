package hu.bme.vik.aut.ui.residentDashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Supply
import hu.bme.vik.aut.ui.admindashboard.adapters.supplylist.SupplyListViewHolder

class ResidentSupplyAdapter : RecyclerView.Adapter<SupplyListViewHolder>() {

    private val supplies = mutableListOf<Supply>()

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

        holder.tvConsumption.visibility = View.VISIBLE
        holder.tvConsumptionAmount.apply {
            visibility = View.VISIBLE
            text = supply.consumption.toString()
        }

        holder.addButton.visibility = View.VISIBLE
        holder.addButton.setOnClickListener {  }
        holder.deleteButton.setOnClickListener {  }
    }

    override fun getItemCount() = supplies.size
}