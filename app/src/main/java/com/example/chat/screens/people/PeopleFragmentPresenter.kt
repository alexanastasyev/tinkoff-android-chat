package com.example.chat.screens.people

import com.example.chat.internet.ZulipService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PeopleFragmentPresenter(private val peopleView: PeopleView) {
    private val compositeDisposable = CompositeDisposable()

    fun loadData() {
        val contactsDisposable = Single.fromCallable { ZulipService.getContacts() }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ contacts ->
               if (contacts != null) {
                   peopleView.showData(contacts)
               } else {
                   peopleView.showError()
               }
            }, {
                peopleView.showError()
            })
        compositeDisposable.add(contactsDisposable)
    }

    fun disposeDisposable() {
        compositeDisposable.dispose()
    }
}