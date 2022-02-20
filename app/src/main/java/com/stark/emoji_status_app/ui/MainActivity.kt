package com.stark.emoji_status_app.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stark.emoji_status_app.EmojiFilter
import com.stark.emoji_status_app.R
import com.stark.emoji_status_app.adapter.FirestoreAdapter
import com.stark.emoji_status_app.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recycleView: RecyclerView
    private lateinit var firestoreAdapter: FirestoreAdapter
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycleView = findViewById(R.id.user_list_rv)
        auth = Firebase.auth
        val currentUser = auth.currentUser?.displayName ?: getString(R.string.no_user_signed_in)
        recyclerviewSetup()
        title = currentUser
        Toast.makeText(this, "Sign In as $currentUser", Toast.LENGTH_SHORT).show()
    }

    private fun recyclerviewSetup() {
        val query = db.collection("users").orderBy(
            "createdDate", Query.Direction.DESCENDING
        )
        val fireStoreOptions: FirestoreRecyclerOptions<User> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()
        val name = if (auth.currentUser != null) auth.currentUser!! else null
        firestoreAdapter = FirestoreAdapter(this, fireStoreOptions, name?.displayName, editDialog)
        recycleView.adapter = firestoreAdapter
        recycleView.layoutManager = LinearLayoutManager(this)
    }

    // Not Recommended to do this way. Better way to update emoji.
    private val editDialog =  {
        val editText = EditText(this)
        val maxLengthFilter = InputFilter.LengthFilter(9)
        editText.filters = arrayOf(maxLengthFilter, EmojiFilter(this))
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.update_emoji)
            .setView(editText)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.update, null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).apply {
            setOnClickListener {
                val updatedEmoji = editText.text.toString()
                val currentUser = auth.currentUser
                if (updatedEmoji.isBlank()) {
                    Toast.makeText(this@MainActivity,
                        "Cannot Update Blank Emoji",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // update user emoji if current user not null
                if (currentUser != null && currentUser.displayName != null) {
                    val user = User(currentUser.displayName!!, updatedEmoji)
                        db.collection("users").document(currentUser.uid)
                            .set(user)
                    Toast.makeText(this@MainActivity,
                    "Emoji Updated Successfully.",
                    Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@MainActivity,
                    "No User Found",
                     Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
        }
        Unit
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout ->  {
                auth.signOut()
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
                finish()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}