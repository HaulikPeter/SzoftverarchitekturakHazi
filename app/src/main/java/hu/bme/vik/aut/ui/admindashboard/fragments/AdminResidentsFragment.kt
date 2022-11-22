package hu.bme.vik.aut.ui.admindashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.vik.aut.databinding.FragmentAdminResidentsBinding
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
import hu.bme.vik.aut.ui.admindashboard.adapters.ResidentsList.ResidentsListRecyclerViewAdapter
import hu.bme.vik.aut.data.Resident
import kotlin.concurrent.thread


class AdminResidentsFragment : Fragment(), ResidentsListRecyclerViewAdapter.ResidentsListRecyclerViewListener{
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
        residentsRecyclerViewAdapter.clearResidents()
        loadingProgressBar.visibility = View.VISIBLE

        thread {


            ResidentsService.getInstance().getResidentsInHousehold(
                args.householdId,
                object : OnResultListener<List<Resident>> {
                    override fun onSuccess(result: List<Resident>) {
                        requireActivity().runOnUiThread{
                            residentsRecyclerViewAdapter.setResidents(result)
                            loadingProgressBar.visibility = View.GONE
                        }
                    }

                    override fun onError(exception: Exception) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                context,
                                "Error loading residents. Error: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadingProgressBar.visibility = View.GONE
                        }
                    }
                })
        }
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