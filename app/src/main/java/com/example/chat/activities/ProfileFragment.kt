package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.example.chat.internet.ZulipService
import com.example.chat.R
import com.example.chat.entities.Status
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
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

        val profileDisposable = Single.fromCallable{ZulipService.getProfileDetails()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ profile ->
                if (profile != null) {
                    textViewName.text = profile.name

                    Picasso
                        .with(imageViewProfile.context)
                        .load(profile.imageUrl)
                        .placeholder(R.drawable.default_avatar)
                        .into(imageViewProfile)

                    when(profile.status) {
                        Status.ACTIVE -> {
                            textViewStatus.text = getString(R.string.online)
                            textViewStatus.setTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.status_active_color,
                                    null
                                )
                            )
                        }
                        Status.IDLE -> {
                            textViewStatus.text = getString(R.string.idle)
                            textViewStatus.setTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.status_idle_color,
                                    null
                                )
                            )
                        }
                        Status.OFFLINE -> {
                            textViewStatus.text = getString(R.string.offline)
                            textViewStatus.setTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.status_offline_color,
                                    null
                                )
                            )
                        }
                    }

                    shimmerProfile.stopShimmer()
                    shimmerProfile.visibility = View.GONE

                    cardView.visibility = View.VISIBLE
                    imageViewProfile.visibility = View.VISIBLE
                    textViewName.visibility = View.VISIBLE
                    textViewStatus.visibility = View.VISIBLE
                }
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