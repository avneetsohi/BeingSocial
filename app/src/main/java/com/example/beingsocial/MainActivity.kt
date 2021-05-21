package com.example.beingsocial

import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beingsocial.Daos.PostDao
import com.example.beingsocial.models.Post
import com.example.beingsocial.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var fab: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var signOutButton:Button
    val auth= Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.postRV)
        signOutButton=findViewById(R.id.signOutButton)



        fab.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)

        }

        signOutButton.setOnClickListener{
            val currentUserID=auth.currentUser!!.uid
            val db=FirebaseFirestore.getInstance()
            val userCollection=db.collection("users")
            userCollection.document(currentUserID).delete()
            auth.signOut()
            val intent=Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()


    }

    fun setupRecyclerView() {
        postDao = PostDao()
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOption =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        adapter = PostAdapter(recyclerViewOption, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postID: String) {
        postDao.updateLikes(postID)

    }

}