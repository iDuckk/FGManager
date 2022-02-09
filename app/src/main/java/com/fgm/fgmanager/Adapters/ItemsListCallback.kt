package com.fgm.fgmanager.Adapters

import androidx.recyclerview.widget.DiffUtil
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent

class ItemsListCallback(val oldList: MutableList<PlaceholderContent.PlaceholderItem>, val newList: MutableList<PlaceholderContent.PlaceholderItem>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.keyProduct == newItem.keyProduct
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}