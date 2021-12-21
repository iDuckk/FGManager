package com.fgm.fgmanager

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import com.fgm.fgmanager.DBHelpers.DBHelper

import com.fgm.fgmanager.placeholder.PlaceholderContent.PlaceholderItem
import com.fgm.fgmanager.databinding.FragmentItemBinding
import com.fgm.fgmanager.placeholder.PlaceholderContent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
var par : Context? = null
class MyItemRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)
        //val itemDs = STORAGE.localDsArray[position]
//        holder.idView.text = item.id
//        holder.contentView.text = item.content

//        FirstElementAsButton(holder, position) // Create first element as Button
        SetColorPriorityRecycleView(holder, position)   //Set Color Priority
        SetTextItemRecycleView(holder, position)       //Set Text Recycle view
        DialogToDelete(holder, position)              //Delete Item
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
        val im_Delete_Item : ImageView = binding.imDeleteItem
        val im_Button_Item: ImageView = binding.imGoToCreateDateFragment
    }

    fun FirstElementAsButton(holder: ViewHolder, pos: Int) {
        if (pos == 0) {   //This first Item as a Button to Create a new ITEM
            holder.tv_Item_NameProduct.visibility = View.GONE
            holder.tv_Item_Date.visibility = View.GONE
            holder.tv_Item_Barcode.visibility = View.GONE
            holder.tv_Item_Amout.visibility = View.GONE
            holder.im_Delete_Item.visibility = View.GONE
            holder.im_Button_Item.visibility = View.VISIBLE     //Show Image for "Click Button"

            holder.itemView.setOnClickListener {
                Navigation.findNavController(holder.itemView)
                    .navigate(R.id.action_itemFragment_to_craeteDateFragment) // Go to Create Fragment
                //Log.i("123","EGOR")
            }
        } else {
            holder.tv_Item_NameProduct.visibility = View.VISIBLE
            holder.tv_Item_Date.visibility = View.VISIBLE
            holder.tv_Item_Barcode.visibility = View.VISIBLE
            holder.tv_Item_Amout.visibility = View.VISIBLE
            holder.im_Delete_Item.visibility = View.VISIBLE
            holder.im_Button_Item.visibility = View.GONE
        }
    }

    fun SetColorPriorityRecycleView(holder: ViewHolder, pos : Int){
        val item = values[pos]
        val amountInt: Int? = item.amountDays.toIntOrNull()
//        if(pos != 0) {
            if (amountInt != null && amountInt > 0) {
                when {
                    amountInt <= 14 -> holder.tv_Item_NameProduct.setBackgroundResource(R.color.red)
                    amountInt > 30 -> holder.tv_Item_NameProduct.setBackgroundResource(R.color.green)
                    amountInt <= 30 -> holder.tv_Item_NameProduct.setBackgroundResource(R.color.orange)
                }
            } else
                holder.tv_Item_NameProduct.setBackgroundResource(R.color.grey)
//        }
    }
    fun SetTextItemRecycleView(holder: ViewHolder, pos : Int){
        val item = values[pos]
        val amountInt: Int? = item.amountDays.toIntOrNull()
        //Set Text in Cardholder Items
        holder.tv_Item_NameProduct.text = item.productName
        holder.tv_Item_Date.text = item.productDate
        holder.tv_Item_Barcode.text = item.productBarcode
        if (amountInt == null) holder.tv_Item_Amout.text ="Срок истек" else holder.tv_Item_Amout.text = "Осталось $amountInt дней"
    }

    fun DialogToDelete(holder : ViewHolder, position : Int) {
        holder.im_Delete_Item.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(par)     //Set Dialog
            val dialog =
                builder.setTitle("Удалить").setMessage("Удалить элемент?")
                    .setTitle("Удалить")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Удалить элемент?")
                    .setPositiveButton("Да") {       //When click "Yes"
                            dialog, id ->
                        if(STORAGE.TypeAccFree) {
                            myRef.child(values[position].keyProduct)
                                .removeValue()          //Delete Item
                            //Log.d("TAG", values[position].keyProduct)
                        }else{
                            val db = DBHelper(par!!,null)
                            db.deleteCourse(values[position].keyProduct.toInt())
                            //Log.d("TAG", values[position].keyProduct.toString())
                        }
                        //action_itemFragment_self
                        Navigation.findNavController(holder.itemView)
                            .navigate(R.id.action_itemFragment_self) // That refresh ItemFragment
                    }
                    .setNegativeButton("Нет", null)
                    .create()
            dialog.show()
        }
    }
}