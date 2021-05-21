package com.example.beingsocial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.example.beingsocial.Daos.UserDao
import com.example.beingsocial.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {

    private lateinit var googleSignInClient:GoogleSignInClient
    private val RC_SIGN_IN: Int=123
    private lateinit var signInButton: SignInButton
    private lateinit var auth: FirebaseAuth
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        signInButton=findViewById(R.id.signInButton)
        auth = Firebase.auth
        progressBar=findViewById(R.id.Progressbar)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton.setOnClickListener{
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handlesignIn(task)

            }
        }

    private fun handlesignIn(completedTask: Task<GoogleSignInAccount>?) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account =
                completedTask?.getResult(ApiException::class.java)!!
            Log.d("FragmentActivity", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w("FragmentActivity", "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential=GoogleAuthProvider.getCredential(idToken,null)
        signInButton.visibility=View.GONE
        progressBar.visibility=View.VISIBLE


        GlobalScope.launch(Dispatchers.IO) {
            val auth=auth.signInWithCredential(credential).await()
            val firebaseUser=auth.user
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)

            }


        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser!=null) {
            val user= User(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
            val userDao= UserDao()
            userDao.addUser(user)

            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }
        else{signInButton.visibility=View.VISIBLE
            progressBar.visibility=View.GONE


        }



    }


}



