package com.example.chat.recycler

class Adapter<T: ViewTyped>(holderFactory: HolderFactory) : BaseAdapter<T>(holderFactory) {
    private val localItems: MutableList<T> = mutableListOf()

    override var items: ArrayList<T>
        get() = localItems as ArrayList<T>
        set(newItems) {
            localItems.clear()
            localItems.addAll(newItems)
            notifyDataSetChanged()
        }
}