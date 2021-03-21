package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.Contact
import com.example.chat.R
import com.example.chat.recycler.*

class PeopleFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val contacts = getContacts()
        val contactUis = contactToUi(contacts)
        val holderFactory = ChatHolderFactory()
        val adapter = Adapter<ViewTyped>(holderFactory)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView?.context)

        recyclerView.adapter = adapter
        adapter.items = contactUis as ArrayList<ViewTyped>
    }

    private fun getContacts() : List<Contact> {
        return listOf(
                Contact("Sherlock Holmes", "https://aif-s3.aif.ru/images/020/856/92c446222800f644b2a57f05a8025a9b.jpg"),
                Contact("John Watson", "https://cdn.fishki.net/upload/post/2017/12/03/2447213/tn/4de61c308551534ae848c984a4d7cb74.jpg")
        )
    }
}