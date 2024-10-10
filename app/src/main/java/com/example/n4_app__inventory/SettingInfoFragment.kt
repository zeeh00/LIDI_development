package com.example.n4_app__inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageButton

class SettingInfoFragment : Fragment() {

    private lateinit var btnArrowleft: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting_info, container, false)

        // Initialize the back button
        btnArrowleft = view.findViewById(R.id.btnArrowleft)

        // Set click listener for the back button
        btnArrowleft.setOnClickListener {
            // Navigate back to the previous fragment
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}
