package com.lukasz.myapplicatiokotlin.LoginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.lukasz.myapplicatiokotlin.NavigationActivity
import com.lukasz.myapplicatiokotlin.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        back_registration_textview_login.setOnClickListener {
            Log.d("LoginActivity", "Try to show registration activity")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        login_button_login.setOnClickListener {
            performerLogin()
        }

    }

    private fun performerLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Pleas enter text to email/pw", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("LoginActivity", "Email is : $email")
        Log.d("LoginActivity", "Password is : $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("Main", "Login in successful")
                val intent = Intent(this, NavigationActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Main", "You were unable to login in because ${it.message}")
                Toast.makeText(this, "You were unable to login in because ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

