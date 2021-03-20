package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.chat.R

class ProfileFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.fragment_profile, container, true)

        val text = "PROFILE"
        val textView = activity?.findViewById<TextView>(R.id.profileTextView)
        textView?.text = text

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}