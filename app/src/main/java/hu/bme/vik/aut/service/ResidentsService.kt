package hu.bme.vik.aut.service

import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import hu.bme.vik.aut.ui.admindashboard.data.Resident
import hu.bme.vik.aut.ui.admindashboard.data.ResidentStatus
import kotlin.coroutines.suspendCoroutine

class ResidentsService private constructor(val db: DatabaseReference) {


    fun removeResidentFromHousehold(residentId: String, onResultListener: OnResultListener<Boolean>) {
        db.get()
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