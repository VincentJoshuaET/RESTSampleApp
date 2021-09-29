package com.vjet.sampleapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.vjet.sampleapp.databinding.FragmentPostsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class PostsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementReturnTransition = MaterialContainerTransform(requireContext(), false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentPostsBinding.inflate(inflater, container, false)
        _binding = binding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            binding.recyclerView.updatePadding(bottom = navigationBars.bottom, right = navigationBars.right, left = navigationBars.left)
            insets
        }
        binding.swipeRefreshLayout.isEnabled = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        val adapter = PostAdapter { itemView, post, user ->
            val extras = FragmentNavigatorExtras(itemView to "transition${post.id}")
            findNavController().navigate(PostsFragmentDirections.actionPostsToComments(post, user.name), extras)
        }
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collectLatest(adapter::submitData)
        }
        val text = "Error loading data. Check your internet connection."
        val snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE)
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { states ->
                if (adapter.itemCount <= 0) {
                    when (states.refresh) {
                        LoadState.Loading -> {
                            binding.swipeRefreshLayout.isRefreshing = true
                            job?.cancel()
                            job = null
                        }
                        is LoadState.NotLoading -> {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        is LoadState.Error -> {
                            binding.textView.text = text
                            binding.textView.isVisible = true
                            binding.swipeRefreshLayout.isRefreshing = false
                            job = viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.isConnected.collectLatest { connected ->
                                    if (connected) adapter.retry()
                                }
                            }
                        }
                    }
                } else {
                    when (states.append) {
                        LoadState.Loading -> {
                            snackbar.dismiss()
                            binding.swipeRefreshLayout.isRefreshing = true
                            binding.textView.isVisible = false
                            job?.cancel()
                            job = null
                        }
                        is LoadState.NotLoading -> {
                            snackbar.dismiss()
                            binding.swipeRefreshLayout.isRefreshing = false
                            binding.textView.isVisible = false
                        }
                        is LoadState.Error -> {
                            snackbar.show()
                            binding.swipeRefreshLayout.isRefreshing = false
                            job = viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.isConnected.collectLatest { connected ->
                                    if (connected) adapter.retry()
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}