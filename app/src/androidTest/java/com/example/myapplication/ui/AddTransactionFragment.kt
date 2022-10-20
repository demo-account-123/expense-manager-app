package com.anureet.expensemanager.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anureet.expensemanager.MaterialSpinnerAdapter
import com.anureet.expensemanager.R
import com.anureet.expensemanager.data.Type
import com.anureet.expensemanager.data.Transaction
import com.anureet.expensemanager.data.TransactionMode
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import java.text.SimpleDateFormat
import java.util.*


class AddTransactionFragment : Fragment() {

    private lateinit var viewModel: TransactionDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(TransactionDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.addAppBarid)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        transaction_date_layoutid.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy")
        transaction_date_layoutid.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())

        recurring_from_dateid.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy")
        recurring_from_dateid.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())

        recurring_to_dateid.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy")
        recurring_to_dateid.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())

        val type = mutableListOf<String>()
        TransactionMode.values().forEach { type.add(it.name) }
        val adapter = MaterialSpinnerAdapter(requireActivity(), R.layout.spinner_item, type)
        (transaction_type_spinner_layoutid.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        transaction_type_spinner_layoutid.editText?.setText("Cash")

        recurring_transactionid.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                recurring_from_dateid.isEnabled = true
                recurring_to_dateid.isEnabled = true
            }else{
                recurring_from_dateid.isEnabled = false
                recurring_to_dateid.isEnabled = false
                recurring_from_dateid.editText?.setText("")
                recurring_to_dateid.editText?.setText("")

            }
        }

        val id = AddTransactionFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setTaskId(id)
        if(!(id == 0L)){
            disableFields()
            addAppBarid.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editid -> {
                        // Handle edit icon press
                        enableFields()
                        true
                    }
                    R.id.deleteid -> {
                        // Handle delete icon press
                        deleteTransaction()
                        true
                    }
                    else -> false
                }
            }
        }


        viewModel.transaction.observe(viewLifecycleOwner, Observer {
            it?.let{ setData(it) }
        })
        expense_buttonid.setOnClickListener {
            val isNull = checkNullValues()
            if(isNull)
                saveTask(Type.EXPENSE)
        }
        income_buttonid.setOnClickListener {
            val isNull = checkNullValues()
            if(isNull)
                saveTask(Type.INCOME)
        }
    }
    private fun checkNullValues(): Boolean {
        val name = transaction_namei.editText?.text.toString()
        val amount = transaction_amount_addid.editText?.text.toString()
        val type = transaction_type_spinner_layoutid.editText?.text.toString()
        val date = transaction_date_layoutid.editText?.text.toString()

        if(name==""||amount==""||type==""||date==""){
            Toast.makeText(
                context,
                "Please fill all the mandatory fields",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }
    private fun enableFields() {
        transaction_namei.isEnabled = true
        transaction_amount_addid.isEnabled = true
        transaction_date_layoutid.isEnabled = true
        recurring_to_dateid.isEnabled = true
        recurring_from_dateid.isEnabled = true
        recurring_transactionid.isEnabled = true
        category_spinner_layoutid.isEnabled = true
        transaction_type_spinner_layoutid.isEnabled = true
        commentsid.isEnabled = true
        expense_buttonid.isEnabled = true
        income_buttonid.isEnabled = true
    }
    private fun disableFields() {
        transaction_namei.isEnabled = false
        transaction_amount_addid.isEnabled = false
        transaction_date_layoutid.isEnabled = false
        recurring_to_dateid.isEnabled = false
        recurring_from_dateid.isEnabled = false
        recurring_transactionid.isEnabled = false
        category_spinner_layoutid.isEnabled = false
        transaction_type_spinner_layoutid.isEnabled = false
        commentsid.isEnabled = false
        expense_buttonid.isEnabled = false
        income_buttonid.isEnabled = false

    }
    private fun setData(transaction: Transaction){
        transaction_namei.editText?.setText(transaction.name)
        transaction_amount_addid.editText?.setText((transaction.amount *(-1)).toString())

        var date = transaction.day.toString() +"/"+transaction.month+"/"+transaction.year
        if(transaction.month<10)
            date = transaction.day.toString() +"/0"+transaction.month+"/"+transaction.year

        transaction_date_layoutid.editText?.setText(date)
        transaction_type_spinner_layoutid.editText?.setText(transaction.transaction_type)
        category_spinner_layoutid.editText?.setText(transaction.category)
        commentsid.editText?.setText(transaction.comments)
    }
    private fun <E : Enum<E>> saveTask(mode: E){
        var checkType = true

        val name = transaction_namei.editText?.text.toString()
        val amount = transaction_amount_addid.editText?.text.toString()
        val category = category_spinner_layoutid.editText?.text.toString()

        var finalAmt = amount.toFloat()
        if(mode == Type.EXPENSE){
            finalAmt *= -1
        }

        var date = transaction_date_layoutid.editText?.text.toString()

        val month = Integer.parseInt(date.substring(3,5))
        val year = Integer.parseInt(date.substring(6))
        val day = Integer.parseInt(date.substring(0,2))

        if(month<10)
            date = "$year-0$month-$day"
        else
            date = "$year-$month-$day"

        val datePicker: Date = Date(year,month,day)
        Log.d("Add Transaction","date: "+datePicker)
        val monthYear = (""+month+year).toLong()

        val type = transaction_type_spinner_layoutid.editText?.text.toString()
        if(type!="Cash" && type!="Bank" && type!= "Credit"){
            Toast.makeText(
                context,
                "Invalid Type! Please select from the given types",
                Toast.LENGTH_LONG
            ).show()
            checkType=false
        }

        val comments = commentsid.editText?.text.toString()

        var recurringFrom = recurring_from_dateid.editText?.text.toString()
        var recurringTo = recurring_to_dateid.editText?.text.toString()

        val checkBalance = checkPossibility(type,finalAmt)

        if(checkBalance && checkType) {
            val transaction = Transaction(
                viewModel.transactionId.value!!,
                name,
                finalAmt,
                date,
                category,
                type,
                comments,
                month,
                year,
                day,
                datePicker,
                monthYear,
                mode.toString(),
                recurringFrom,
                recurringTo
            )
            viewModel.saveTask(transaction)
            requireActivity().onBackPressed()
        }

    }

    private fun checkPossibility(type: String, finalAmt: Float): Boolean {
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var cash = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
        var credit = sharedPreferences.getFloat(getString(R.string.CREDIT), 0f)
        var bank = sharedPreferences.getFloat(getString(R.string.BANK), 0f)
        val flag = sharedPreferences.getBoolean(getString(R.string.FLAG),false)
        val yearly = sharedPreferences.getFloat(getString(R.string.YearlyBudget),0f)

        if(flag) {
            if (type == "Cash" && cash + finalAmt < 0) {
                Toast.makeText(
                    requireContext(),
                    "Transaction not possible as Cash amount is insufficient",
                    Toast.LENGTH_LONG
                ).show()
                return false
            } else if (type == "Credit" && credit + finalAmt < 0) {
                Toast.makeText(
                    context,
                    "Transaction not possible as Credit amount is insufficient",
                    Toast.LENGTH_LONG
                ).show()
                return false
            } else if (type == "Bank" && bank + finalAmt < 0) {
                Toast.makeText(
                    context,
                    "Transaction not possible as Bank amount is insufficient",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        }else if(yearly+finalAmt<0){
            Toast.makeText(
                context,
                "Transaction not possible as Balance amount is insufficient",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true

    }

    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                show()
            }
        }
    }

    fun deleteTransaction() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Alert!")

        builder.setMessage("Do you want to delete item?")

        builder.setPositiveButton("delete") { dialogInterface, which ->
            viewModel.deleteTask()
            requireActivity().onBackPressed()
        }
        builder.setNegativeButton("cancel") { dialogInterface, which ->

        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}

