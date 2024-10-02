package com.example.n4_app__inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SettingFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}
