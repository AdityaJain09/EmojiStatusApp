package com.stark.emoji_status_app.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
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
import com.stark.emoji_status_app.R


class LoginActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    private lateinit var loginBtn: SignInButton
    private lateinit var progress: ProgressBar
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth

    private val loginResultHandler = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Note Below is another way to handle activity result callback
                    // using StartIntentSenderForResult
//                val credential =
//                    Identity.getSignInClient(this).getSignInCredentialFromIntent(result.data)

                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                Log.d(TAG, "Sign In Failed", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        if (auth.currentUser == null) {
            setContentView(R.layout.activity_login)
            progress = findViewById(R.id.progress_bar)
            Log.i(TAG, "onCreate: Auth = ${auth.currentUser}")
            loginBtn = findViewById(R.id.btnSignIn)
            progress.visibility = View.GONE
            clickListener()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.client_id))
                    .requestEmail()
                    .build()
                googleSignInClient = GoogleSignIn.getClient(this, gso)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun clickListener() {

        loginBtn.setOnClickListener {
            googleSignInClient.signOut()
            loginResultHandler.launch(googleSignInClient.signInIntent)
            //    optional way to sign in using google
            //    signIn()
        }
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }


    // Another Way to Handle Deprecated StartActivityForResult, No Use In this Project.
    private fun signIn() {
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.client_id))
            .build()
        Identity.getSignInClient(this)
            .getSignInIntent(request)
            .addOnSuccessListener { result ->
               // loginResultHandler.launch(IntentSenderRequest.Builder(result.intentSender).build())
            }
            .addOnFailureListener { e -> Log.e(TAG, "Google Sign-in failed", e) }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            Log.w(TAG, "user not signed in..")
            return
        }
        Log.i(TAG, "updateUI: User Signed In Successfully.")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        // Navigate to MainActivity
    }
}