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

    fun addItemsAtPosition(position: Int, newItems: List<T>) {
        val newItemsList: MutableList<T> = ArrayList()
        for (i in 0 until position) {
            newItemsList.add(items[i])
        }
        for (i in newItems) {
            newItemsList.add(i)
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