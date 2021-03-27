package com.example.chat.recycler.converters

import com.example.chat.entities.Contact
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.ContactUi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

fun contactToUi (contacts: List<Contact>) : List<ViewTyped> {
    val resultObservable = Observable.fromIterable(contacts)
        .subscribeOn(Schedulers.newThread())
        .map { ContactUi(
            it.imageUrl,
            it.name
        ) }
    return resultObservable.toList().blockingGet()
}