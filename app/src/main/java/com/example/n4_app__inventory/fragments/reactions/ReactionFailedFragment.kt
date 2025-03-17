package com.example.n4_app__inventory.fragments.reactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.n4_app__inventory.R


class ReactionFailedFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reaction_failed, container, false)

        val btnArrowLeft = view.findViewById<ImageButton>(R.id.btnArrowleft)
        btnArrowLeft.setOnClickListener {
            closeCard()
        }

        return view
    }

    fun closeCard() {
        dismiss()
    }

    companion object {
        fun newInstance(): ReactionFailedFragment {
            return ReactionFailedFragment()
        }
    }
}