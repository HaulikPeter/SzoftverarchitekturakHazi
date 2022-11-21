package hu.bme.vik.aut.service

import com.google.firebase.database.*
import hu.bme.vik.aut.data.Resident
import hu.bme.vik.aut.data.ResidentStatus
import hu.bme.vik.aut.data.Supply
import java.util.UUID

class SuppliesService  private constructor(val db: DatabaseReference) {

    fun addSupply(supply: Supply,  onResultListener: OnResultListener<String>) {
        val id = UUID.randomUUID().toString()
        val supplyValues:  HashMap<String, Any?> = HashMap()
        supplyValues.put("name", supply.name)
        supplyValues.put("calorie", supply.calorie)
        supplyValues.put("stock", supply.stock)

        db.child("supplies")
            .child(supply.householdId!!)
            .child(id)
            .updateChildren(supplyValues)
            .addOnSuccessListener {
                onResultListener.onSuccess(id)
            }.addOnFailureListener{
                onResultListener.onError(it)
            }

    }
    fun getSuppliesInHousehold(householdId: String, onResultListener: OnResultListener<List<Supply>>) {
        db.child("supplies").orderByKey().equalTo(householdId).addListenerForSingleValueEvent(object :
            ValueEventListener {
                override fun onDataChange(suppliesFromDB: DataSnapshot) {
                    val supplies = mutableListOf<Supply>()
                    if (suppliesFromDB.value == null) {
                        onResultListener.onSuccess(supplies)
                        return
                    }
                    try {
                        val householdSuppliesHashMap: HashMap<String, Any> = suppliesFromDB.value as HashMap<String, Any>
                        for ((household, suppliesHashMap) in householdSuppliesHashMap) {
                            for ((supplyId, supplyData) in suppliesHashMap as HashMap<String, HashMap<String, Any>>) {
                                val supply = Supply(
                                    id = supplyId,
                                    householdId = householdId,
                                    name =  supplyData.getOrDefault("name", "None").toString(),
                                    calorie = supplyData.getOrDefault("calorie", 0) as Long,
                                    stock = supplyData.getOrDefault("stock", 0) as Long
                                )
                                supplies.add(supply)
                            }
                        }
                        onResultListener.onSuccess(supplies)
                    }catch (e: Exception) {
                        onResultListener.onSuccess(mutableListOf<Supply>())
                        return
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    companion object{
        private var INSTANCE : SuppliesService? = null

        fun getInstance(): SuppliesService
        {
            if(INSTANCE == null)
            {
                INSTANCE = SuppliesService(FirebaseDatabase.getInstance().reference)
            }

            return INSTANCE!!
        }

        fun destroyInstance()
        {
            INSTANCE = null
        }
    }
}