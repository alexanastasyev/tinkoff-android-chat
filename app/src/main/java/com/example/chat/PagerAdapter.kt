package com.example.chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.activities.ChatActivity
import com.example.chat.recycler.*
import com.example.chat.recycler.holders.ChannelUi

class PagerAdapter(
        private val channels: List<List<Channel>>
) : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    private lateinit var adapter: Adapter<ViewTyped>
    private lateinit var recyclerView: RecyclerView

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_channels_list, parent, false)
        view.isSelected = false
        return PagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val channelUis = channelToUi(channels[position])
        val holderFactory = ChatHolderFactory(
                click = { view ->
                    if ((view.parent as ConstraintLayout).tag == "layoutChannelItem") {
                        showTopics(view)
                    } else {
                        val intent = Intent(view.context, ChatActivity::class.java)
                        view.context.startActivity(intent)
                    }
                }
        )
        adapter = Adapter(holderFactory)
        recyclerView = holder.itemView.findViewById(R.id.recyclerViewChannels)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

        recyclerView.adapter = adapter
        adapter.items = channelUis as ArrayList<ViewTyped>
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    private fun showTopics(view: View) {
        val position = recyclerView.getChildLayoutPosition(view.parent as ConstraintLayout)

        if (position == adapter.itemCount - 1 || adapter.items[position + 1] is ChannelUi) {

            val layout = recyclerView.getChildAt(position)
            val textView = layout.findViewById<TextView>(R.id.channelName)
            val channelName = textView.text.toString()
            val topics = getTopics(channelName)
            val topicUis = topicToUi(topics)

            adapter.addItemsAtPosition(position + 1, topicUis)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getTopics(channelName: String): List<Topic> {
        return listOf(
                Topic("First topic"),
                Topic("Second topic"),
                Topic("Third topic")
        )
    }
}