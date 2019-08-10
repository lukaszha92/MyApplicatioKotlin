package com.lukasz.myapplicatiokotlin.Messager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.lukasz.myapplicatiokotlin.Models.ChatMessage
import com.lukasz.myapplicatiokotlin.Models.User
import com.lukasz.myapplicatiokotlin.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.imageView as imageView1

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "Chatlog"
    }
    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)  // pobieramy wartosc z poprzedniej aktywnosci

        supportActionBar?.title = toUser?.username

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

               if (chatMessage != null) {  // na podstawie uid stwierdzamy czy to wiadomosc do nas czy od nas, sprawdzamy tez czy wiadomosc nie jest pusta
                   Log.d(TAG, chatMessage?.text)

                   if (chatMessage.fromId == FirebaseAuth.getInstance().uid){  // i dodajemy ja po odpowiedniej stronie
                       val currentUser = MessagerActivity.currentUser
                       adapter.add(ChatFromItem(chatMessage.text, currentUser!!)) // w zaleznosci czy jestesmy adresatami czy nadawcami
                   }
                   else{
                       adapter.add(ChatToItem(chatMessage.text, toUser!!))
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
}

class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ChatToItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        // ładowanie zdjecia obok wiadomosci w czacie
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}