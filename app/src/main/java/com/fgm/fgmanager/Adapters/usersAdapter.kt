package com.fgm.fgmanager.Adapters

//class usersAdapter {
//}


import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.navigation.Navigation
import com.fgm.fgmanager.PoJo.User
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.fgm.fgmanager.databinding.FragmentItemUsersBinding

//var parentContext : Context? = null

class usersAdapter(
    private val values: List<User>
) : RecyclerView.Adapter<usersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        parentContext = parent.context
        return ViewHolder(
            FragmentItemUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
//        holder.idView.text = item.id
//        holder.contentView.text = item.content

        holder.et_UserName.setText(item.User)
        holder.et_UserPassword.setText(item.Password)
//        Log.d("TAG1","UserAdapter")
//        holder.et_UserName.text = item.User
//        holder.et_UserPassword.text = item.Password

        //holder.et_UserPassword.setOnFocusChangeListener{view, b -> holder.et_UserPassword.setEnabled(true)}

        holder.im_Edit_Item.setOnClickListener {
//            if(STORAGE.checkPressOtherRow) {
//                holder.et_UserPassword.setEnabled(true)
//                STORAGE.checkPressOtherRow = false
//            }else{
//                holder.et_UserPassword.setEnabled(false)
//                STORAGE.checkPressOtherRow = true
//            }
            STORAGE.OldPassword = item.Password
            STORAGE.userNameForChangePass = item.User
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.action_itemUsersFragment_to_updatePasswordFragment) // Go to Create New Password Fragment
        }

        if(item.User == "Button"){  //Button For Return Back to MENU
            holder.et_UserName.visibility = View.GONE
            holder.et_UserPassword.visibility = View.GONE
            holder.im_Edit_Item.visibility = View.GONE
            holder.b_Back.visibility = View.VISIBLE
            holder.b_Back.setOnClickListener {
                Navigation.findNavController(holder.itemView)
                    .navigate(R.id.action_itemUsersFragment_to_menuFragment) // Go to Create Fragment
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        //        val idView: TextView = binding.itemNumber
//        val contentView: TextView = binding.content
//
//        override fun toString(): String {
//            return super.toString() + " '" + contentView.text + "'"
//        }
        val et_UserName: EditText = binding.etUsersUserName
        val et_UserPassword: EditText = binding.etUsersPassword
        val im_Edit_Item : ImageView = binding.imUsesrsEdit
        val b_Back : Button = binding.bUserBACK
    }
}