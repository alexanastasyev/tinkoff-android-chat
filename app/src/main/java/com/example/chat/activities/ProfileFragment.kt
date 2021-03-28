package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.chat.Database
import com.example.chat.entities.Contact
import com.example.chat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileFragment : androidx.fragment.app.Fragment() {
    private val disposeBag = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.profile)
        (activity as AppCompatActivity).supportActionBar?.show()
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profileDisposable = Database.getProfileDetails()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ profile ->
                val textViewName = view.findViewById<TextView>(R.id.profileName)
                textViewName.text = profile.name

                val imageView = view.findViewById<CircleImageView>(R.id.profilePicture)
                Picasso
                    .with(imageView.context)
                    .load(profile.imageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(imageView)
            },
            {
                Toast.makeText(this.context, getString(R.string.error_receive_user_info), Toast.LENGTH_SHORT).show()
            })
        disposeBag.add(profileDisposable)

        val isOnlineDisposable = Database.getProfileStatus()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isOnline ->
                val textViewStatus = view.findViewById<TextView>(R.id.status)
                if (isOnline) {
                    textViewStatus.text = getString(R.string.online)
                    textViewStatus.setTextColor(ResourcesCompat.getColor(resources, R.color.status_online_color, null))
                } else {
                    textViewStatus.text = getString(R.string.offline)
                    textViewStatus.setTextColor(ResourcesCompat.getColor(resources, R.color.status_offline_color, null))
                }
            },
            {
                Toast.makeText(this.context, getString(R.string.error_receive_user_status), Toast.LENGTH_SHORT).show()
            })
        disposeBag.add(isOnlineDisposable)
    }



    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}