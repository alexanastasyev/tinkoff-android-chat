package com.example.chat.screens.profile

import com.example.chat.internet.ZulipService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileFragmentPresenter(private val profileView: ProfileView) {
    private val compositeDisposable = CompositeDisposable()

    fun loadData() {
        val profileDisposable = Single.fromCallable{ ZulipService.getProfileDetails()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ profile ->
                if (profile != null) {
                    profileView.showData(profile)
                } else {
                    profileView.showError()
                }
            }, {
                profileView.showError()
            })
        compositeDisposable.add(profileDisposable)
    }

    fun disposeDisposable() {
        compositeDisposable.dispose()
    }
}