package com.fgm.fgmanager.Models

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.PoJo.User
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class modelUsersList(val activity: MainActivity) {

    val mActivity : MainActivity = activity as MainActivity
    val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)

    @RequiresApi(Build.VERSION_CODES.N)
    fun CreateFireStoreDB(RecView: RecyclerView) {
        val dbFSUsers = Firebase.firestore
//        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)

        val resultNameOfCollection =
            STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSUsers.collection(resultNameOfCollection).document(STORAGE.docPathLogInDB)
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
                progressBar.visibility = View.VISIBLE
                val data = snapshot.data as Map<String, MutableMap<String, String>>
                if (STORAGE.ITEMS.size > 0) STORAGE.ITEMS.clear() //CLear up ItemsList.
                data.forEach { t, u ->          //Add Items in Array for each
                    STORAGE.ITEMS.add(
                        User(
                            "${u!!.get(STORAGE.collectionUser)}",
                            "${u!!.get(STORAGE.collectionPassword)}",
                        )
                    )
                }

            } else {
                Log.d("TAG", "$source data: null")
            }
            STORAGE.ITEMS.sortBy { it.User }
            //Last Item FOR BUTTON
            STORAGE.ITEMS.add(User(STORAGE.buttonString,""))
            progressBar.visibility = View.INVISIBLE
//            PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
            RecView.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
        }
    }
}