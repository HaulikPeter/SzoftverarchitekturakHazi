package hu.bme.vik.aut.data

import hu.bme.vik.aut.R

enum class ResidentStatus(private val value: String, private val color: Int) {
    HEALTHY("HEALTHY", R.color.healthy_green),
    SICK("SICK", R.color.sick_red);

    override fun toString(): String {
        return value
    }

    fun getColorId(): Int {
        return color
    }

}