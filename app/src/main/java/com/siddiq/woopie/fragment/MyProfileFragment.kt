package com.siddiq.woopie.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.siddiq.woopie.R


class MyProfileFragment : Fragment() {
    lateinit var txtNameProfile: TextView
    lateinit var txtMobileNumberProfile: TextView
    lateinit var txtEmailProfile: TextView
    lateinit var txtDeliveryProfile: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        txtNameProfile = view.findViewById(R.id.txtNameProfile)
        txtMobileNumberProfile = view.findViewById(R.id.txtMobileNumberProfile)
        txtEmailProfile = view.findViewById(R.id.txtEmailProfile)
        txtDeliveryProfile = view.findViewById(R.id.txtDeliveryProfile)
        sharedPreferences =
            requireActivity().getSharedPreferences(getString(R.string.woopie_shared_preferences),
                Context.MODE_PRIVATE)

        txtNameProfile.text = sharedPreferences.getString("user_name", "Name")
        txtEmailProfile.text = sharedPreferences.getString("user_email", "Email")
        txtMobileNumberProfile.text = sharedPreferences.getString("user_mobile_number", "Mobile Number")
        txtDeliveryProfile.text = sharedPreferences.getString("user_address", "Address")

        return view
    }


}