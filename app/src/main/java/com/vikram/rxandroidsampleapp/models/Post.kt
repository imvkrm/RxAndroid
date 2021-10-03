package com.vikram.rxandroidsampleapp.models

class Post(
     var userId: Int,  var id: Int,
     var title: String?,  var body: String?,
     var comments: List<Comment>
) {


    override fun toString(): String {
        return "Post{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}'
    }
}