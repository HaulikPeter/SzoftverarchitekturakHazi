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
import hu.bme.vik.aut.data.Supply

import hu.bme.vik.aut.databinding.FragmentAdminOverviewBinding
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.SuppliesService
import hu.bme.vik.aut.ui.admindashboard.adapters.supplyrestocklist.SupplyRestockListRecyclerViewAdapter
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match



/**
 * A simple [Fragment] subclass.
 * Use the [AdminOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminOverviewFragment : Fragment(),SupplyRestockListRecyclerViewAdapter.SupplyRestockListRecyclerViewListener, SupplyRestockDialogFragment.SupplyRestockDialogFragmentListener {
    val args: AdminSupplyFragmentArgs by navArgs()

    lateinit var binding: FragmentAdminOverviewBinding
    lateinit var supplyRestockListRecyclerViewAdapter: SupplyRestockListRecyclerViewAdapter
    lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminOverviewBinding.inflate(inflater, container, false)
        loadingProgressBar = binding.suppliesLoadingProgressBar
        loadingProgressBar.visibility = View.VISIBLE
        val suppliesRecyclerView = binding.supplyRecyclerView

        supplyRestockListRecyclerViewAdapter = SupplyRestockListRecyclerViewAdapter(this)
        suppliesRecyclerView.adapter = supplyRestockListRecyclerViewAdapter
        suppliesRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        loadSupplies()
        return binding.root
    }
    val lockObj = Object()

    private fun loadSupplies() {
        supplyRestockListRecyclerViewAdapter.clearSupplies()
        loadingProgressBar.visibility = View.VISIBLE
        thread {
            SuppliesService.getInstance().getSuppliesInHousehold(args.householdId, object :
                OnResultListener<List<Supply>> {
                override fun onSuccess(supplies: List<Supply>) {
                    requireActivity().runOnUiThread{
                        supplyRestockListRecyclerViewAdapter.setSupplies(supplies)
                        loadingProgressBar.visibility = View.GONE
                    }
                    for (supply in supplies) {
                        SuppliesService.getInstance().addConsumptionForSupply(supply, object: OnResultListener<Supply> {
                            override fun onSuccess(supplyWithConsumption: Supply) {
                                requireActivity().runOnUiThread {
                                    supplyRestockListRecyclerViewAdapter.changeSupply(
                                        supply,
                                        supplyWithConsumption
                                    )
                                }
                            }

                            override fun onError(exception: Exception) {
                                supply.consumption = 0
                                requireActivity().runOnUiThread {
                                    supplyRestockListRecyclerViewAdapter.changeSupply(
                                        supply,
                                        supply
                                    )
                                }
                            }
                        })
                    }
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(
                        context,
                        "Error loading supplies. Error: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().runOnUiThread {
                        loadingProgressBar.visibility = View.GONE
                    }
                }
            })
        }
    }



    override fun restockButtonClickedOnSupplyItem(supply: Supply, onResult: (Boolean) -> Unit) {
        SupplyRestockDialogFragment.newInstance(this, supply).show(parentFragmentManager, SupplyRestockDialogFragment.TAG)
    }

    override fun onSupplyRestock(supply: Supply, addedStockAmount: Long) {
        thread {
            SuppliesService.getInstance().changeSupplyStockByAmount(supply, addedStockAmount, object: OnResultListener<Supply> {
                override fun onSuccess(newSupply: Supply) {
                    requireActivity().runOnUiThread{
                        loadSupplies()
                    }
                }

                override fun onError(exception: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            context,
                            "Error incrementing supply stock. Error: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

    }
}