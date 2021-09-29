package com.vjet.sampleapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comment(val postId: Int, val id: Int, val name: String, val email: String, val body: String)
