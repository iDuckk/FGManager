package com.fgm.fgmanager.Adapters

import android.content.Context
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import com.fgm.fgmanager.Models.modelForProductItems
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent.PlaceholderItem
import com.fgm.fgmanager.R
import com.fgm.fgmanager.databinding.FragmentItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
var par : Context? = null
class MyItemRecyclerViewAdapter(
//    private val valuesReceivedList: MutableList<PlaceholderItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    var values : MutableList<PlaceholderItem> = ArrayList()
    set(value) {
        val callback = ItemsListCallback(values, value)  //values is oldList, value is newList
        val diffResult = DiffUtil.calculateDiff(callback, false)
        field = value
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        par = parent.context

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val model = modelForProductItems(par)

        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)
        //val itemDs = STORAGE.localDsArray[position]
//        holder.idView.text = item.id
//        holder.contentView.text = item.content

        SetColorPriorityRecycleView(holder, position)   //Set Color Priority
        SetTextItemRecycleView(holder, position)       //Set Text Recycle view
        model.DialogToDelete(holder, item.keyProduct, position)              //Delete Item

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        //        val idView: TextView = binding.itemNumber
//        val contentView: TextView = binding.content
//
//        override fun toString(): String {
//            return super.toString() + " '" + contentView.text + "'"
//        }
        val tv_Item_NameProduct: TextView = binding.tvItemName
        val tv_Item_Date: TextView = binding.tvItemDate
        val tv_Item_Barcode: TextView = binding.tvItemBarcode
        val tv_Item_Amout: TextView = binding.tvAmountDays
        val tv_Item_Title_NumberDays: TextView = binding.tvTitleNumberDays
        val im_Delete_Item : ImageView = binding.imDeleteItem
        val im_Button_Item: ImageView = binding.imGoToCreateDateFragment

    }

    fun SetColorPriorityRecycleView(holder: ViewHolder, pos : Int){
        val item = values[pos]
        val amountInt: Int? = item.amountDays.toIntOrNull()
            if (amountInt != null && amountInt > 0) {
                when {
                    amountInt <= 14 -> holder.tv_Item_NameProduct.setBackgroundResource(R.color.red)
                    amountInt > 30 -> holder.tv_Item_NameProduct.setBackgroundResource(R.color.green)
                    amountInt <= 30 -> holder.tv_Item_NameProduct.setBackgroundResource(R.color.orange)
                }
            } else
                holder.tv_Item_NameProduct.setBackgroundResource(R.color.gray)
    }

    fun SetTextItemRecycleView(holder: ViewHolder, pos : Int){
        val item = values[pos]
        val amountInt: Int? = item.amountDays.toIntOrNull()
        //Set Text in Cardholder Items
        holder.tv_Item_NameProduct.text = " " + item.productName
        holder.tv_Item_Date.text = " " + item.productDate
        holder.tv_Item_Barcode.text = " " + item.productBarcode // R.string.left_date.toString() + " $amountInt " + R.string.left_dates //
        //var str : String = R.string.left_date.toString()
        if (amountInt == null) {
            var str = R.string.expiration_date
            holder.tv_Item_Title_NumberDays.setText(str)
            holder.tv_Item_Amout.visibility = View.GONE
        } else {
            var str = R.string.left_date
            holder.tv_Item_Title_NumberDays.setText(str)
            holder.tv_Item_Amout.setText(" $amountInt")
        }
    }

}