package com.vjet.sampleapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(val id: Int, val name: String)