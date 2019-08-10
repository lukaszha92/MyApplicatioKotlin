package com.lukasz.myapplicatiokotlin.Messager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukasz.myapplicatiokotlin.Models.User
import com.lukasz.myapplicatiokotlin.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Secelct User"  // nadajemy tytuł wyswietlany w gornej czesci ekranu

        fetchUsers()  // pobiera urzytkownikow i po kolei dodaje ich do listy kontaktow
    }

    companion object {
        val USER_KEY = "USER_KEY"   // potrzebne do linia 51
            }

    private fun fetchUsers(){
       val ref =  FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{                   // dodajemy urzytkowników do widoku
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null){
                    adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->            // akcja po kliknieciu na urzytkownika

                    val userItem = item as UserItem // pobieramy objekt user z klasy useritem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)   // wysyłamy wartość do nowej aktywności ( potrzebny KEY )
                    startActivity(intent)

                    finish()
                }
                
                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username      // wstawiamy nazwe pod text view pobrana z obiektu user

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)  // ładujemy obrazek do odpowiedniego uzytkownika
    }   // ladowanie obrazka dzieki dodanej bibliotece

    override fun getLayout(): Int {
        return R.layout.user_row_new_message  //  ładujemy tu nowoutworzony layout
    }
}
