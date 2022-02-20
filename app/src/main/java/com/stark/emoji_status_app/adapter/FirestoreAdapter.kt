package com.stark.emoji_status_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.stark.emoji_status_app.databinding.ItemViewBinding
import com.stark.emoji_status_app.model.User
import com.stark.emoji_status_app.RandomColorUtil

class FirestoreAdapter(
    private val context: Context,
    options: FirestoreRecyclerOptions<User>,
    private val currentUserName: String?,
    private val onEditClick: () -> Unit
    ) : FirestoreRecyclerAdapter<User, FirestoreAdapter.FirestoreUserViewHolder>(
    options) {

    class FirestoreUserViewHolder  constructor(
        val binding: ItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(position: Int, model: User) {
            binding.nameTv.text = model.displayName
            binding.emojiTv.text = model.emojis
            binding.profileTv.text = model.displayName.first().toString()
        }

        companion object {
            fun createView(parent: ViewGroup): FirestoreUserViewHolder {
                val layoutInflator = LayoutInflater.from(parent.context)
                val view = ItemViewBinding.inflate(layoutInflator, parent, false)
                return FirestoreUserViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirestoreUserViewHolder {
        return FirestoreUserViewHolder.createView(parent)
    }

    override fun onBindViewHolder(
        holderUser: FirestoreUserViewHolder,
        position: Int,
        model: User
    ) {
        if (currentUserName != null && currentUserName == model.displayName) {
            holderUser.binding.editIv.visibility = View.VISIBLE
        }
        holderUser.binding.textContainer.setBackgroundColor(
            ContextCompat.getColor(context, RandomColorUtil.getRandomColor())
        )
        holderUser.binding.editIv.setOnClickListener {
            onEditClick.invoke()
        }
        holderUser.bind(position, model)
    }
}