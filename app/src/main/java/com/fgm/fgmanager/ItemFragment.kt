package com.fgm.fgmanager

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.fgm.fgmanager.placeholder.PlaceholderContent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.ArrayList

/**
 * A fragment representing a list of Items.
 */

class ItemFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)

        //val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        //val value = LocalDate.now().format(formatter) // Current Date
        //val parseDateNow = LocalDate.parse(value,formatter)      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
        //Log.d("TAG", value)

        //Log out From Account
        val mActivity : MainActivity = activity as MainActivity
        val tv_SignOut = mActivity.findViewById<TextView>(R.id.tv_SignOut)
        //SET Menu Bar
        val linear = mActivity.findViewById<LinearLayout>(R.id.layoutMenu)
        linear.visibility = View.VISIBLE

        tv_SignOut.setOnClickListener{
            //LoginDataSource::logout
            DialogToQuit(view)
        }

        requireActivity().onBackPressedDispatcher.addCallback(      //If press CallBack BUtton on phone
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    DialogToQuit(view)
                }
            }
        )
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS)
                addNewItem(view)
                //adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        //Take of Menu Bar
        val mActivity : MainActivity = activity as MainActivity
        //Set VISIBLE TextView SignOut
        val tv_SignOut = mActivity.findViewById<TextView>(R.id.tv_SignOut)
        tv_SignOut.visibility = View.VISIBLE
        //Set VISIBLE TextView User Name
        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
        tv_Users.visibility = View.VISIBLE
        //view.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
        //If you press CallBack`s Button on the phone
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    fun DialogToQuit(view : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialog =  builder.setTitle("Выход").setMessage("Вы уверены, что хотите выйти из приложения")
            .setTitle("Выход")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage("Вы уверены, что хотите выйти из приложения?")
            .setPositiveButton("OK"){
                    dialog, id ->
                logOut()
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_myLoginFragment)
            }
            .setNegativeButton("Отмена", null)
            .create()
        dialog.show()
    }

    fun addNewItem(RecView : RecyclerView){
        val newItem =
            arguments?.getStringArrayList("NameProductForFirebase")   //Receive arrayString Title of new products from Create Date Fragment
        if (newItem != null) {
            val item = PlaceholderContent.PlaceholderItem(newItem[0].toString(), newItem[1].toString(), newItem[2]. toString(), newItem[3].toString())

            myRef.push().setValue(item)
            PlaceholderContent.ITEMS.add(item)
            //RecView.adapter?.notifyItemInserted(STORAGE.localDsArray.size)    // If change, Reload Recycler View
            newItem.clear()
            PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
            RecView.adapter?.notifyDataSetChanged()
        }
    }

    fun logOut(){
        auth = Firebase.auth
        auth.signOut()
    }
}