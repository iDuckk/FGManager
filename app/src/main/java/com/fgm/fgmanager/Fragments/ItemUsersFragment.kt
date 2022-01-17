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
                CreateFireStoreDB(view)     //FireStore DB
//                val ITEMS: MutableList<User> = ArrayList()
//                ITEMS.add(User("Loh", "Pidor"))
                adapter = usersAdapter(STORAGE.ITEMS)
                //adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun CreateFireStoreDB(RecView: RecyclerView) {
        val dbFSUsers = Firebase.firestore
//        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)

        val resultNameOfCollection =
            STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSUsers.collection(resultNameOfCollection).document("Users")
        docRef.addSnapshotListener() { snapshot, e -> //MetadataChanges.INCLUDE
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.data as Map<String, MutableMap<String, String>>
                if (STORAGE.ITEMS.size > 0) STORAGE.ITEMS.clear() //CLear up ItemsList.
                data.forEach { t, u ->          //Add Items in Array for each
                    STORAGE.ITEMS.add(
                        User(
                            "${u!!.get("User")}",
                            "${u!!.get("Password")}",
                        )
                    )
                }

            } else {
                Log.d("TAG", "$source data: null")
            }
            //Last Item FOR BUTTON
            STORAGE.ITEMS.add(User("Button",""))

//            PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
            RecView.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
        }
    }
}