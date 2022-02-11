package com.fgm.fgmanager.Fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.fgm.fgmanager.*
import com.fgm.fgmanager.Adapters.MyItemRecyclerViewAdapter
import com.fgm.fgmanager.Models.modelForProductItems
import com.fgm.fgmanager.Models.modelHelpers
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent
import com.google.android.gms.ads.AdView
import io.reactivex.rxjava3.core.Observable

/**
 * A fragment representing a list of Items.
 */

var myAdapter : MyItemRecyclerViewAdapter  = MyItemRecyclerViewAdapter()

class ItemFragment : Fragment() {

    private var columnCount = 1

    @RequiresApi(Build.VERSION_CODES.O)
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

        val model = modelForProductItems(requireContext())
        val modelHelpers = modelHelpers(activity as MainActivity)

        val mActivity : MainActivity = activity as MainActivity
        val tv_SignOut = mActivity.findViewById<TextView>(R.id.tv_SignOut)
        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
        val linear = mActivity.findViewById<LinearLayout>(R.id.layoutMenu)
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
        val b_AddProduct = mActivity.findViewById<Button>(R.id.button_Add_Product)
        val ad_FirstBaneer = mActivity.findViewById<AdView>(R.id.adViewFirstBanner)

        //Set AdView Banner
        if(STORAGE.TypeAccFree){
            tv_SignOut.setText(R.string.menu)
            ad_FirstBaneer.visibility = View.GONE
        }else {
            tv_SignOut.setText(R.string.sign_out)
            ad_FirstBaneer.visibility = View.VISIBLE
        }
        //Set Visible Button Add New Product
        b_AddProduct.visibility = View.VISIBLE
        //SET Menu Bar
        linear.visibility = View.VISIBLE
        //Set VISIBLE TextView SignOut
        tv_SignOut.visibility = View.VISIBLE
        //Set VISIBLE TextView User Name
        tv_Users.visibility = View.VISIBLE
        //Set User Name inText View
        tv_Users.setText(STORAGE.UserName)


        b_AddProduct.setOnClickListener{
//            if(!STORAGE.TypeAccFree) {
//                var currentDate = Calendar.getInstance() // Receive current Date
//                if(STORAGE.AdPressButton) { //Show Ad only each 4 hours (currentDate.time.hours % 4 == 0) OR If you Press Button First time until you close App
//                    mActivity.showInterAd() //Show Add
//                    STORAGE.AdPressButton = false
//                }
//            }

                Navigation.findNavController(view)
                .navigate(R.id.action_itemFragment_to_craeteDateFragment) // Go to Create Fragment
        }

        tv_SignOut.setOnClickListener{
            //LoginDataSource::logout
            if(STORAGE.TypeAccFree){
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_menuFragment) //Menu
            }else{
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_myLoginFragment) //Offline DataBase
            }
        }

        //If press CallBack BUtton on phone
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(STORAGE.TypeAccFree) {
                        modelHelpers.DialogToQuit(view, STORAGE.itemFrag)
                    }else
                        Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_myLoginFragment)
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
                //view.itemAnimator = null
                adapter = myAdapter
                //Set Database for RecViewList
                if(STORAGE.TypeAccFree){
                if(PlaceholderContent.ITEMS.isEmpty()){
                    model.CreateFireStoreDB(view, myAdapter)
                }
                }else{
//                    if(PlaceholderContent.ITEMS.isEmpty()){
                    model.CreateLocalDataBase(myAdapter)    //Offline DataBase
                }
                progressBar.visibility = View.INVISIBLE
            }
        }
        return view
    }

//    override fun onResume() {
//        super.onResume()
//        //Take of Menu Bar
////        val mActivity : MainActivity = activity as MainActivity
////        //Set VISIBLE TextView SignOut
////        val tv_SignOut = mActivity.findViewById<TextView>(R.id.tv_SignOut)
////        tv_SignOut.visibility = View.VISIBLE
////        //Set VISIBLE TextView User Name
////        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
////        tv_Users.visibility = View.VISIBLE
//        //view.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
//        //If you press CallBack`s Button on the phone
//    }

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
}
