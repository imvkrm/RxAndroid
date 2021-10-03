package com.vikram.rxandroidsampleapp.switchMap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vikram.rxandroidsampleapp.R
import com.vikram.rxandroidsampleapp.models.Post


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    private val TAG = "RecyclerAdapter"

    private var posts: MutableList<Post> = ArrayList()
    private lateinit var onPostClickListener: OnPostClickListener

    fun RecyclerAdapter(onPostClickListener: OnPostClickListener?) {
        this.onPostClickListener = onPostClickListener!!
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_post_list_item_switch_map, null, false)
        return MyViewHolder(view,onPostClickListener)
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

    fun getPosts(): List<Post?> {
        return posts
    }

    class MyViewHolder(itemView: View, private var onPostClickListener: OnPostClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var title: TextView = itemView.findViewById(R.id.title)
        fun bind(post: Post) {
            title.text = post.title
        }

        override fun onClick(v: View) {
            onPostClickListener.onPostClick(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnPostClickListener {
        fun onPostClick(position: Int)
    }

}