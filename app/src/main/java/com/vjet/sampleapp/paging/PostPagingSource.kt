package com.vjet.sampleapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vjet.sampleapp.model.Post
import com.vjet.sampleapp.model.User
import com.vjet.sampleapp.service.ApiService
import retrofit2.HttpException
import java.io.IOException

class PostPagingSource(private val service: ApiService) : PagingSource<Int, Pair<Post, User>>() {
    override fun getRefreshKey(state: PagingState<Int, Pair<Post, User>>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position) ?: return null
            page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pair<Post, User>> {
        return try {
            val page = params.key ?: 1
            val posts = service.getPosts(page)
            val users = service.getUsers()
            val pair = posts.associateWith { post ->
                users.first { it.id == post.userId }
            }.toList()
            LoadResult.Page(
                data = pair,
                prevKey = if (page == 1) null else page,
                nextKey = if (posts.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}