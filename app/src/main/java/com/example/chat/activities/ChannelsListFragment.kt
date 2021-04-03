package com.example.chat.activities

import android.os.Bundle
import android.view.*
import com.example.chat.R

class ChannelsListFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels_list, container, false)
    }
}