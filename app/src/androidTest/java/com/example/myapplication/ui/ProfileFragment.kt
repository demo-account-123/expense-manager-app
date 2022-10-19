package com.anureet.expensemanager.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anureet.expensemanager.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.profileTopAppBarid)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var monthlyBudget = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)
        var name = sharedPreferences.getString(getString(R.string.Name),null)
        var income = sharedPreferences.getFloat(getString(R.string.Income),0f)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()

        disableFields()
        if(name!=null){
            setValues(name,monthlyBudget,income)
        }
        appBar()

        saveButtonid.setOnClickListener {
            var newName = profile_namei.editText?.text.toString()
            var newMonthlyBudget = monthly_budgeti.editText?.text.toString()
            var newIncome = monthly_incomei.editText?.text.toString()
            if(!newMonthlyBudget.equals(monthlyBudget)) {
                if (newName != "" && newMonthlyBudget != "")
                    updateDetails(editor, newName, newMonthlyBudget, newIncome)
            }
            requireActivity().onBackPressed()

        }

    }

    private fun setValues(name: String, monthlyBudget: Float, income: Float) {
        profile_namei.editText?.setText(name)
        monthly_budgeti.editText?.setText(monthlyBudget.toString())
        if(income!=0f)
            monthly_incomei.editText?.setText(income.toString())
    }

    private fun appBar() {
        profileTopAppBarid.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.editProfileid -> {
                    enableFields()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateDetails(editor: SharedPreferences.Editor, newName: String, newMonthlyBudget: String, newIncome: String) {
        editor.putString(getString(R.string.Name),newName)
        editor.putFloat(getString(R.string.FinalMonthBudget),newMonthlyBudget.toFloat())
        editor.putFloat(getString(R.string.netBalance),newMonthlyBudget.toFloat())
        editor.putFloat(getString(R.string.YearlyBudget),newMonthlyBudget.toFloat()*12)
        if(newIncome!="")
            editor.putFloat(getString(R.string.Income),newIncome.toFloat())

        editor.putFloat(getString(R.string.CASH),0f)
        editor.putFloat(getString(R.string.CREDIT),0f)
        editor.putFloat(getString(R.string.BANK),0f)
        editor.putBoolean(getString(R.string.FLAG),false)

        editor.commit()
    }

    private fun enableFields() {
        profile_namei.isEnabled = true
        monthly_budgeti.isEnabled = true
        monthly_incomei.isEnabled = true
        saveButtonid.isEnabled = true
    }

    private fun disableFields() {
        profile_namei.isEnabled = false
        monthly_budgeti.isEnabled = false
        monthly_incomei.isEnabled = false
        saveButtonid.isEnabled = false
    }


}