package com.depi.myapplication.adapters.searchadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.depi.myapplication.R
import com.depi.myapplication.data.models.Product
import com.depi.myapplication.databinding.ProductSearchItemBinding


class SearchAdapter : RecyclerView.Adapter<SearchViewHolder>() {
    // Using DiffUtil
    private val _diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    val diffUtil = AsyncListDiffer(this, _diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.product_search_item,
            parent,
            false
        )
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val product = diffUtil.currentList[position]
        holder.binding.tvSearchedWord.text = product.name
        Glide.with(holder.itemView).load(product.images?.get(0))
            .placeholder(R.drawable.ic_cart)
            .into(holder.binding.searchImageProduct)
        holder.itemView.setOnClickListener {
            onProductClicked?.invoke(product)
        }


    }

    var onProductClicked: ((Product) -> Unit)? = null
}

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ProductSearchItemBinding.bind(itemView)
}