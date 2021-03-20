package com.example.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PagerAdapter(
    private val fragments: List<String>
) : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {
    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_channels_list, parent, false)
        return PagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val currentText = fragments[position]
        holder.itemView.findViewById<TextView>(R.id.textView).text = currentText
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}