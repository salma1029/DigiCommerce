package com.depi.myapplication.ui.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.depi.myapplication.ui.state.Resource
import com.depi.myapplication.ui.viewmodel.shopping.CategoryViewModel
import com.depi.myapplication.ui.viewmodel.factory.BaseCategoryViewModelFactoryFactory
import com.depi.myapplication.data.models.Category
import com.depi.myapplication.data.remote.FirebaseUtility
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint

class FurnitureFragment : BaseCategoryFragment() {


    @Inject
    lateinit var firebaseUtility: FirebaseUtility

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactoryFactory( Category.Accessory,firebaseUtility)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.offerProducts.collect {
                    when (it) {
                        is Resource.Loading<*> -> {
                            showOfferLoading()
                        }

                        is Resource.Success<*> -> {
                            offerAdapter.differ.submitList(it.data)
                            hideOfferLoading()
                        }

                        is Resource.Error<*> -> {
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            hideOfferLoading()
                        }

                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.bestProducts.collect {
                    when (it) {
                        is Resource.Loading<*> -> {
                            showBestProductsLoading()
                        }

                        is Resource.Success<*> -> {
                            bestProductsAdapter.differ.submitList(it.data)
                            hideBestProductsLoading()
                        }

                        is Resource.Error<*> -> {
                            Snackbar.make(
                                requireView(),
                                it.message.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            hideBestProductsLoading()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }



    override fun onBestProductsPagingRequest() {
    }

    override fun onOfferPagingRequest() {
    }

}