package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.example.chat.Database
import com.example.chat.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()

        val cardView = view.findViewById<CardView>(R.id.cardViewProfilePicture)
        cardView.visibility = View.GONE

        val imageViewProfile = view.findViewById<ImageView>(R.id.profilePicture)
        imageViewProfile.visibility = View.GONE

        val textViewName = view.findViewById<TextView>(R.id.profileName)
        textViewName.visibility = View.GONE

        val textViewStatus = view.findViewById<TextView>(R.id.status)
        textViewStatus.visibility = View.GONE

        val shimmerProfile = view.findViewById<ShimmerFrameLayout>(R.id.shimmerProfile)
        shimmerProfile.visibility = View.VISIBLE
        shimmerProfile.startShimmer()

        val profileDisposable = Database.getProfileDetails()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ profile ->
                textViewName.text = profile.name

                Picasso
                    .with(imageViewProfile.context)
                    .load(profile.imageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(imageViewProfile)

                if (profile.isOnline) {
                    textViewStatus.text = getString(R.string.online)
                    textViewStatus.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status_online_color,
                            null
                        )
                    )
                } else {
                    textViewStatus.text = getString(R.string.offline)
                    textViewStatus.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status_offline_color,
                            null
                        )
                    )
                }

                shimmerProfile.stopShimmer()
                shimmerProfile.visibility = View.GONE

                cardView.visibility = View.VISIBLE
                imageViewProfile.visibility = View.VISIBLE
                textViewName.visibility = View.VISIBLE
                textViewStatus.visibility = View.VISIBLE
            },
                {
                    Toast.makeText(
                        this.context,
                        getString(R.string.error_receive_user_info),
                        Toast.LENGTH_SHORT
                    ).show()
                })
        disposeBag.add(profileDisposable)
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}