package com.example.chat

import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.activities.ChatActivity
import com.example.chat.recycler.*
import com.example.chat.recycler.holders.ChannelUi
import com.example.chat.recycler.holders.TopicUi

class PagerAdapter(
        private val channels: List<List<Channel>>
) : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    private lateinit var adapter: Adapter<ViewTyped>
    private lateinit var recyclerView: RecyclerView

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_channels_list, parent, false)
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

        if (position == adapter.items.size - 1 || adapter.items[position + 1] is ChannelUi) {

            val layout = view.parent as ConstraintLayout

            val textView = layout.findViewById<TextView>(R.id.channelName)
            val channelName = textView.text.toString()
            val topics = getTopics(channelName)
            val topicUis = topicToUi(topics)

            adapter.addItemsAtPosition(position + 1, topicUis)
            adapter.notifyDataSetChanged()

            (adapter.items[position] as ChannelUi).isExpanded = true

           // (view as ImageView).setImageResource(R.drawable.arrow_up)

        } else if (position < adapter.items.size - 1 && adapter.items[position + 1] is TopicUi) {

            val deleteFrom = position + 1
            var deleteTo = deleteFrom
            for (i in position + 1 until adapter.items.size) {
                if (adapter.items[i] is ChannelUi) {
                    break
                }
                deleteTo = i
            }

            adapter.removeItems(deleteFrom, deleteTo)
            adapter.notifyDataSetChanged()

            (adapter.items[position] as ChannelUi).isExpanded = false

//            (view as ImageView).setImageResource(R.drawable.arrow_down)
        }

//        for (i in 0 until recyclerView.childCount - 1) {
//            if (adapter.items[i] is ChannelUi && adapter.items[i + 1] is TopicUi) {
//                recyclerView.getChildAt(i).findViewById<ImageView>(R.id.imageArrow).setImageResource(R.drawable.arrow_up)
//            } else {
//                recyclerView.getChildAt(i).findViewById<ImageView>(R.id.imageArrow).setImageResource(R.drawable.arrow_down)
//            }
//        }
    }

    private fun getTopics(channelName: String): List<Topic> {
        return listOf(
                Topic("First topic of $channelName"),
                Topic("Second topic of $channelName"),
                Topic("Third topic of $channelName")
        )
    }
}