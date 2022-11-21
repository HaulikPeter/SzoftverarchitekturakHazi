package hu.bme.vik.aut.ui.admindashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import hu.bme.vik.aut.R

import hu.bme.vik.aut.databinding.FragmentAdminOverviewBinding

// TODO: Rename parameter arguments, choose names that match



/**
 * A simple [Fragment] subclass.
 * Use the [AdminOverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminOverviewFragment : Fragment() {
    val args: AdminOverviewFragmentArgs by navArgs()
    lateinit var binding: FragmentAdminOverviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdminOverviewBinding.inflate(inflater, container, false)
        return binding.root;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminOverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminOverviewFragment().apply {
            }
    }
}