package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.chat.entities.Contact
import com.example.chat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profile = getProfileDetails()
        val isOnline = getProfileStatus()

        val textViewName = view.findViewById<TextView>(R.id.profileName)
        textViewName.text = profile.name

        val textViewStatus = view.findViewById<TextView>(R.id.status)
        if (isOnline) {
            textViewStatus.text = getString(R.string.online)
            textViewStatus.setTextColor(ResourcesCompat.getColor(resources, R.color.status_online_color, null))
        } else {
            textViewStatus.text = getString(R.string.offline)
            textViewStatus.setTextColor(ResourcesCompat.getColor(resources, R.color.status_offline_color, null))
        }

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

    private fun getProfileStatus(): Boolean {
        return true
    }
}