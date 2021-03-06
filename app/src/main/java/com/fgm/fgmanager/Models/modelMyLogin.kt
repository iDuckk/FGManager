package com.fgm.fgmanager.Models

import android.util.Log
import android.view.View
import android.widget.*
import androidx.navigation.Navigation
import com.fgm.fgmanager.DBHelpers.DBHelperLogIn
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class modelMyLogin(val view: View, val activity: MainActivity) {

    fun freeLogin(){
        STORAGE.UserName = "Free account"
        STORAGE.TypeAccFree = false
        Navigation.findNavController(view).navigate(R.id.action_myLoginFragment_to_itemFragment)
    }
    fun isNotFirstLogIn() : Boolean{
        var userName : String = ""
        var password : String = ""
        val mActivity : MainActivity = activity as MainActivity
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
        val dbSaveLogin = DBHelperLogIn(view.context, null)
        val cursor = dbSaveLogin.getUser()
        cursor!!.moveToFirst()
        if(cursor.count != 0) {
            userName = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_NAME)).toString()
            password = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_PAS)).toString()

            STORAGE.isOnline = true
            val resultNameOfCollection = userName.split("0")[0] //Delete Number of Users from end of the line
            STORAGE.isAdmin = userName.drop(resultNameOfCollection.lastIndex + 1) // Take Last 3 Char (001, 002 ...)
            STORAGE.UserName = userName //We use the Value when Fill Menu -> UserName List
            STORAGE.Password = password //We use the Value when Fill LOG OUT. Cause do not get It from Sql Login. This way faster to write.
            STORAGE.TypeAccFree = true
            progressBar.visibility = View.VISIBLE

            isOnlineUserLogIn(userName, password, false) //If User Online, we cannot sign in from others gadgets
        }
        cursor.close()
        Log.d("TAG", userName)
        return userName != "" && password != ""
    }

//    fun isNotFirstLogIn(){
//        sentIsNotFirstLogIn()
//            .subscribeOn(Schedulers.io())
//            .subscribe ( {
//                Log.w("TAG", "logIn isOnline")
//            },{
//                Log.w("TAG", "Error: not logIn isOnline = $it")
//            })
//    }
//
//    fun sentIsNotFirstLogIn(): Completable {
//        val dbSaveLogin = DBHelperLogIn(view.context, null)
//        val cursor = dbSaveLogin.getUser()
//        var userName : String = ""
//        var password : String = ""
//        //STORAGE.isOnline = true
//        return Completable.create { emitter ->
//            cursor!!.moveToFirst()
//            if(cursor.count != 0) {
//                userName = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_NAME)).toString()
//                password = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_PAS)).toString()
//            }
//            cursor.close()
//            loginCloudFSMap(userName, password)
//        }
//    }

//    fun isNotFirst(){
//        val dbSaveLogin = DBHelperLogIn(view.context, null)
//        val cursor = dbSaveLogin.getUser()
//        var userName : String = ""
//        var password : String = ""
//        val mActivity : MainActivity = activity as MainActivity
//        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
//        cursor!!.moveToFirst()
//        if(cursor.count != 0) {
//            userName = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_NAME)).toString()
//            password = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_PAS)).toString()
//        }
//        cursor.close()
//
//        val resultNameOfCollection = userName.split("0")[0] //Delete Number of Users from end of the line
//        STORAGE.isAdmin = userName.drop(resultNameOfCollection.lastIndex + 1) // Take Last 3 Char (001, 002 ...)
//        STORAGE.UserName = userName //We use the Value when Fill Menu -> UserName List
//        STORAGE.Password = password //We use the Value when Fill LOG OUT. Cause do not get It from Sql Login. This way faster to write.
//        STORAGE.TypeAccFree = true
//        progressBar.visibility = View.VISIBLE
//
//        isOnlineUserLogIn(userName, password, false) //If User Online, we cannot sign in from others gadgets
//
//        Navigation.findNavController(view)
//            .navigate(R.id.action_myLoginFragment_to_itemFragment)
//    }

