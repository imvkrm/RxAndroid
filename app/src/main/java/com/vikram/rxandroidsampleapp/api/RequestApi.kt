package com.vikram.rxandroidsampleapp.api

import com.vikram.rxandroidsampleapp.models.Comment
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.http.GET
import okhttp3.ResponseBody
import com.vikram.rxandroidsampleapp.models.Post
import retrofit2.http.Path





interface RequestApi {

    @GET("todos/1")
    fun makeObservableQueryF(): Observable<ResponseBody>//It's able to return an Observable because of the RxJava Call Adapter dependency

    @GET("todos/1")
    fun makeObservableQueryP(): Flowable<ResponseBody>//It's able to return an Observable because of the RxJava Call Adapter dependency


    @GET("posts")
    fun getPosts(): Observable<MutableList<Post>>

    @GET("posts/{id}/comments")
    fun getComments(@Path("id") id: Int): Observable<MutableList<Comment>>

    @GET("posts/{id}")
    fun getPost(@Path("id") id: Int): Observable<Post>
}