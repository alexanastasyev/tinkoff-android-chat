package com.example.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val clickListener =
                {
                    view: View -> Toast.makeText(this, "Woohoo", Toast.LENGTH_SHORT).show()
                }
        val holderFactory = TfsHolderFactory(clickListener)
        val adapter = Adapter<ViewTyped>(holderFactory)
        recyclerView.adapter = adapter

        val messageToUi = MessageToUi()
        val messageUis = messageToUi(getMessagesList())

        adapter.items = messageUis
    }

    private fun getMessagesList(): List<Message> {
        return listOf(
                Message("Hello, world!", "John Smith", Date(10000), 2384758, 1),

                Message("Look\n" +
                        "If you had\n" +
                        "One shot\n" +
                        "Or one opportunity\n" +
                        "To seize everything you ever wanted\n" +
                        "In one moment\n" +
                        "Would you capture it\n" +
                        "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2334412, 2,
                reactions = listOf(
                        Reaction(Emoji.FACE_IN_LOVE, 100, listOf(1,2,3, THIS_USER_ID)),
                        Reaction(Emoji.FACE_WITH_SUNGLASSES, 12, listOf(1,2,3))
                )),

                Message("Nice text, bro", "Dr Dre", Date(3984), 13413, 3),

                Message("Wanna apple?", "Steve Jobs", Date(30000), 6534758, 4,
                    avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png"),

                Message("Hello, world!", "John Smith", Date(10000), 2384758, 1),

                Message("Look\n" +
                        "If you had\n" +
                        "One shot\n" +
                        "Or one opportunity\n" +
                        "To seize everything you ever wanted\n" +
                        "In one moment\n" +
                        "Would you capture it\n" +
                        "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2334412, 2),

                Message("Nice text, bro", "Dr Dre", Date(3984), 13413, 3),

                Message("Wanna apple?", "Steve Jobs", Date(30000), 6534758, 4,
                    avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png"),

                Message("Good morning", "Anna Anastasyeva", Date(1), 269, 10,
                    avatarUrl = "https://sun9-17.userapi.com/impf/c845221/v845221644/202103/rv4qM1HcbIk.jpg?size=800x600&quality=96&sign=32c7b6f5e844201615170c2677f42805&type=album"),

                Message("I love u", "Alexey Anastasyev", Date(2), THIS_USER_ID, 11)
        )
    }

    companion object {
        const val THIS_USER_ID = 100L
    }
}