//    fun isNotFirstLogIn(){
//        val dbSaveLogin = DBHelperLogIn(view.context, null)
//        val cursor = dbSaveLogin.getUser()
//        STORAGE.isOnline = true
//        cursor!!.moveToFirst()
//        if(cursor.count != 0) {
//            var userName = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_NAME)).toString()
//            var password = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_PAS)).toString()
//            loginCloudFSMap(userName, password)
//        }
//        cursor.close()
//    }

    fun loginCloudFSMap(username: String, password: String) {
        val dbSaveLogin = DBHelperLogIn(view.context, null)
        val dbFSLogin = Firebase.firestore
        val mActivity : MainActivity = activity as MainActivity
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
        if (username != "" && password != "") {
            //username Is username for Log In what we receive from EditText
            //resultNameOfCollection Is Name OF Main Collection
            val resultNameOfCollection = username.split("0")[0] //Delete Number of Users from end of the line
            STORAGE.isAdmin = username.drop(resultNameOfCollection.lastIndex + 1) // Take Last 3 Char (001, 002 ...)
            val docRef = dbFSLogin.collection(resultNameOfCollection).document(STORAGE.docPathLogInDB)
            docRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Document found in the offline cache
                        val document = task.result
                        if (document != null && document.exists()) {
                            val users: Map<String, String> =
                                document?.get(username) as Map<String, String>
                            if (users.get(STORAGE.collectionUser) == username
                                && users.get(STORAGE.collectionPassword) == password
                                && (users.get(STORAGE.collectionIsOnline) as Boolean)) { //Authentication

                                dbSaveLogin.addUser(username, password)
                                progressBar.visibility = View.VISIBLE
                                STORAGE.TypeAccFree = true
                                STORAGE.UserName = username //We use the Value when Fill Menu -> UserName List
                                STORAGE.Password = password //We use the Value when Fill LOG OUT. Cause do not get It from Sql Login. This way faster to write.
                                holdOnAllViewsOnFragments(false)
                                isOnlineUserLogIn(username, password, false) //If User Online, we cannot sign in from others gadgets
                                Navigation.findNavController(view)
                                    .navigate(R.id.action_myLoginFragment_to_itemFragment)
                            }
                            //Log.d("TAG", "Cached document data: ${users.get("Password")}")
                        }else{
                            Toast.makeText(view.context, R.string.UncorrectNameOrPassword, Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Log.d("TAG", "Cached get failed: ", task.exception)
                    }
                }
        }
    }

    fun holdOnAllViewsOnFragments(a : Boolean){
        val et_Username = view.findViewById<EditText>(R.id.username)
        val et_Password = view.findViewById<EditText>(R.id.password)
        val b_Login = view.findViewById<Button>(R.id.login)
        val b_FreeLogin = view.findViewById<Button>(R.id.b_FreeLogin)
        val tv_Registration = view.findViewById<TextView>(R.id.tv_Registration)

        et_Username.setEnabled(a)
        et_Password.setEnabled(a)
        b_Login.setEnabled(a)
        b_FreeLogin.setEnabled(a)
        tv_Registration.setEnabled(a)

    }

//    fun isOnlineUser(userName : String, password : String, isOnline : Boolean){
//        val dbFSAddIsOnline = Firebase.firestore
//
//        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
//        val docRef = dbFSAddIsOnline.collection(resultNameOfCollection).document(STORAGE.docPathLogInDB)
//
//        val updates = hashMapOf<String, Any>( //Create new element for Fire Store
//            STORAGE.collectionUser to userName,
//            STORAGE.collectionPassword to password,
//            STORAGE.collectionIsOnline to isOnline
//        )
//
//        docRef
//            .update(userName, updates)   //Replace Element in FS
//            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
//            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
//    }

    fun isOnlineUserLogIn(userName : String, password : String, isOnline : Boolean){
    sentIsOnlineUserLogIn(userName, password, isOnline)
            .subscribeOn(Schedulers.io())
            .subscribe ( {
                Log.w("TAG", "logIn isOnline")
            },{
                Log.w("TAG", "Error: not logIn isOnline")
            })
    }

    fun sentIsOnlineUserLogIn(userName : String, password : String, isOnline : Boolean): Completable {
        val dbFSAddIsOnline = Firebase.firestore

        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSAddIsOnline.collection(resultNameOfCollection).document(STORAGE.docPathLogInDB)

        val updates = hashMapOf<String, Any>( //Create new element for Fire Store
            STORAGE.collectionUser to userName,
            STORAGE.collectionPassword to password,
            STORAGE.collectionIsOnline to isOnline
        )

        return Completable.create { emitter ->
            docRef
                .update(userName, updates)   //Replace Element in FS
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }
}