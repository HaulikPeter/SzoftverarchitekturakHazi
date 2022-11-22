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
import hu.bme.vik.aut.data.Resident
import hu.bme.vik.aut.databinding.FragmentAdminSupplyBinding
import hu.bme.vik.aut.ui.admindashboard.adapters.supplylist.SupplyListRecyclerViewAdapter
import hu.bme.vik.aut.data.Supply
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
import hu.bme.vik.aut.service.SuppliesService
import kotlin.concurrent.thread

class AdminSupplyFragment : Fragment(), AddSupplyDialogFragment.AddSupplyDialogFragmentListener, SupplyListRecyclerViewAdapter.SuppliesListRecyclerViewListener {
    val args: AdminSupplyFragmentArgs by navArgs()

    lateinit var binding: FragmentAdminSupplyBinding
    lateinit var suppliesRecyclerViewAdapter: SupplyListRecyclerViewAdapter
    lateinit var loadingProgressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminSupplyBinding.inflate(inflater, container, false)
        loadingProgressBar = binding.suppliesLoadingProgressBar
        loadingProgressBar.visibility = View.VISIBLE
        val suppliesRecyclerView = binding.supplyRecyclerView

        suppliesRecyclerViewAdapter = SupplyListRecyclerViewAdapter(this)
        suppliesRecyclerView.adapter = suppliesRecyclerViewAdapter
        suppliesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadSupplies()
        return binding.root
    }
    private fun loadSupplies() {
        suppliesRecyclerViewAdapter.clearSupplies()
        loadingProgressBar.visibility = View.VISIBLE

        thread {
            SuppliesService.getInstance().getSuppliesInHousehold(args.householdId, object :
                OnResultListener<List<Supply>> {
                override fun onSuccess(supplies: List<Supply>) {
                    requireActivity().runOnUiThread {
                        suppliesRecyclerViewAdapter.setSupplies(supplies)
                        loadingProgressBar.visibility = View.GONE
                    }
                }

                override fun onError(exception: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            context,
                            "Error loading supplies. Error: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingProgressBar.visibility = View.GONE
                    }
                }
            })
        }
    }

    override fun onSupplyAdded(newSupply: Supply) {
        newSupply.householdId = args.householdId
        SuppliesService.getInstance().addSupply(newSupply, object: OnResultListener<String>{
            override fun onSuccess(id: String) {
                newSupply.id = id
                suppliesRecyclerViewAdapter.addSupply(newSupply)
            }

            override fun onError(exception: Exception) {
                Toast.makeText(requireContext(), "Error while creating supply item", Toast.LENGTH_SHORT)
            }
        })

    }

    override fun deleteButtonClickedOnSupplyItem(supply: Supply, onResult: (Boolean) -> Unit) {
        SuppliesService.getInstance().deleteSupply(supply, object: OnResultListener<Boolean>{
            override fun onSuccess(result: Boolean) {
                onResult(result)
            }

            override fun onError(exception: Exception) {
                Toast.makeText(requireContext(), "Error deleting Supply. Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        })
    }

    override fun onSupplyConsumptionChangeClicked(
        supply: Supply,
        changeAmount: Int,
        onResult: (Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}