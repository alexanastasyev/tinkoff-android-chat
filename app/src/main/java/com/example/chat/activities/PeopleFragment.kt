package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.entities.Contact
import com.example.chat.R
import com.example.chat.recycler.*
import com.example.chat.recycler.converters.contactToUi
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
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.users)
        (activity as AppCompatActivity).supportActionBar?.show()
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val contactsDisposable = getContacts()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ contacts ->
                val contactUis = contactToUi(contacts)
                val holderFactory = ChatHolderFactory()
                val adapter = Adapter<ViewTyped>(holderFactory)
                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewContacts)
                recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

                recyclerView.adapter = adapter
                adapter.items = contactUis as ArrayList<ViewTyped>
            },
            {

            })
        disposeBag.add(contactsDisposable)
    }

    private fun getContacts() : Single<List<Contact>> {
        return Single.create { subscriber ->
            val contacts = listOf(
                Contact(
                    "Sherlock Holmes",
                    "https://aif-s3.aif.ru/images/020/856/92c446222800f644b2a57f05a8025a9b.jpg"
                ),
                Contact(
                    "John Watson",
                    "https://cdn.fishki.net/upload/post/2017/12/03/2447213/tn/4de61c308551534ae848c984a4d7cb74.jpg"
                )
            )
            subscriber.onSuccess(contacts)
        }
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}