package com.vikram.rxandroidsampleapp.switchMap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.vikram.rxandroidsampleapp.R
import com.vikram.rxandroidsampleapp.models.Post
import android.widget.TextView







class ViewPostActivity : AppCompatActivity() {

    private lateinit var text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)
        text = findViewById(R.id.text);

        getIncomingIntent();
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("post")) {
            val postTitle: String? = intent.getStringExtra("post_title")
            text.text = postTitle
        }
    }
}