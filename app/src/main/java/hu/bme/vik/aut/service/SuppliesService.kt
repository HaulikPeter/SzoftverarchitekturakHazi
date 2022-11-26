package hu.bme.vik.aut.service

import com.google.firebase.database.*
import hu.bme.vik.aut.data.Supply
import java.util.UUID

class SuppliesService  private constructor(val db: DatabaseReference) {

    fun changeSupplyStockByAmount(supply: Supply, amount: Long, onResultListener: OnResultListener<Supply>) {
        db.child("supplies").child(supply.householdId!!).child(supply.id!!).child("stock").setValue(ServerValue.increment(amount)).addOnSuccessListener {
            supply.stock += amount
            onResultListener.onSuccess(supply)
        }.addOnFailureListener {
            onResultListener.onError(it)
        }
    }

    fun deleteSupply(supply: Supply, onResultListener: OnResultListener<Boolean>) {
        db.child("users").orderByChild("household_id").equalTo(supply.householdId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(users: DataSnapshot) {
                    val valuesToDelete: HashMap<String, Any?> = HashMap()
                    if (users.value != null) {
                        val usersHashMap: HashMap<String, HashMap<String, Any>> = users.value as HashMap<String,HashMap<String, Any>>
                        for ((userId, userData) in usersHashMap) {
                            try {
                                val consumption: HashMap<String, Any> = userData.getOrDefault("consumption", null)  as HashMap<String, Any>
                                for((consumption_id, _) in consumption) {
                                    if (userId.isNotEmpty() && consumption_id == supply.id) {
                                        valuesToDelete.put("users/${userId}/consumption/${supply.id}", null)
                                    }
                                }
                            } catch (e: Exception) {
                                continue
                            }
                        }
                    }
                    valuesToDelete.put("supplies/${supply.householdId}/${supply.id}", null)
                    db.updateChildren(valuesToDelete).addOnSuccessListener {
                        onResultListener.onSuccess(true)
                    }.addOnFailureListener {
                        onResultListener.onError(it)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onResultListener.onError(Exception(error.message))
                }
            })
    }


    fun fetchConsumptionForSupply(supply: Supply, residentId: String?, onResultListener: OnResultListener<Supply>) {
        db.child("users").orderByChild("household_id").equalTo(supply.householdId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(users: DataSnapshot) {
                var consumptionCounter: Long = 0
                var userConsumptionCounter: Long = 0
                if (users.value != null) {
                    val usersHashMap: HashMap<String, HashMap<String, Any>> = users.value as HashMap<String,HashMap<String, Any>>
                    for ((userId, userData) in usersHashMap) {
                        try {
                            val consumption: HashMap<String, Any> = userData.getOrDefault("consumption", null)  as HashMap<String, Any>
                            for((consumption_id, consuptionCountOfUser) in consumption) {
                                if (userId != null && consumption_id == supply.id) {

                                    if (userId == residentId) {
                                        userConsumptionCounter += consuptionCountOfUser as Long
                                    }
                                    consumptionCounter +=  consuptionCountOfUser as Long
                                }
                            }
                        } catch (e: Exception) {
                            continue
                        }
                    }
                }
                supply.consumption = consumptionCounter
                supply.consumptionByUser = userConsumptionCounter
                onResultListener.onSuccess(supply)
            }

            override fun onCancelled(error: DatabaseError) {
                onResultListener.onError(Exception(error.message))
            }
        })
    }

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

                    } catch (e: Exception) {
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