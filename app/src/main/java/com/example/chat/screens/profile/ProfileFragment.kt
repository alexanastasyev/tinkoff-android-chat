package com.example.chat.screens.profile

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
import com.example.chat.R
import com.example.chat.entities.Contact
import com.example.chat.entities.Status
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso

class ProfileFragment : androidx.fragment.app.Fragment(), ProfileView {

    private lateinit var cardView: CardView
    private lateinit var imageViewProfile: ImageView
    private lateinit var textViewName: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var shimmerProfile: ShimmerFrameLayout

    private val presenter = ProfileFragmentPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()

        cardView = view.findViewById(R.id.cardViewProfilePicture)
        cardView.visibility = View.GONE

        imageViewProfile = view.findViewById(R.id.profilePicture)
        imageViewProfile.visibility = View.GONE

        textViewName = view.findViewById(R.id.profileName)
        textViewName.visibility = View.GONE

        textViewStatus = view.findViewById(R.id.status)
        textViewStatus.visibility = View.GONE

        shimmerProfile = view.findViewById(R.id.shimmerProfile)
        shimmerProfile.visibility = View.VISIBLE
        shimmerProfile.startShimmer()

        presenter.loadData()
    }

    override fun onDestroyView() {
        presenter.disposeDisposable()
        super.onDestroyView()
    }

    override fun showData(profile: Contact) {
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

    override fun showError() {
        Toast.makeText(
            this.context,
            getString(R.string.error_receive_user_info),
            Toast.LENGTH_SHORT
        ).show()
    }
}