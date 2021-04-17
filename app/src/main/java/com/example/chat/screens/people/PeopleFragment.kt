package com.example.chat.screens.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.entities.Contact
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.converters.convertContactToUi
import com.facebook.shimmer.ShimmerFrameLayout

class PeopleFragment : androidx.fragment.app.Fragment(), PeopleView {
    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmerPeople: ShimmerFrameLayout

    private val presenter = PeopleFragmentPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()

        recyclerView = view.findViewById(R.id.recyclerViewContacts)
        recyclerView.visibility = View.GONE

        shimmerPeople = view.findViewById(R.id.shimmerPeople)
        shimmerPeople.visibility = View.VISIBLE
        shimmerPeople.startShimmer()

        presenter.loadData()
    }

    override fun onDestroyView() {
        presenter.disposeDisposable()
        super.onDestroyView()
    }

    override fun showData(contacts: List<Contact>) {
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

    override fun showError() {
        Toast.makeText(this.context, getString(R.string.cant_get_contacts), Toast.LENGTH_SHORT).show()
    }
}