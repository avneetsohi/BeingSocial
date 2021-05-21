package com.example.beingsocial.Daos

import com.example.beingsocial.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    val db= FirebaseFirestore.getInstance()
    val postCollection=db.collection("posts")
    val auth=Firebase.auth


    fun addPost(text:String)
    { val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val userDao= UserDao()
           val user=userDao.getUserById(currentUserId).await().toObject(com.example.beingsocial.models.User::class.java)!!
           val currentTime=System.currentTimeMillis()
            val post=Post(text,user,currentTime)
            postCollection.document().set(post)
        }
    }
    fun getPostByID(postID: String):Task<DocumentSnapshot>{
        return postCollection.document(postID).get()
    }


    fun updateLikes(postID:String){
        GlobalScope.launch(Dispatchers.IO) {
        val currentUserID=auth.currentUser!!.uid
        val post=getPostByID(postID).await().toObject(Post::class.java)
        val isLiked= post!!.likedBy!!.contains(currentUserID)
        if(isLiked == true){
            post.likedBy.remove(currentUserID)
        }
        else{
            post!!.likedBy!!.add(currentUserID)
        }
            postCollection.document(postID).set(post)
        }

    }

 }
