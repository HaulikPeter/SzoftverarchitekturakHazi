package hu.bme.vik.aut.ui.householdselector.adapters.householdlist

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import hu.bme.vik.aut.R

class HouseholdListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val householdWholeItemWrapper: ConstraintLayout
    val householdItemWrapper: ConstraintLayout
    val name: TextView
    val deleteButton: ImageButton

    init {
        name = itemView.findViewById(R.id.household_name)
        householdItemWrapper = itemView.findViewById(R.id.household_item_wrapper)
        householdWholeItemWrapper = itemView.findViewById(R.id.household_list_item)
        deleteButton = itemView.findViewById(R.id.delete_household_button)
    }
}