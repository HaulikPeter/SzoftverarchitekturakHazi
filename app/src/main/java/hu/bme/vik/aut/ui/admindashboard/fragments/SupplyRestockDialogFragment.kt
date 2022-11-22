package hu.bme.vik.aut.ui.admindashboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Supply
import hu.bme.vik.aut.databinding.FragmentRestockSupplyDialogBinding

class SupplyRestockDialogFragment(private val listener: SupplyRestockDialogFragmentListener, val supply: Supply) : DialogFragment(){

    interface SupplyRestockDialogFragmentListener
    {
        fun onSupplyRestock(supply: Supply, addedStockAmount: Long)
    }

    lateinit var binding: FragmentRestockSupplyDialogBinding
    lateinit var dialogView: View
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = getContentView()
        return AlertDialog.Builder(requireContext())
            .setTitle("Restock ${supply.name}")
            .setView(dialogView)
            .setPositiveButton(getString(R.string.restock_button_text)){
                    _,_ -> {}

            }.create().apply {
                setOnShowListener{
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                        if(isValid())
                        {
                            listener.onSupplyRestock(supply, getRestockAmount())
                            dismiss()
                        }
                    }
                }
            }
    }

    private fun getContentView(): View {
        binding = FragmentRestockSupplyDialogBinding.inflate(layoutInflater, null, false)
        binding.currentStockTextView.text = supply.stock.toString()
        binding.currentConsumptionTextView.text = supply.consumption.toString()

        binding.supplyStockPicker.minValue = 1
        binding.supplyStockPicker.maxValue = 10000
        binding.supplyStockPicker.value = 1

        return binding.root
    }

    private fun getRestockAmount(): Long = binding.supplyStockPicker.value.toLong()

    private fun isValid(): Boolean
    {
        if(binding.supplyStockPicker.value <= 0) {
            Toast.makeText(requireContext(), R.string.new_supply_enter_stock_error, Toast.LENGTH_SHORT)
            return false
        }
        return true
    }

    companion object {
        const val TAG = "RestockSupplyDialogFragment"
        fun newInstance(listener: SupplyRestockDialogFragmentListener, supply: Supply) =  SupplyRestockDialogFragment(listener, supply)

    }
}