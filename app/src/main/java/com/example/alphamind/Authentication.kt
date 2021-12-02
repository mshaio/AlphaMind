package com.example.alphamind

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.example.alphamind.databinding.ActivityAuthenticationBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Authentication : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAuthenticationBinding

    // Configure Google Sign In
    private lateinit var auth: FirebaseAuth
//    private val googleSignInClient = GoogleSignIn.getClient(this, gso)
    private val RC_SIGN_IN = 1

    lateinit var signInClient: GoogleSignInClient
    lateinit var signInOptions: GoogleSignInOptions

    lateinit var userHandle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.brown_300)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.brown_300)
        }
        window.decorView.setBackgroundColor(resources.getColor(R.color.brown_300))

        if (isSignedIn()) {
            println("SINGED OUT")
            signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            signInClient = GoogleSignIn.getClient(this, signInOptions)
            signOut()
        } else {
            println("SIGNED IN")
            auth = FirebaseAuth.getInstance()

            val googleSignInButton: SignInButton = findViewById(R.id.sign_in_button)
            googleSignInButton.setOnClickListener {
                signIn()
            }
            setupGoogleLogin()
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

    private fun signOut() {
        signInClient.signOut()
        FirebaseAuth.getInstance().signOut()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                userHandle = account.displayName
                Toast.makeText(this, "FirebaseAuthWithGoogle" + account.displayName, Toast.LENGTH_LONG).show()
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
            println("LOGGED IN")
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

    fun isSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun getUserId (): String {
        if (FirebaseAuth.getInstance().currentUser != null) {
            return FirebaseAuth.getInstance().currentUser!!.uid.toString()
        }
        return ""
    }

}