package hu.bme.vik.aut.data

data class Supply(
    var id: String? = null,
    var householdId: String? = null,
    val name: String,
    val calorie: Long,
    var stock: Long,
    var consumption: Long? = null
)