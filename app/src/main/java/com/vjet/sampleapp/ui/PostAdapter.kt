package com.vjet.sampleapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vjet.sampleapp.databinding.ItemPostBinding
import com.vjet.sampleapp.model.Post
import com.vjet.sampleapp.model.User

class PostAdapter(private val listener: (View, Post, User) -> Unit) : PagingDataAdapter<Pair<Post, User>, PostAdapter.PostViewHolder>(Callback) {

    class PostViewHolder(binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val textViewTitle = binding.textViewTitle
        val textViewBody = binding.textViewBody
        val textViewUser = binding.textViewUser
    }

    private companion object Callback : DiffUtil.ItemCallback<Pair<Post, User>>() {
        override fun areItemsTheSame(oldItem: Pair<Post, User>, newItem: Pair<Post, User>): Boolean {
            return oldItem.first.id == newItem.first.id
        }

        override fun areContentsTheSame(oldItem: Pair<Post, User>, newItem: Pair<Post, User>): Boolean {
            return oldItem.first == newItem.first
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val pair = getItem(position) ?: return
        holder.itemView.transitionName = "transition${pair.first.id}"
        holder.textViewTitle.text = pair.first.title
        holder.textViewBody.text = pair.first.body

        val text = "Posted by ${pair.second.name}"
        holder.textViewUser.text = text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val (post, user) = getItem(position) ?: return@setOnClickListener
                    listener(itemView, post, user)
                }
            }
        }
    }

}