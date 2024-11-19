package com.depi.myapplication.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.myapplication.data.models.order.Order
import com.depi.myapplication.data.remote.FirebaseUtility
import com.depi.myapplication.ui.state.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    val firestore: FirebaseFirestore,
    private val firebaseUtility: FirebaseUtility
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    private val _errorState = MutableSharedFlow<String>()
    val errorState = _errorState.asSharedFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }
        firestore.runBatch {
            // TODO: Add the order into user-orders collection
            // TODO: Add the order into orders collection
            // TODO: Delete the products from user-cart collection

            firebaseUtility.addOrderIntoUserCollection(order)

            firebaseUtility.addOrderIntoOrderCollection(order)


            firebaseUtility.getCartProducts().get()
                .addOnSuccessListener { cartProducts ->
                    cartProducts.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _errorState.emit(it.message.toString())
                _order.emit(Resource.Error(_errorState.toString()))
            }
        }
    }

}










