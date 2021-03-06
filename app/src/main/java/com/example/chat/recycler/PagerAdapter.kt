package com.example.chat.recycler

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.Database
import com.example.chat.R
import com.example.chat.activities.ChatActivity
import com.example.chat.entities.Channel
import com.example.chat.recycler.converters.channelToUi
import com.example.chat.recycler.converters.topicToUi
import com.example.chat.recycler.uis.ChannelUi
import com.example.chat.recycler.uis.TopicUi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class PagerAdapter(
        private val channels: List<List<Channel>>
) : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    companion object {
        private const val LAYOUT_CHANNEL_ITEM_TAG = "layoutChannelItem"
        const val TOPIC_KEY = "topic"

        private const val TYPE_MY_CHANNELS = 0
        private const val TYPE_ALL_CHANNELS = 1

        private lateinit var adapterMyChannels: Adapter<ViewTyped>
        private lateinit var adapterAllChannels: Adapter<ViewTyped>

        private lateinit var itemsMyChannels: List<ViewTyped>
        private lateinit var itemsAllChannels: List<ViewTyped>
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

        recyclerView = holder.itemView.findViewById(R.id.recyclerViewChannels)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

        recyclerView.adapter = adapter
        adapter.items = channelUis as ArrayList<ViewTyped>
        when (getItemViewType(position)) {
            TYPE_MY_CHANNELS -> {
                adapterMyChannels = adapter
                itemsMyChannels = channelUis
            }
            TYPE_ALL_CHANNELS -> {
                adapterAllChannels = adapter
                itemsAllChannels = channelUis
            }
        }
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
        return position == currentAdapter.items.size - 1 || currentAdapter.items[position + 1] is ChannelUi
    }

    private fun getChildPosition(view: View): Int {
        return recyclerView.getChildLayoutPosition(view.parent as ConstraintLayout)
    }

    private fun expandTopics(view: View, channelsType: Int) {
        val position = getChildPosition(view)
        val currentAdapter = getCurrentAdapter(channelsType)
        (currentAdapter.items[position] as ChannelUi).isExpanded = true
        val layout = view.parent as ConstraintLayout
        val textView = layout.findViewById<TextView>(R.id.channelName)
        val channelName = textView.text.toString()
        val topicsDisposable = Database.getTopics(channelName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
            {topics ->
                val topicUis = topicToUi(topics)
                currentAdapter.addItemsAtPosition(position + 1, topicUis)
                currentAdapter.notifyDataSetChanged()
            },
            {
                Toast.makeText(view.context, view.context.getString(R.string.error_receive_topics), Toast.LENGTH_SHORT).show()
            })
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

    private fun collapseTopics(view: View, channelsType: Int) {
        val position = getChildPosition(view)
        val currentAdapter = getCurrentAdapter(channelsType)
        (currentAdapter.items[position] as ChannelUi).isExpanded = false

        val deleteFrom = position + 1
        var deleteTo = deleteFrom

        for (i in position + 1 until currentAdapter.items.size) {
            if (currentAdapter.items[i] is ChannelUi) {
                break
            }
            deleteTo = i
        }

        currentAdapter.removeItems(deleteFrom, deleteTo)
        currentAdapter.notifyDataSetChanged()

    }

    private fun startChatActivity(view: View) {
        val intent = Intent(view.context, ChatActivity::class.java)
        val topicName = (view as TextView).text
        intent.putExtra(TOPIC_KEY, topicName)
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

    fun filterChannels(key: String, channelsType: Int) {
        val currentAdapter = getCurrentAdapter(channelsType)
        val currentItems = when (channelsType) {
            TYPE_MY_CHANNELS -> itemsMyChannels
            TYPE_ALL_CHANNELS -> itemsAllChannels
            else -> itemsAllChannels
        }
        currentAdapter.items.forEach {
            if (it is ChannelUi && it.isExpanded) {
                it.isExpanded = false
            }
        }
        currentAdapter.items = currentItems.filter {
                it is ChannelUi && it.name.contains(key)
        }
    }
}