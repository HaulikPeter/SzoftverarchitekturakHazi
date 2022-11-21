package hu.bme.vik.aut.ui.admindashboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.FragmentAddSupplyDialogBinding
import hu.bme.vik.aut.data.Supply

class AddSupplyDialogFragment(private val listener: AddSupplyDialogFragmentListener) : DialogFragment(){

    interface AddSupplyDialogFragmentListener
    {
        fun onSupplyAdded(newSupply: Supply)
    }

    lateinit var binding: FragmentAddSupplyDialogBinding
    lateinit var dialogView: View
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = getContentView()
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_household_dialog_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.add_button_text)){
                    _,_ -> {}

            }.create().apply {
                setOnShowListener{
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                        if(isValid())
                        {
                            listener.onSupplyAdded(getSupplyItem())
                            dismiss()
                        }
                    }
                }
            }
    }

    private fun getContentView(): View {
        binding = FragmentAddSupplyDialogBinding.inflate(layoutInflater, null, false)


        binding.supplyCaloriesPicker.minValue = 1
        binding.supplyCaloriesPicker.maxValue = 10000
        binding.supplyCaloriesPicker.value = 100


        binding.supplyStockPicker.minValue = 1
        binding.supplyStockPicker.maxValue = 10000
        binding.supplyStockPicker.value = 1

        return binding.root
    }

    private fun getSupplyItem(): Supply = Supply(
        name = binding.supplyNameEditText.text.toString(),
        calorie = binding.supplyCaloriesPicker.value.toLong(),
        stock = binding.supplyStockPicker.value.toLong()
    )

    private fun isValid(): Boolean
    {
        if(binding.supplyNameEditText.text.isEmpty()) {
            binding.supplyNameEditText.error = getString(R.string.new_supply_enter_name_error)
            return false
        }

        if(binding.supplyCaloriesPicker.value <= 0 || binding.supplyCaloriesPicker.value > 10000) {
            Toast.makeText(requireContext(), R.string.new_supply_enter_calories_error, Toast.LENGTH_SHORT)
            return false
        }

        if(binding.supplyStockPicker.value <= 0) {
            Toast.makeText(requireContext(), R.string.new_supply_enter_stock_error, Toast.LENGTH_SHORT)
            return false
        }
        return true
    }

    companion object {
        const val TAG = "AddSupplyDialogFragment"

    }
}