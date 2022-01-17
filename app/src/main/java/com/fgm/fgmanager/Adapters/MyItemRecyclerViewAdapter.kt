package com.fgm.fgmanager.Adapters

import android.app.AlertDialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import com.fgm.fgmanager.DBHelpers.DBHelper
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent.PlaceholderItem
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.fgm.fgmanager.databinding.FragmentItemBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        val tv_Item_Title_NumberDays: TextView = binding.tvTitleNumberDays
        val im_Delete_Item : ImageView = binding.imDeleteItem
        val im_Button_Item: ImageView = binding.imGoToCreateDateFragment
    }

    fun FirstElementAsButton(holder: ViewHolder, pos: Int) {
        if (pos == 0) {   //This first Item as a Button to Create a new ITEM
            holder.tv_Item_NameProduct.visibility = View.GONE
            holder.tv_Item_Date.visibility = View.GONE
            holder.tv_Item_Barcode.visibility = View.GONE
            holder.tv_Item_Amout.visibility = View.GONE
            holder.tv_Item_Title_NumberDays.visibility = View.GONE
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
            holder.tv_Item_Title_NumberDays.visibility = View.VISIBLE
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
        holder.tv_Item_Barcode.text = item.productBarcode // R.string.left_date.toString() + " $amountInt " + R.string.left_dates //
        //var str : String = R.string.left_date.toString()
        if (amountInt == null) {
            holder.tv_Item_Title_NumberDays.setText(R.string.expiration_date)
            holder.tv_Item_Amout.visibility = View.GONE
        } else {
            holder.tv_Item_Title_NumberDays.setText(R.string.left_date)
            holder.tv_Item_Amout.setText(" $amountInt")
        }
    }

    fun DialogToDelete(holder : ViewHolder, position : Int) {
        holder.im_Delete_Item.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(par)     //Set Dialog
            val dialog =
                builder.setTitle(R.string.Delete_Item).setMessage(R.string.Question_Delete_Item)
                    .setTitle(R.string.Delete_Item)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.Question_Delete_Item)
                    .setPositiveButton(R.string.Answer_Delete_Item_Yes) {       //When click "Yes"
                            dialog, id ->
                        if(STORAGE.TypeAccFree) {
//                            myRef.child(values[position].keyProduct)
//                                .removeValue()          //Delete Item
                            deleteFromFireStoreDB(position)
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
                    .setNegativeButton(R.string.Answer_Delete_Item_No, null)
                    .create()
            dialog.show()
        }
    }
    fun deleteFromFireStoreDB(position : Int){
        val dbFSDelete = Firebase.firestore
        //Add new Item in Firestore
        //val user = mapOf<String, PlaceholderContent.PlaceholderItem>(NameOfMapProd.toString() to item) //Create Map for sending
        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSDelete.collection(resultNameOfCollection).document("DataBase")
        // Remove the 'Item with number of keyProduct' field from the document
        val updates = hashMapOf<String, Any>(
            "${(values[position].keyProduct)}" to FieldValue.delete()
        )
        docRef.update(updates).addOnCompleteListener { }
        //Log.d("TAG", "Key product from MYRV fun delete${values[position].keyProduct}")
        //docRef.update(updates).addOnCompleteListener { }

//            .delete()
//            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
//            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
    }
}