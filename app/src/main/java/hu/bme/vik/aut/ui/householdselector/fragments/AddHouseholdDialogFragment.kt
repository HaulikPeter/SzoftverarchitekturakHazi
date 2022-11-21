package hu.bme.vik.aut.ui.householdselector.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Household
import hu.bme.vik.aut.databinding.FragmentAddHouseholdDialogBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddHouseholdDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddHouseholdDialogFragment(private val listener: AddHouseholdDialogFragmentListener) : DialogFragment() {
    interface AddHouseholdDialogFragmentListener
    {
        fun onHouseholdAdded(newHousehold: Household)
    }

    lateinit var binding: FragmentAddHouseholdDialogBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_household_dialog_title))
            .setView(getContentView())
            .setPositiveButton(getString(R.string.add_button_text)){
                    _,_ -> {}

            }.create().apply {
                setOnShowListener{
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                        if(isValid())
                        {
                            listener.onHouseholdAdded(getHouseholdItem())
                            dismiss()
                        }
                    }
                }
            }
    }

    private fun getContentView(): View {
        binding = FragmentAddHouseholdDialogBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    private fun getHouseholdItem(): Household = Household(
        name = binding.householdNameEditText.text.toString()
    )

    private fun isValid(): Boolean
    {
        if(binding.householdNameEditText.text.isEmpty()) {
            binding.householdNameEditText.error = getString(R.string.new_household_enter_name_error)
            return false
        }
        return true
    }

    companion object {
        const val TAG = "AddHouseholdDialogFragment"

    }
}