package com.example.n4_app__inventory.fragments.reactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.n4_app__inventory.R

class ReactionSuccessUpdateFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reaction_success_update, container, false)

        val btnArrowLeft = view.findViewById<ImageButton>(R.id.btnArrowleft)
        btnArrowLeft.setOnClickListener {
            closeCard()
        }

        val generatedId = arguments?.getString("generatedId")
        val txtIDN4DOMBAT = view.findViewById<TextView>(R.id.txtIDN4)
        txtIDN4DOMBAT?.text = "ID: $generatedId"

        return view
    }

    fun closeCard() {
        dismiss()
    }

    companion object {
        fun newInstance(generatedId: String): ReactionSuccessUpdateFragment {
            val fragment = ReactionSuccessUpdateFragment()
            val args = Bundle()
            args.putString("generatedId", generatedId)
            fragment.arguments = args
            return fragment
        }
    }
}