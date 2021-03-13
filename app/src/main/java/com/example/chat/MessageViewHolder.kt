package com.example.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.view.View
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toDrawable
import com.squareup.picasso.Picasso
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ExecutionException

class MessageUi(
        var authorId: Long,
        var text: String,
        var author: String,
        var avatarUrl: String?,
        var reactions: List<Pair<Emoji, Int>>,
        var messageId: Long,
        override val viewType: Int = R.layout.item_message
) : ViewTyped

class MessageViewHolder(view: View) : BaseViewHolder<MessageUi>(view) {
    val messageHolder = view.findViewById<MessageViewGroup>(R.id.message_from_other)

    override fun bind(item: MessageUi) {
        val context = messageHolder.context
        if (item.authorId == MainActivity.THIS_USER_ID) {
            messageHolder.align = 1
            messageHolder.messageText = item.text
            messageHolder.userName = item.author
            messageHolder.reactions = item.reactions

//            if (!item.avatarUrl.isNullOrEmpty()) {
//                val imageDownloadTask = ImageDownloadTask()
//
//                try {
//                    val receivedImage = imageDownloadTask.execute(item.avatarUrl).get()
//                    if (receivedImage != null) {
//                        messageHolder.avatar = BitmapDrawable(receivedImage)
//                    }
//                } catch (e: ExecutionException) {
//                    e.printStackTrace()
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }

        } else {
            messageHolder.align = 0
            messageHolder.messageText = item.text
            messageHolder.userName = item.author
            messageHolder.reactions = item.reactions

            Picasso
                    .with(context)
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(messageHolder.avatarImageView)

//            if (!item.avatarUrl.isNullOrEmpty()) {
//                val imageDownloadTask = ImageDownloadTask()
//
//                try {
//                    val receivedImage = imageDownloadTask.execute(item.avatarUrl).get()
//                    if (receivedImage != null) {
//                        messageHolder.avatar = BitmapDrawable(receivedImage)
//                    }
//                } catch (e: ExecutionException) {
//                    e.printStackTrace()
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
        }
    }
}

private class ImageDownloadTask : AsyncTask<String?, Void?, Bitmap?>() {
    override fun doInBackground(vararg params: String?): Bitmap? {
        var url: URL? = null
        var urlConnection: HttpURLConnection? = null
        try {
            url = URL(params[0])
            urlConnection = url.openConnection() as HttpURLConnection
            val inputStream = urlConnection.content as InputStream
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }
        return null
    }
}