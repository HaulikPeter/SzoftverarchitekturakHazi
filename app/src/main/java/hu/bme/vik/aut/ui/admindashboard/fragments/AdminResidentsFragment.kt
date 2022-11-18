package hu.bme.vik.aut.ui.admindashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.databinding.FragmentAdminResidentsBinding
import hu.bme.vik.aut.ui.admindashboard.adapters.ResidentsList.ResidentsListRecyclerViewAdapter
import hu.bme.vik.aut.ui.admindashboard.data.Resident
import hu.bme.vik.aut.ui.admindashboard.data.ResidentStatus
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminResidentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminResidentsFragment : Fragment(), AddResidentDialogFragment.AddResidentDialogFragmentListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentAdminResidentsBinding
    lateinit var residentsRecyclerViewAdapter: ResidentsListRecyclerViewAdapter
    lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminResidentsBinding.inflate(inflater, container, false)
        loadingProgressBar = binding.residentsLoadingProgressBar
        loadingProgressBar.visibility = View.VISIBLE
        val residentsListRecyclerView = binding.residentsRecyclerView

        residentsRecyclerViewAdapter = ResidentsListRecyclerViewAdapter(requireContext())
        residentsListRecyclerView.adapter = residentsRecyclerViewAdapter
        residentsListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadResidents()
        return binding.root
    }

    private fun loadResidents() {
        val user = Firebase.auth.currentUser!!
        val db = FirebaseDatabase.getInstance().reference
        db.child("users").child(user.uid).child("household_id").get()
            .addOnSuccessListener {
                fillUsersByHousehold(db, it.value.toString())
            }
    }

    private fun fillUsersByHousehold(db: DatabaseReference, householdId: String) {
        db.child("user").orderByChild("household_id").equalTo(householdId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(user: DataSnapshot) {
                    residentsRecyclerViewAdapter.addResident(
                        Resident(
                            user.child("display_name").value.toString(),
                            ResidentStatus.valueOf(user.child("status").value.toString()))
                    )
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error loading residents", Toast.LENGTH_SHORT).show()
                }
            })
    }


//        thread {
//            val residents: List<Resident> = listOf(Resident(name = "Bruce Willis", status = ResidentStatus.HEALTHY),
//                Resident(name = "Helen Mirren", status = ResidentStatus.SICK),
//                Resident(name = "John Malkovich", status = ResidentStatus.SICK),
//                Resident(name = "Morgan Freeman", status = ResidentStatus.HEALTHY))
//            Thread.sleep(1000)
//            if(this.isAdded) {
//                requireActivity().runOnUiThread {
//                    residentsRecyclerViewAdapter.addResidents(residents)
//                    loadingProgressBar.visibility = View.GONE
//                }
//            }
//        }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminResidentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminResidentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }
    }

    override fun onResidentAdded(newResident: Resident) {
        residentsRecyclerViewAdapter.addResident(newResident)
    }
}