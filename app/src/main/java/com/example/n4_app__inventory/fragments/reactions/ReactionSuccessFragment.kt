package com.example.n4_app__inventory.fragments.reactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.n4_app__inventory.R

class ReactionSuccessFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reaction_success, container, false)

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
        fun newInstance(generatedId: String): ReactionSuccessFragment {
            val fragment = ReactionSuccessFragment()
            val args = Bundle()
            args.putString("generatedId", generatedId)
            fragment.arguments = args
            return fragment
        }
    }
}