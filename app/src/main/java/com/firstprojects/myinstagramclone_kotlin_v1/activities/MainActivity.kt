package com.firstprojects.myinstagramclone_kotlin_v1.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.firstprojects.myinstagramclone_kotlin_v1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    // [START declare firebase properties]
    private lateinit var auth: FirebaseAuth
    // [END declare firebase properties]

    private lateinit var editTextEmail : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var buttonUp : Button
    private lateinit var buttonIn : Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializion
        editTextEmail = findViewById(R.id.mainActivity_EditText_email)
        editTextPassword = findViewById(R.id.mainActivity_EditText_password)
        buttonUp = findViewById(R.id.button2)
        buttonIn = findViewById(R.id.button)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        buttonUp.visibility = View.VISIBLE
        val currentUser = auth.currentUser
        if(currentUser != null) {
            val intent = Intent(this@MainActivity, RepresentationScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signIn (view : View) {

        val emailString = editTextEmail.text.toString()
        val passwordString = editTextEmail.text.toString()
        signInMethod(emailString,passwordString)
    }

    fun signUp (view : View) {

        val emailString = editTextEmail.text.toString()
        val passwordString = editTextEmail.text.toString()
        signUpMethod(emailString,passwordString)
    }

    private fun signUpMethod (email : String, password : String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    println("succesfull sign Up")
                    buttonUp.visibility = View.INVISIBLE
                    editTextEmail.setText("")
                    editTextPassword.setText("")
                    Toast.makeText(this@MainActivity, "SIGN UP IS SUCCESFUL", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    println("failed + singUp + ${task.exception.toString()}")
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, task.exception.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signInMethod (email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful && task.isComplete) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                        println("succesfull + signIn")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    println("failed  + sign In + ${task.exception.toString()}")
                    Toast.makeText(baseContext, task.exception.toString(),
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this@MainActivity, RepresentationScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}