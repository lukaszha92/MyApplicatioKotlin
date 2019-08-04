package com.lukasz.myapplicatiokotlin.Messager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lukasz.myapplicatiokotlin.LoginRegister.User
import com.lukasz.myapplicatiokotlin.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "Chatlog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)  // pobieramy wartosc z poprzedniej aktywnosci

        supportActionBar?.title = user.username

       setupDummyData()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message ...")
            performSendMessage()
        }
    }
    private fun performSendMessage() {

    }

    private fun setupDummyData(){
        val adapter = GroupAdapter<ViewHolder>()  // dodawanie adapeterow dzieki bibliotece com.xwray:groupie:2.3.0

        adapter.add(ChatFromItem("to jest tekst ktory wysylamuy"))
        adapter.add(ChatToItem("a to tekst ktory odbieramy"))
        adapter.add(ChatFromItem("to jest tekst ktory wysylamuy"))
        adapter.add(ChatToItem("a to tekst ktory odbieramy"))

        recyclerview_chat_log.adapter = adapter
    }
}

class ChatFromItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ChatToItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}