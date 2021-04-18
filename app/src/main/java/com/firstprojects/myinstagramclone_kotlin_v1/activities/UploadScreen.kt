package com.firstprojects.myinstagramclone_kotlin_v1

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import kotlin.random.Random

class UploadScreen : AppCompatActivity() {

    //declaration
   private lateinit var imageView : ImageView
   private lateinit var editText : EditText
   private          var bitmap : Bitmap? = null
   private lateinit var selectedImageUri : Uri
    //fb dec
   private lateinit var auth : FirebaseAuth
   private lateinit var storage : FirebaseStorage
   private lateinit var db : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_screen)

        //initilization
        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.uploadScreen_editText_text)
        auth = FirebaseAuth.getInstance()
        storage  = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun selectAnImage(view : View) {
    if(ContextCompat.checkSelfPermission(this@UploadScreen,READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE),1)
    }else {
        val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intentToGallery,2)

          }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data!!

            if(Build.VERSION.SDK_INT >= 28) {
                val source : ImageDecoder.Source = ImageDecoder.createSource(contentResolver,selectedImageUri)
                bitmap = ImageDecoder.decodeBitmap(source)
                imageView.setImageBitmap(bitmap)
            }else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedImageUri)
                    imageView.setImageBitmap(bitmap)
                }catch (e : Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && permissions[0] == READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentToGallery, 2)
        }
    }

        fun shareButton(view: View) {
            if (bitmap != null) {
                val typedText = editText.text.toString()
                val randomID = randomID(typedText.length,bitmap!!.height,bitmap!!.width)
                val imagePath = storage.reference.child("images/${randomID}.jpg")

                if(typedText.length < 400) {
                    //save section
                    imagePath.putFile(selectedImageUri).addOnCompleteListener {taskSnapShot ->
                        if (taskSnapShot.isSuccessful) {
                            imagePath.downloadUrl.addOnSuccessListener { uri ->
                                val downloadUrl = uri.toString()
                                val postHashMap = hashMapOf<String,Any>(
                                        "downloadUrl" to downloadUrl,
                                        "email" to auth.currentUser!!.email!!.toString(),
                                        "text" to typedText,
                                        "date" to Timestamp.now()
                                )
                                db.collection("Posts").add(postHashMap).addOnCompleteListener { task ->
                                    if(task.isSuccessful && task.isComplete) {
                                        finish()
                                    }else {
                                        Toast.makeText(this@UploadScreen, task.exception.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                }


                            }
                        }else {
                            println(taskSnapShot.exception.toString())
                        }
                    }

                }else {
                    Toast.makeText(this@UploadScreen,"you can not pass 400+ Chracters text!" , Toast.LENGTH_LONG).show()
                }
            }else {
                Toast.makeText(this@UploadScreen,"you have got to choose a picture!" , Toast.LENGTH_LONG).show()
            }
        }

    private fun randomID (textSize : Int ,bitmapsHeight : Int , bitmapsWidth : Int) : String {
        val randomInteger = Random
        val textSize = textSize * randomInteger.nextInt(10, 10000)
        return textSize.toString() + auth.currentUser?.email!!.toString() + bitmapsHeight.toString() + bitmapsWidth.toString() + randomInteger.nextInt().toString()
    }
    }

