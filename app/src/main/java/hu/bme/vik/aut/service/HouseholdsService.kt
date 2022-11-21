package hu.bme.vik.aut.service

import com.google.firebase.database.*
import hu.bme.vik.aut.data.Household
import java.util.UUID

class HouseholdsService private constructor(val db: DatabaseReference) {

    fun removeHousehold(householdId: String, onResultListener: OnResultListener<Boolean>) {
        db.child("users").orderByChild("household_id").equalTo(householdId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(users: DataSnapshot) {

                    val valuesToDelete: HashMap<String, Any?> = HashMap()
                    if (users.value != null) {
                        val usersHashMap: HashMap<String, Any> = users.value as HashMap<String, Any>
                        for ((userId,_) in usersHashMap) {
                            if (userId != null) {
                                valuesToDelete.put("users/${userId}", null)
                            }

                        }
                    }
                    valuesToDelete.put("households/${householdId}", null)
                    valuesToDelete.put("supplies/${householdId}", null)
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

    fun addHousehold (household: Household, onResultListener: OnResultListener<String>) {
        val id = UUID.randomUUID().toString()
        val householdValues:  HashMap<String, String?> = HashMap()
        householdValues.put("name", household.name)
        householdValues.put("admin_id", household.adminId)

        db.updateChildren(mapOf(Pair("households/${id}", householdValues), Pair("supplies/${id}", ""))).addOnSuccessListener {
            onResultListener.onSuccess(id)
        }.addOnFailureListener{
            onResultListener.onError(it)
        }


    }

    fun getHouseholdsForAdmin (adminId: String, onResultListener: OnResultListener<List<Household>>) {
        db.child("households").orderByChild("admin_id").equalTo(adminId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(householdsFromDB: DataSnapshot) {
                    val households = mutableListOf<Household>()
                    if (householdsFromDB.value == null) {
                        onResultListener.onSuccess(households)
                        return
                    }
                    val householdsHashMap: HashMap<String, HashMap<String, Any>> = householdsFromDB.value as HashMap<String, HashMap<String, Any>>
                    for ((householdId,householdData) in householdsHashMap) {
                        val household = Household(
                            id = householdId,
                            name =  householdData.getOrDefault("name", "None").toString(),
                            adminId = adminId
                        )
                        households.add(household)
                    }
                    onResultListener.onSuccess(households)
                }
                override fun onCancelled(error: DatabaseError) {
                    onResultListener.onError(Exception(error.message))
                }
            })
    }
    companion object{
        private var INSTANCE : HouseholdsService? = null

        fun getInstance(): HouseholdsService
        {
            if(INSTANCE == null)
            {
                INSTANCE = HouseholdsService(FirebaseDatabase.getInstance().reference)
            }

            return INSTANCE!!
        }

        fun destroyInstance()
        {
            INSTANCE = null
        }
    }
}