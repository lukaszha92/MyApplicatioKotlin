package com.lukasz.myapplicatiokotlin.Messager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.lukasz.myapplicatiokotlin.LoginRegister.User
import com.lukasz.myapplicatiokotlin.Models.ChatMessage
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
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)  // pobieramy wartosc z poprzedniej aktywnosci

        supportActionBar?.title = user.username

       //setupDummyData()
        listenFromMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message ...")
            performSendMessage()
        }
    }
    // tworzymy obiekt wiadomosci wraz z parametrami ktore beda go opisywaly "ChatMessage" przenieslismy do Models


   private fun listenFromMessages(){
       val ref = FirebaseDatabase.getInstance().getReference("/messages") // pobieramy istniejace wiadomosci

       ref.addChildEventListener(object : ChildEventListener{
           override fun onCancelled(p0: DatabaseError) {

           }

           override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) //pobieramy zmienne od kazdej wiadomosci

               if (chatMessage != null) {  // na podstawie uid stwierdzamy czy to wiadomosc do nas czy od nas
                   Log.d(TAG, chatMessage?.text)

                   if (chatMessage.fromId == FirebaseAuth.getInstance().uid){  // i dodajemy ja po odpowiedniej stronie
                       adapter.add(ChatFromItem(chatMessage.text))
                   }
                   else{
                       adapter.add(ChatToItem(chatMessage.text))
                   }
               } // to czy wiadomosc jest po prawej czy po lewej mozliwe jest dzieki funkcja ktore zrobilismy

           }

           override fun onChildChanged(p0: DataSnapshot, p1: String?) {

           }

           override fun onChildMoved(p0: DataSnapshot, p1: String?) {

           }

           override fun onChildRemoved(p0: DataSnapshot) {

           }
       })
   }

    // funkcja wysylania wiadomosci
    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString() // pobieramy tekst ktory bedzie wpisany w oknie

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)  // korzystamy z triku parcelable
        val toId = user.uid

        // wskazujemy miejsce gdzie maja byc one zapisywane
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        if (fromId == null) return

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message ${reference.key}")
            }
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