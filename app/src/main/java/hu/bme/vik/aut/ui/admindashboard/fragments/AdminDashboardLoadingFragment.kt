package hu.bme.vik.aut.ui.admindashboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.vik.aut.data.Supply
import hu.bme.vik.aut.databinding.FragmentAdminDashboardLoadingBinding
import hu.bme.vik.aut.databinding.FragmentAdminOverviewBinding
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.SuppliesService
import hu.bme.vik.aut.ui.admindashboard.adapters.supplyrestocklist.SupplyRestockListRecyclerViewAdapter

class AdminDashboardLoadingFragment : Fragment() {
    val args: AdminSupplyFragmentArgs by navArgs()

    lateinit var binding: FragmentAdminDashboardLoadingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminDashboardLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

}

