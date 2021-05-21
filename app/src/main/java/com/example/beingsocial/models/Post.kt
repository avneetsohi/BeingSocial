package com.example.beingsocial.models

import com.example.beingsocial.Daos.UserDao
import com.google.firebase.firestore.auth.User
import java.util.*

data class Post (
    val text: String="",
    val createdBy:com.example.beingsocial.models.User=User(),
    val createdAt:Long=0L,
    val likedBy:ArrayList<String> = ArrayList())

