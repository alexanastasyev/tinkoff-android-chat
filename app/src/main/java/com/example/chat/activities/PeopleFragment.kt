package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.Database
import com.example.chat.R
import com.example.chat.recycler.*
import com.example.chat.recycler.converters.contactToUi
import com.facebook.shimmer.ShimmerFrameLayout
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
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.users)
        (activity as AppCompatActivity).supportActionBar?.show()
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        recyclerView.visibility = View.GONE
        val shimmerPeople = view.findViewById<ShimmerFrameLayout>(R.id.shimmerPeople)
        shimmerPeople.visibility = View.VISIBLE
        shimmerPeople.startShimmer()
        val contactsDisposable = Database.getContacts()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ contacts ->
                val contactUis = contactToUi(contacts)
                val holderFactory = ChatHolderFactory()
                val adapter = Adapter<ViewTyped>(holderFactory)
                recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

                recyclerView.adapter = adapter
                adapter.items = contactUis as ArrayList<ViewTyped>

                shimmerPeople.stopShimmer()
                shimmerPeople.visibility = View.GONE

                recyclerView.visibility = View.VISIBLE
            },
            {

            })
        disposeBag.add(contactsDisposable)
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}