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
import com.example.chat.entities.Channel
import com.example.chat.entities.Topic
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.converters.channelToUi
import com.example.chat.recycler.converters.topicToUi
import com.example.chat.recycler.holders.ChannelUi
import com.example.chat.recycler.holders.TopicUi

class PagerAdapter(
        private val channels: List<List<Channel>>
) : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    companion object {
        private const val LAYOUT_CHANNEL_ITEM_TAG = "layoutChannelItem"

        private const val TYPE_MY_CHANNELS = 0
        private const val TYPE_ALL_CHANNELS = 1

        private lateinit var adapterMyChannels: Adapter<ViewTyped>
        private lateinit var adapterAllChannels: Adapter<ViewTyped>
    }

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
                click = getChannelOrTopicClickListener(getItemViewType(position))
        )
        adapter = Adapter(holderFactory)

        when (getItemViewType(position)) {
            TYPE_MY_CHANNELS -> adapterMyChannels = adapter
            TYPE_ALL_CHANNELS -> adapterAllChannels = adapter
        }

        recyclerView = holder.itemView.findViewById(R.id.recyclerViewChannels)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

        recyclerView.adapter = adapter
        adapter.items = channelUis as ArrayList<ViewTyped>
    }

    private fun getChannelOrTopicClickListener(channelsType: Int): (View) -> Unit {
        return { view ->
            if (isChannel(view)) {
                showOrHideTopics(view, channelsType)
            } else {
                startChatActivity(view)
            }
        }
    }

    private fun isChannel(view: View): Boolean {
        return (view.parent as ConstraintLayout).tag == LAYOUT_CHANNEL_ITEM_TAG
    }

    private fun showOrHideTopics(view: View, channelsType: Int) {
        if (isCollapsed(view, channelsType)) {
            expandTopics(view, channelsType)
        } else if (isExpanded(view, channelsType)) {
            collapseTopics(view, channelsType)
        }
    }

    private fun isCollapsed(view: View, channelsType: Int): Boolean {
        val position = getChildPosition(view)
        val currentAdapter = getCurrentAdapter(channelsType)
        return  position == currentAdapter.items.size - 1 || currentAdapter.items[position + 1] is ChannelUi
    }

    private fun getChildPosition(view: View): Int {
        return recyclerView.getChildLayoutPosition(view.parent as ConstraintLayout)
    }

    private fun expandTopics(view: View, channelsType: Int) {
        val position = getChildPosition(view)
        val layout = view.parent as ConstraintLayout
        val textView = layout.findViewById<TextView>(R.id.channelName)
        val channelName = textView.text.toString()
        val topics = getTopics(channelName)
        val topicUis = topicToUi(topics)

        val currentAdapter = getCurrentAdapter(channelsType)

        currentAdapter.addItemsAtPosition(position + 1, topicUis)
        currentAdapter.notifyDataSetChanged()

        (currentAdapter.items[position] as ChannelUi).isExpanded = true
    }

    private fun getCurrentAdapter(channelsType: Int): Adapter<ViewTyped> {
        return when (channelsType) {
            TYPE_MY_CHANNELS -> adapterMyChannels
            TYPE_ALL_CHANNELS -> adapterAllChannels
            else -> adapterAllChannels
        }
    }

    private fun isExpanded(view: View, channelsType: Int): Boolean {
        val position = getChildPosition(view)
        val currentAdapter = getCurrentAdapter(channelsType)
        return position < currentAdapter.items.size - 1 && currentAdapter.items[position + 1] is TopicUi
    }

    private fun getTopics(channelName: String): List<Topic> {
        return listOf(
                Topic("First topic of $channelName"),
                Topic("Second topic of $channelName"),
                Topic("Third topic of $channelName")
        )
    }

    private fun collapseTopics(view: View, channelsType: Int) {
        val position = getChildPosition(view)
        val deleteFrom = position + 1
        var deleteTo = deleteFrom

        val currentAdapter = getCurrentAdapter(channelsType)

        for (i in position + 1 until currentAdapter.items.size) {
            if (currentAdapter.items[i] is ChannelUi) {
                break
            }
            deleteTo = i
        }

        currentAdapter.removeItems(deleteFrom, deleteTo)
        currentAdapter.notifyDataSetChanged()

        (currentAdapter.items[position] as ChannelUi).isExpanded = false

    }

    private fun startChatActivity(view: View) {
        val intent = Intent(view.context, ChatActivity::class.java)
        val topicName = (view as TextView).text
        intent.putExtra("topic", topicName)
        view.context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return channels.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_MY_CHANNELS
        } else {
            TYPE_ALL_CHANNELS
        }
    }
}