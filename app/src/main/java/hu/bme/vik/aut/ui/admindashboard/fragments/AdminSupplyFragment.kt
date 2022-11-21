package hu.bme.vik.aut.ui.admindashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.FragmentAdminOverviewBinding
import hu.bme.vik.aut.databinding.FragmentAdminResidentsBinding
import hu.bme.vik.aut.databinding.FragmentAdminSupplyBinding
import hu.bme.vik.aut.ui.admindashboard.adapters.ResidentsList.ResidentsListRecyclerViewAdapter
import hu.bme.vik.aut.ui.admindashboard.adapters.supplylist.SupplyListRecyclerViewAdapter
import hu.bme.vik.aut.ui.admindashboard.data.Resident
import hu.bme.vik.aut.ui.admindashboard.data.ResidentStatus
import hu.bme.vik.aut.ui.admindashboard.data.Supply
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminSupplyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminSupplyFragment : Fragment(), AddSupplyDialogFragment.AddSupplyDialogFragmentListener {
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
        val residentsListRecyclerView = binding.supplyRecyclerView

        suppliesRecyclerViewAdapter = SupplyListRecyclerViewAdapter()
        residentsListRecyclerView.adapter = suppliesRecyclerViewAdapter
        residentsListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadSupplies()
        return binding.root
        return binding.root;
    }
    private fun loadSupplies() {
        thread {
            val supplies: List<Supply> = listOf(
                Supply(name = "Honey", calorie = 1000, stock = 10),
                Supply(name = "Apple", calorie = 150, stock = 2),
                Supply(name = "Banana", calorie = 300, stock = 6),
                Supply(name = "Pork Chop", calorie = 560, stock = 4),
                Supply(name = "Chop Suey", calorie = 666, stock = 99)

            )
            Thread.sleep(1000)
            if(this.isAdded) {
                requireActivity().runOnUiThread {
                    suppliesRecyclerViewAdapter.addSupplies(supplies)
                    loadingProgressBar.visibility = View.GONE
                }
            }
        }
    }

    companion object {

    }

    override fun onSupplyAdded(newSupply: Supply) {
        suppliesRecyclerViewAdapter.addSupply(newSupply)
    }
}