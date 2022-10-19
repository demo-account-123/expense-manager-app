package com.anureet.expensemanager.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.anureet.expensemanager.R
import kotlinx.android.synthetic.main.fragment_onboarding.*


class OnboardingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        var openedFirstTime: String? = sharedPreferences.getString("FirstTimeInstall","")
        if(openedFirstTime.equals("Yes")){
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToHomeFragment())
        }else{

            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("FirstTimeInstall","Yes")
            editor.apply()

        }
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profile_namei.editText?.addTextChangedListener(boardingTextWatcher)
        monthly_budgeti.editText?.addTextChangedListener(boardingTextWatcher)
        monthly_incomei.editText?.addTextChangedListener(boardingTextWatcher)

        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference",Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        val income = monthly_incomei.editText?.text.toString()
        if(income!=""){
            editor.putFloat(getString(R.string.Income),income.toFloat())
        }

        continueButtonid.setOnClickListener {
            val name = profile_namei.editText?.text.toString()
            val monthlyBudget = monthly_budgeti.editText?.text.toString()

            editor.putString(getString(R.string.Name),name)
            editor.putFloat(getString(R.string.netBalance),monthlyBudget.toFloat())
            editor.putFloat(getString(R.string.YearlyBudget),monthlyBudget.toFloat()*12)
            editor.putFloat(getString(R.string.FinalMonthBudget),monthlyBudget.toFloat())
            editor.apply()
            findNavController().navigate(
                OnboardingFragmentDirections.actionOnboardingFragmentToHomeFragment(name,monthlyBudget.toFloat())
            )
        }

    }

    private val boardingTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            val name = profile_namei.editText?.text.toString()
            val monthlyBudget = monthly_budgeti.editText?.text.toString()

            if(name.isEmpty()){
                continueButtonid.isEnabled = false
                profile_namei.error = "This field cannot be empty"
                profile_namei.isEndIconVisible = true

            }
            if(monthlyBudget.isEmpty()){
                continueButtonid.isEnabled = false
                monthly_budgeti.error = "This field cannot be empty"
                monthly_budgeti.isEndIconVisible = true
            }
            else{
                continueButtonid.isEnabled = true
                monthly_budgeti.isEndIconVisible = false
                profile_namei.isEndIconVisible = false
                monthly_budgeti.error = null
                profile_namei.error = null
            }
        }
    }
}