package com.fgm.fgmanager.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fgm.fgmanager.Adapters.usersAdapter
import com.fgm.fgmanager.DBHelpers.DBHelperLogIn
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.Models.modelForProductItems
import com.fgm.fgmanager.Models.modelUsersList
import com.fgm.fgmanager.PoJo.User
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ItemUsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemUsersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ItemFragment.ARG_COLUMN_COUNT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_users_list, container, false)

        val modelUserList = modelUsersList(activity as MainActivity)
        val mActivity : MainActivity = activity as MainActivity
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)

//        val tv_usersListName = view.findViewById<TextView>(R.id.et_Users_UserName) //EDIT TEXT!!!
//        val tv_usersListPassword = view.findViewById<TextView>(R.id.et_Users_Password)

        requireActivity().onBackPressedDispatcher.addCallback(      //If press CallBack BUtton on phone
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                        Navigation.findNavController(view).navigate(R.id.action_itemUsersFragment_to_menuFragment)
                }
            }
        )

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                modelUserList.CreateFireStoreDB(view)     //FireStore DB
                adapter = usersAdapter(STORAGE.ITEMS)
                progressBar.visibility = View.INVISIBLE
            }
        }

        return view
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
}