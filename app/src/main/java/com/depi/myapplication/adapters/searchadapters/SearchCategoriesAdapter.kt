package com.depi.myapplication.adapters.searchadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.depi.myapplication.R
import com.depi.myapplication.data.models.Categories
import com.depi.myapplication.databinding.SearchCategoryItemBinding

class SearchCategoriesAdapter : RecyclerView.Adapter<SearchCategoriesViewHolder>() {

    // Using DiffUtil
    private val _diffUtil = object : DiffUtil.ItemCallback<Categories>() {
        override fun areItemsTheSame(oldItem: Categories, newItem: Categories): Boolean {
            return oldItem.rank == newItem.rank
        }

        override fun areContentsTheSame(oldItem: Categories, newItem: Categories): Boolean {
            return oldItem == newItem
        }

    }
    val diffUtil = AsyncListDiffer(this, _diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCategoriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.search_category_item,
            parent,
            false
        )
        return SearchCategoriesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: SearchCategoriesViewHolder, position: Int) {
        val category = diffUtil.currentList[position]
        holder.binding.apply {
            tvCategoryName.text = category.title
            Glide.with(holder.itemView).load(category.image).error(R.drawable.ic_cart)
                .into(imgCategory)

            holder.itemView.setOnClickListener {
                onCategoryClick?.invoke(category)
            }
        }
    }

    val onCategoryClick: ((Categories) -> Unit)? = null

}

class SearchCategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = SearchCategoryItemBinding.bind(itemView)
}