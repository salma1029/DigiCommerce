package com.depi.myapplication.ui.viewmodel.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.myapplication.data.models.Product
import com.depi.myapplication.data.remote.FirebaseUtility
import com.depi.myapplication.ui.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firebaseUtility: FirebaseUtility

) : ViewModel() {


    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    private fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firebaseUtility.getSpecialProducts().addOnSuccessListener { result ->
            val specialProductsList = result.toObjects(Product::class.java)
            viewModelScope.launch {
                _specialProducts.emit(Resource.Success(specialProductsList))
            }

        }.addOnFailureListener {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    private fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }
        firebaseUtility.getBestDeals()
            .addOnSuccessListener { result ->
                val bestDealsProducts = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProducts))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
                firebaseUtility.getBestProducts(pagingInfo.bestProductsPage * 10)
                    .addOnSuccessListener { result ->
                        val bestProducts = result.toObjects(Product::class.java)
                        pagingInfo.isPagingEnd = bestProducts == pagingInfo.oldBestProducts
                        pagingInfo.oldBestProducts = bestProducts
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Success(bestProducts))
                        }
                        pagingInfo.bestProductsPage++
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            _bestProducts.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }
        }
    }


}

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)
