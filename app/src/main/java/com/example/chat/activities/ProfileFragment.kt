package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.chat.Contact
import com.example.chat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profile = getProfileDetails()

        val textView = view.findViewById<TextView>(R.id.profileName)
        textView.text = profile.name

        val imageView = view.findViewById<CircleImageView>(R.id.profilePicture)
        Picasso
                .with(imageView.context)
                .load(profile.imageUrl)
                .placeholder(R.drawable.default_avatar)
                .into(imageView)
    }

    private fun getProfileDetails(): Contact {
        return Contact(
                "Alexey Anastasyev",
                "https://assets.gitlab-static.net/uploads/-/system/user/avatar/8174750/avatar.png"
        )
    }
}