package hu.bme.vik.aut.service

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import hu.bme.vik.aut.data.Resident
import hu.bme.vik.aut.data.ResidentStatus
import hu.bme.vik.aut.data.Supply

class ResidentsService private constructor(private val db: DatabaseReference) {

    fun removeResidentFromHousehold(residentId: String, onResultListener: OnResultListener<Boolean>) {
        db.child("users").child(residentId).removeValue().addOnSuccessListener {
            onResultListener.onSuccess(true)
        }.addOnFailureListener {
            onResultListener.onError(it)
        }
    }

    fun getResidentsInHousehold(householdId: String, onResultListener: OnResultListener<List<Resident>> ) {
        db.child("users").orderByChild("household_id").equalTo(householdId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(users: DataSnapshot) {
                    val residents = mutableListOf<Resident>()
                    if (users.value == null) {
                        onResultListener.onSuccess(residents)
                        return
                    }
                    val usersHashMap: HashMap<String, HashMap<String, Any>> = users.value as HashMap<String, HashMap<String, Any>>
                    for ((userId,userData) in usersHashMap) {
                        val resident = Resident(
                            id = userId,
                            name =  userData.getOrDefault("display_name", "None").toString(),
                            status = ResidentStatus.valueOf(userData.getOrDefault("status", ResidentStatus.HEALTHY.toString()).toString())
                        )
                        residents.add(resident)
                    }
                    onResultListener.onSuccess(residents)
                }
                override fun onCancelled(error: DatabaseError) {
                    onResultListener.onError(Exception(error.message))
                }
            })
    }

    fun initUserData(user: FirebaseUser, onResultListener: OnResultListener<Boolean>) {
        db.child("users").child(user.uid).child("display_name")
            .setValue(user.email.orEmpty().substringBefore('@')).addOnSuccessListener {
            onResultListener.onSuccess(true)
        }.addOnFailureListener {
            onResultListener.onError(it)
        }
    }

    fun setIsAdminForResident(residentId: String, isAdmin: Boolean, onResultListener: OnResultListener<Boolean>) {
        db.child("users").child(residentId).child("admin").setValue(isAdmin).addOnSuccessListener {
            onResultListener.onSuccess(true)
        }.addOnFailureListener {
            onResultListener.onError(it)
        }
    }

    fun setIsHouseholdIDForResident(residentId: String, householdId: String, onResultListener: OnResultListener<Boolean>) {
        db.child("users").child(residentId).child("household_id").setValue(householdId).addOnSuccessListener {
            onResultListener.onSuccess(true)
        }.addOnFailureListener {
            onResultListener.onError(it)
        }
    }

    fun getResidentStatus(residentId: String, onResultListener: OnResultListener<ResidentStatus>) {
        db.child("users").child(residentId).child("status").get().addOnSuccessListener {
            when (it.value) {
                "HEALTHY" -> onResultListener.onSuccess(ResidentStatus.HEALTHY)
                "SICK" -> onResultListener.onSuccess(ResidentStatus.SICK)
                else -> onResultListener.onError(Exception("Status error!"))
            }
        }.addOnFailureListener { onResultListener.onError(it) }
    }

    fun setResidentStatus(residentId: String, result: ResidentStatus, onResultListener: OnResultListener<Boolean>) {
        db.child("users").child(residentId).child("status").setValue(result)
            .addOnSuccessListener { onResultListener.onSuccess(true) }
            .addOnFailureListener { onResultListener.onError(it) }
    }

    fun addConsumption(residentId: String, supply: Supply, amount: Long, onResultListener: OnResultListener<Boolean>) {
        SuppliesService.getInstance().fetchConsumptionForSupply(supply, residentId, object: OnResultListener<Supply> {
            override fun onSuccess(supply: Supply) {
                val newConsumption = supply.consumption!! + amount
                if(newConsumption >= 0 && newConsumption <= supply.stock )
                {
                    val newConsumptionByUser = supply.consumptionByUser!! + amount
                    if (newConsumptionByUser >= 0 && newConsumptionByUser <= supply.stock) {
                        db.child("users").child(residentId).child("consumption").child(supply.id!!).setValue(newConsumptionByUser).addOnSuccessListener {
                            onResultListener.onSuccess(true)
                        }.addOnFailureListener {
                            onResultListener.onError(it)
                        }
                    }
                    else {
                        onResultListener.onSuccess(false)
                        return
                    }
                }else {
                    onResultListener.onSuccess(false)
                    return
                }
            }
            override fun onError(exception: Exception) {
               onResultListener.onError(exception)
            }
        })
        db.child("users").child(residentId)
    }

    companion object{
        private var INSTANCE : ResidentsService? = null

        fun getInstance(): ResidentsService
        {
            if(INSTANCE == null)
            {
                INSTANCE = ResidentsService(FirebaseDatabase.getInstance().reference)
            }

            return INSTANCE!!
        }

        fun destroyInstance()
        {
            INSTANCE = null
        }
    }
}