package com.fgm.fgmanager.Models

import android.content.Context
import android.util.Log
import com.fgm.fgmanager.DBHelpers.DBHelperLogIn
import com.fgm.fgmanager.STORAGE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class modelUpPassword(val ctx : Context?) {

    fun UpdateUserPasswordFireStoreDB(userName : String, newPass : String){
        val dbFSUpdatePass = Firebase.firestore

        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSUpdatePass.collection(resultNameOfCollection).document(STORAGE.docPathLogInDB)

        val updates = hashMapOf<String, Any>( //Create new element for Fire Store
            STORAGE.collectionPassword to newPass.toString(),
            STORAGE.collectionUser to userName
        )

        docRef
            .update(userName, updates)   //Replace Element in FS
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }

        isAdminPass(STORAGE.UserName.toString(), newPass.toString()) // If we change our own Password.
    }

    fun isAdminPass(name : String, Pass : String){
        //Take Name User for ChangePass. When we login
        var userName : String = ""
        val dbNewPass = DBHelperLogIn(ctx!!, null)

        //Take Name User
        val cursor = dbNewPass.getUser()
        cursor!!.moveToFirst()
        if(cursor.count != 0) {
            userName = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_NAME)).toString()
        }
        cursor.close()

        if(userName == STORAGE.UserName) {
            dbNewPass.deleteCourse(STORAGE.UserName)
            dbNewPass.addUser(name, Pass)
        }
    }
}