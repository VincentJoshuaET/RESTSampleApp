package com.vjet.sampleapp.service

import com.vjet.sampleapp.model.Comment
import com.vjet.sampleapp.model.Post
import com.vjet.sampleapp.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("posts")
    suspend fun getPosts(@Query("_page") page: Int): List<Post>
    @GET("posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: Int): List<Comment>
    @GET("users")
    suspend fun getUsers(): List<User>
}