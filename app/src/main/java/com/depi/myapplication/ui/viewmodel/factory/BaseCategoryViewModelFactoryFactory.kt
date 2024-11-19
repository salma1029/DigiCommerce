package com.depi.myapplication.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.depi.myapplication.ui.viewmodel.shopping.CategoryViewModel
import com.depi.myapplication.data.models.Category
import com.depi.myapplication.data.remote.FirebaseUtility

class BaseCategoryViewModelFactoryFactory(

    private val category: Category,
    private val firebaseUtility: FirebaseUtility
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(category,firebaseUtility) as T
    }
}