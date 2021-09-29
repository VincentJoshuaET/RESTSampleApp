package com.vjet.sampleapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vjet.sampleapp.model.Comment
import com.vjet.sampleapp.service.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(private val service: ApiService) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>>(listOf())
    val comments = _comments.asStateFlow()

    fun getComments(postId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _comments.value = service.getComments(postId)
    }

}