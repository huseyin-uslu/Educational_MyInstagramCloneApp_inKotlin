package com.firstprojects.myinstagramclone_kotlin_v1.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firstprojects.myinstagramclone_kotlin_v1.R
import com.squareup.picasso.Picasso


class RecyclerViewFeedAdapter(
             private val emailArray:ArrayList<String> ,
             private val usersTextArray:ArrayList<String>,
             private val imageUriArray:ArrayList<String>) : RecyclerView.Adapter<RecyclerViewFeedAdapter.PostHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.recyclerview_row,parent,false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.textViewEmail!!.text = emailArray[position]
        holder.usersText!!.text = usersTextArray[position]
        Picasso.get().load(imageUriArray[position]).into(holder.imageView)

    }

    override fun getItemCount(): Int {
    return emailArray.size
    }

    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       var textViewEmail : TextView? = null
       var usersText : TextView? = null
       var imageView : ImageView? = null
   init {
       textViewEmail = itemView.findViewById(R.id.recyclerViewLayout_rowsScreen_emailName)
       usersText = itemView.findViewById(R.id.recyclerViewLayout_rowsScreen_usersText)
       imageView = itemView.findViewById(R.id.recyclerViewLayout_rowsScreen_imageView)
   }

    }
}