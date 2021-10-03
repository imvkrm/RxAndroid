package com.vikram.rxandroidsampleapp.flatMap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.vikram.rxandroidsampleapp.R
import com.vikram.rxandroidsampleapp.models.Post


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    private val TAG = "RecyclerAdapter"

    private var posts: MutableList<Post> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_post_list_item, null, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (posts.isNotEmpty())
            holder.bind(posts[position]);
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun setPosts(posts: MutableList<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    fun updatePost(post: Post) {
        posts[posts.indexOf(post)] = post
        notifyItemChanged(posts.indexOf(post))
    }

    fun getPosts(): List<Post?>? {
        return posts
    }

    class MyViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var numComments: TextView = itemView.findViewById(R.id.num_comments)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        fun bind(post: Post) {
            title.text = post.title
            showProgressBar(false)
            if (!post.comments.isNullOrEmpty())
                numComments.text = "${post.comments.size}"
            else {
                showProgressBar(true);
                numComments.text = "";
            }
        }

        private fun showProgressBar(showProgressBar: Boolean) {
            if (showProgressBar) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }

    }

}