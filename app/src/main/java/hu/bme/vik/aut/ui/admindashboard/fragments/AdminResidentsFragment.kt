package hu.bme.vik.aut.ui.admindashboard.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.databinding.FragmentAdminResidentsBinding
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
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
class AdminResidentsFragment : Fragment(), AddResidentDialogFragment.AddResidentDialogFragmentListener, ResidentsListRecyclerViewAdapter.ResidentsListRecyclerViewListener{
    val args: AdminResidentsFragmentArgs by navArgs()

    lateinit var binding: FragmentAdminResidentsBinding
    lateinit var residentsRecyclerViewAdapter: ResidentsListRecyclerViewAdapter
    lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        residentsRecyclerViewAdapter = ResidentsListRecyclerViewAdapter(requireContext(), this)
        residentsListRecyclerView.adapter = residentsRecyclerViewAdapter
        residentsListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadResidents()
        return binding.root
    }

    private fun loadResidents() {


        ResidentsService.getInstance().getResidentsInHousehold(args.householdId, object : OnResultListener <List<Resident>> {
            override fun onSuccess(result: List<Resident>) {
                residentsRecyclerViewAdapter.setResidents(result)
                loadingProgressBar.visibility = View.GONE
            }

            override fun onError(exception: Exception) {
                Toast.makeText(context, "Error loading residents. Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


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
            }
    }

    override fun onResidentAdded(newResident: Resident) {
        residentsRecyclerViewAdapter.addResident(newResident)
    }

    override fun deleteButtonClickedOnResidentItem(resident: Resident, onResult: (Boolean)->Unit) {
        ResidentsService.getInstance().removeResidentFromHousehold(resident.id, object: OnResultListener<Boolean>{
            override fun onSuccess(result: Boolean) {
                onResult(result)
            }

            override fun onError(exception: Exception) {
                Toast.makeText(context, "Error loading deleting Resident. Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        })
    }
}