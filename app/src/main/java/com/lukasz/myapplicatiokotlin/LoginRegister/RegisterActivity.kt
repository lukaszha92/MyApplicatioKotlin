package com.lukasz.myapplicatiokotlin.LoginRegister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lukasz.myapplicatiokotlin.Models.User
import com.lukasz.myapplicatiokotlin.NavigationActivity
import com.lukasz.myapplicatiokotlin.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }
        alredy_have_account_text_view.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login activity")  // loujemy klikniecie w napis
            val intent = Intent(this, LoginActivity::class.java) // przenosimy do login activity
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null // wartosc uri zostala wyciagnieta i zamieniona na selectorphotouri poniewaz uzyjemy jej podczas tworzenia urzytkownika

    //sprawdzamy czy został wybrany obrazek i podstawiamy go w pole przycisku
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            Log.d("Register ", "$selectedPhotoUri")

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected image was ...
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotoUri = data.data


            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }


    private fun performRegister() {
        val email = email_edittext_registration.text.toString()     // przypisujemy wartosci do zmiennych pobierane z aplikacji na podstawie id pola
        val password = password_edittext_registration.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Pleas enter text to email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is : $email")     // logujemy wpisywane wartosci
        Log.d("RegisterActivity", "Password :  $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("Register", "Successfully created user with uid : ${it.result!!.user.uid}")
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Register", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()   // tworzy nam unikalny ciag z wielu randomowych znakow
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")  // w referencji podalismy miejsce zapisania pliku

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}") // nazwa pliku

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File location: $it") // tu pobieramy sciezke do pliku

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageurl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""  // sprawdzamy czy uid nie jest puste ?? ( nie jestem pewny )
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid") // podajemy sciezke do zapisu w bazie danych

        val user = User(
            uid,
            username_edittext_registration.text.toString(),
            profileImageurl
        ) // tworzymy obiekt z klasy ponizej i podajemy mu wartosci ktore juz mamy

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")
                val intent = Intent(this, NavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)   // jezeli wyjdziemy z app to po wlaczeniu wlacza ja od nowa
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to set value to database ${it.message}")
            }
    }
}





