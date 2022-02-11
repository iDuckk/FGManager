package com.fgm.fgmanager.Adapters

import androidx.recyclerview.widget.DiffUtil
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent

class ItemDiffCallback : DiffUtil.ItemCallback<PlaceholderContent.PlaceholderItem>(){
    override fun areItemsTheSame(
        oldItem: PlaceholderContent.PlaceholderItem,
        newItem: PlaceholderContent.PlaceholderItem
    ): Boolean {
        return oldItem.keyProduct == newItem.keyProduct
    }

    override fun areContentsTheSame(
        oldItem: PlaceholderContent.PlaceholderItem,
        newItem: PlaceholderContent.PlaceholderItem
    ): Boolean {
        return oldItem == newItem
    }
}