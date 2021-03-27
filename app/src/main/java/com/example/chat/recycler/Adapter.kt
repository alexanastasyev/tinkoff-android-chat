package com.example.chat.recycler

import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import com.example.chat.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Adapter<T: ViewTyped>(holderFactory: HolderFactory) : BaseAdapter<T>(holderFactory) {
    private val differ = AsyncListDiffer(this, DiffCallback())

    override var items: List<T> = emptyList()
        // Elvis operator is needed to make property have a backing field
        get() = ((differ.currentList ?: field) as List<T>)
        set(newItems) {
            val dispose = Single.just(Unit)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    differ.submitList(newItems)
                    notifyDataSetChanged()
                }, {})
        }

    fun addItemsAtPosition(position: Int, newItems: List<T>) {
        val newItemsList: MutableList<T> = ArrayList()
        for (i in 0 until position) {
            newItemsList.add(items[i])
        }
        for (item in newItems) {
            newItemsList.add(item)
        }
        for (i in position until items.size) {
            newItemsList.add(items[i])
        }
        items = newItemsList as ArrayList<T>
    }

    fun removeItems(from: Int, to: Int) {
        val newItemsList: MutableList<T> = ArrayList()
        for (i in 0 until from) {
            newItemsList.add(items[i])
        }
        for (i in to + 1 until items.size) {
            newItemsList.add(items[i])
        }
        items = newItemsList as ArrayList<T>
    }
}