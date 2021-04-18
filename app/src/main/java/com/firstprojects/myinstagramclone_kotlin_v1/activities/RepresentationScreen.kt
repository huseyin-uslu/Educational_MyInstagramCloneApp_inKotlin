package com.firstprojects.myinstagramclone_kotlin_v1.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.constraintlayout.solver.ArrayLinkedVariables
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firstprojects.myinstagramclone_kotlin_v1.R
import com.firstprojects.myinstagramclone_kotlin_v1.UploadScreen
import com.firstprojects.myinstagramclone_kotlin_v1.adapters.RecyclerViewFeedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RepresentationScreen : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.menulayout,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.menulayout_shareItem) {
            val intent = Intent(this, UploadScreen::class.java)
            startActivity(intent)
       }
        else if(item.itemId == R.id.menulayout_signOutItem) {
            auth.signOut()

            if (auth.currentUser == null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
       }
        return super.onOptionsItemSelected(item)
    }

    //Declaration
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    //recyclerView
    private lateinit var recyclerView : RecyclerView
    private lateinit var layoutManager : RecyclerView.LayoutManager
    private lateinit var recyclerViewFeedAdapter: RecyclerViewFeedAdapter

    //dataShowing
    private          var emailTextArrayFromDB : ArrayList<String> = ArrayList()
    private          var usersTextArrayFromDB : ArrayList<String> = ArrayList()
    private          var dowloadLinksArrayFromDB : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_representation_screen)

        //initialize
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //recyclerView options
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewFeedAdapter = RecyclerViewFeedAdapter(emailTextArrayFromDB,usersTextArrayFromDB,dowloadLinksArrayFromDB)
        recyclerView.adapter = recyclerViewFeedAdapter

        //getData
        getDataFromFirestore()
    }

    private fun getDataFromFirestore() {
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
            if(e != null ) {
                Toast.makeText(this, e.localizedMessage!!.toString(), Toast.LENGTH_SHORT).show()
            }else {
                if(snapshot != null) {
                    if(!snapshot.isEmpty) {
                        usersTextArrayFromDB.clear()
                        emailTextArrayFromDB.clear()
                        dowloadLinksArrayFromDB.clear()

                        val documents = snapshot.documents
                        for(document in documents) {
                            val downloadUrl = document.get("downloadUrl") as String
                            val email = document.get("email") as String
                            val text = document.get("text") as String
                             //
                            usersTextArrayFromDB.add(text)
                            emailTextArrayFromDB.add(email)
                            dowloadLinksArrayFromDB.add(downloadUrl)
                            recyclerViewFeedAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}