package hu.bme.vik.aut.ui.admindashboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.FragmentAddResidentDialogBinding
import hu.bme.vik.aut.ui.admindashboard.data.Resident
import hu.bme.vik.aut.ui.admindashboard.data.ResidentStatus

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddResidentDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddResidentDialogFragment(private val listener: AddResidentDialogFragmentListener) : DialogFragment() {
    interface AddResidentDialogFragmentListener
    {
        fun onResidentAdded(newResident: Resident)
    }

    lateinit var binding: FragmentAddResidentDialogBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_resident_dialog_title))
            .setView(getContentView())
            .setPositiveButton(getString(R.string.add_button_text)){
                    _,_ -> {}

            }.create().apply {
                setOnShowListener{
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                        if(isValid())
                        {
                            listener.onResidentAdded(getResidentItem())
                            dismiss()
                        }
                    }
                }
            }
    }

    private fun getContentView(): View {
        binding = FragmentAddResidentDialogBinding.inflate(layoutInflater, null, false)
        binding.residentStatusSpinnerSelector.adapter = ArrayAdapter<ResidentStatus>(requireContext(), R.layout.support_simple_spinner_dropdown_item, ResidentStatus.values())
        return binding.root
    }

    private fun getResidentItem(): Resident = Resident(
        id = "",
        name = binding.residentNameEditText.text.toString(),
        status = binding.residentStatusSpinnerSelector.selectedItem as ResidentStatus
    )

    private fun isValid(): Boolean
    {
        if(binding.residentNameEditText.text.isEmpty()) {
            binding.residentNameEditText.error = getString(R.string.new_resident_enter_name_error)
            return false
        }
        return true
    }

    companion object {
        const val TAG = "AddResidentDialogFragment"

    }
}