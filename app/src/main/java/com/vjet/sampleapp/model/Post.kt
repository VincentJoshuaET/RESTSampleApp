package com.vjet.sampleapp.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Post(val id: Int, val userId: Int, val title: String, val body: String) : Parcelable
