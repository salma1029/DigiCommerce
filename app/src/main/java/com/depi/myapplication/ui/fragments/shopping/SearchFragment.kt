package com.depi.myapplication.ui.fragments.shopping

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.depi.myapplication.R
import com.depi.myapplication.adapters.searchadapters.SearchAdapter
import com.depi.myapplication.adapters.searchadapters.SearchCategoriesAdapter
import com.depi.myapplication.databinding.FragmentSearchBinding
import com.depi.myapplication.ui.state.Resource
import com.depi.myapplication.ui.viewmodel.shopping.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val categoriesAdapter by lazy { SearchCategoriesAdapter() }
    private val searchAdapter by lazy { SearchAdapter() }
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var inputMethodManger: InputMethodManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupSearchRv()
        showKeyboardAutomatically()
        onHomeClick()
        searchProducts()
        handleSearchState()

        onItemClicked()
        stopSearching()

    }

    private fun stopSearching() {
        binding.tvCancel.setOnClickListener {
            searchAdapter.diffUtil.submitList(emptyList())
            binding.edSearch.setText("")
            hideCancelTv()
        }
    }

    private fun onItemClicked() {
        searchAdapter.onProductClicked = { product ->
            // TODO: 1- Hide Keyboard
            //       2- Go to Product Details
            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)

            val action =
                SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(product)

            findNavController().navigate(
                action
            )
        }
    }


    private fun handleSearchState() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                searchViewModel.searchState.collectLatest { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.productSearchProgress.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            if (state.data!!.isNotEmpty()) {
                                binding.tvNoItems.visibility = View.GONE
                                binding.noItems.visibility = View.GONE
                                searchAdapter.diffUtil.submitList(state.data)
                                binding.productSearchProgress.visibility = View.GONE
                                showChancelTv()
                            } else {
                                binding.tvNoItems.visibility = View.VISIBLE
                                binding.noItems.visibility = View.VISIBLE
                                binding.productSearchProgress.visibility = View.GONE
                            }

                        }

                        is Resource.Error -> {
                            showChancelTv()
                            searchViewModel.searchErrorState.collectLatest {
                                // TODO: Using SharedStateFlow not to reply the case in rotating screen
                                binding.edSearch.error = state.message.toString()
                            }
                        }

                        else -> Unit
                    }

                }
            }
        }
    }

    // TODO: Search Products By Capitalize first Character and search with free the resource in case of error
    private fun searchProducts() {
        var job: Job? = null
        binding.edSearch.addTextChangedListener { query ->
            val trimmedQuery = query.toString().trim()
            if (trimmedQuery.isNotEmpty()) { // there is keyword
                //TODO: Capitalize first char and combine it with the whole word
                val finalQuery = query.toString().subSequence(0, 1)
                    .toString().uppercase().plus(
                        query.toString().substring(1)
                    )
                // TODO: If there is an ongoing search job, it gets cancelled to avoid unnecessary searches
                //  (especially if the user keeps typing quickly).
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(400L)
                    searchViewModel.searchProduct(finalQuery)
                }
            } else { // TODO: There is no key words found , so pass empty list to show empty list
                searchAdapter.diffUtil.submitList(emptyList())
            }
        }
    }

    private fun setupSearchRv() {
        binding.rvSearch.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }

    }

    private fun showKeyboardAutomatically() {
        inputMethodManger =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )

        binding.edSearch.requestFocus()
    }

    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
    }

    private fun showChancelTv() {

        binding.tvCancel.visibility = View.VISIBLE


    }

    private fun hideCancelTv() {
        binding.tvCancel.visibility = View.GONE

    }


    override fun onResume() {
        super.onResume()
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.visibility = View.VISIBLE
    }
}