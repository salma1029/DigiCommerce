package com.depi.myapplication.ui.viewmodel.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.myapplication.data.models.Product
import com.depi.myapplication.data.remote.FirebaseUtility
import com.depi.myapplication.ui.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firebaseUtility: FirebaseUtility
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchState = _searchState.asStateFlow()
    val searchErrorState = _searchState.asSharedFlow()

    fun searchProduct(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchState.emit(Resource.Loading())
        }
        firebaseUtility.searchProduct(query)
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch(Dispatchers.IO) {
                    _searchState.emit(Resource.Success(products))
                }
            }.addOnFailureListener { error ->
                viewModelScope.launch(Dispatchers.IO) {
                    _searchState.emit(Resource.Error(error.message.toString()))
                }
            }

    }


}