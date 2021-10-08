package com.example.alphamind

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.alphamind.databinding.ActivityAuthenticationBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Authentication : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAuthenticationBinding

    // Configure Google Sign In
    private lateinit var auth: FirebaseAuth
//    private val googleSignInClient = GoogleSignIn.getClient(this, gso)
    private val RC_SIGN_IN = 1

    lateinit var signInClient: GoogleSignInClient
    lateinit var signInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        println("ONCREATE")
        println("GSO")
        auth = FirebaseAuth.getInstance()

        val googleSignInButton: SignInButton = findViewById(R.id.sign_in_button)
        googleSignInButton.setOnClickListener {
            println("Clicked Sign In")
            signIn()
        }
        setupGoogleLogin()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun setupGoogleLogin() {
        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, signInOptions)
    }

    private fun signIn() {
        val signInIntent = signInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                Toast.makeText(this, "FirebaseAuthWithGoogle" + account.id, Toast.LENGTH_LONG).show()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        println("Logged in??")
//        println(currentUser)
////        updateUI(currentUser)
//    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            println("LOGGED  IN")
//            startActivity(LoggedInActivity.getLaunchIntent(this))
            finish()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "signInWithCredential: success", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
//                    updateUI(null)
                }
            }
    }

}