package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.internet.ZulipService
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.converters.convertContactToUi
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PeopleFragment : androidx.fragment.app.Fragment() {
    private val disposeBag = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        recyclerView.visibility = View.GONE
        val shimmerPeople = view.findViewById<ShimmerFrameLayout>(R.id.shimmerPeople)
        shimmerPeople.visibility = View.VISIBLE
        shimmerPeople.startShimmer()
        val contactsDisposable = Single.fromCallable { ZulipService.getContacts() }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ contacts ->
                if (contacts != null) {
                    val contactUis = convertContactToUi(contacts)
                    val holderFactory = ChatHolderFactory()
                    val adapter = Adapter<ViewTyped>(holderFactory)
                    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

                    recyclerView.adapter = adapter
                    adapter.items = contactUis as ArrayList<ViewTyped>

                    shimmerPeople.stopShimmer()
                    shimmerPeople.visibility = View.GONE

                    recyclerView.visibility = View.VISIBLE
                }
            },
                {
                    Toast.makeText(this.context, getString(R.string.cant_get_contacts), Toast.LENGTH_SHORT).show()
                })
        disposeBag.add(contactsDisposable)
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}