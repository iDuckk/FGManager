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

class modelMyLogin(val view: View, val activity: MainActivity) {

    fun freeLogin(){
        STORAGE.UserName = "Free account"
        STORAGE.TypeAccFree = false
        Navigation.findNavController(view).navigate(R.id.action_myLoginFragment_to_itemFragment)
    }

    fun isNotFirstLogIn(){
        val dbSaveLogin = DBHelperLogIn(view.context, null)
        val cursor = dbSaveLogin.getUser()
        cursor!!.moveToFirst()
        if(cursor.count != 0) {
            var userName = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_NAME)).toString()
            var password = cursor.getString(cursor.getColumnIndex(DBHelperLogIn.USER_PAS)).toString()
            loginCloudFSMap(userName, password)
        }
        cursor.close()
    }

    fun loginCloudFSMap(username: String, password: String) {
        val dbSaveLogin = DBHelperLogIn(view.context, null)
        val dbFSLogin = Firebase.firestore
        val mActivity : MainActivity = activity as MainActivity
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
        if (username != "" && password != "") {
            //username Is username for Log In what we receive from EditText
            //resultNameOfCollection Is Name OF Main Collection
            val resultNameOfCollection = username.split("0")[0] //Delete Number of Users from end of the line
            val docRef = dbFSLogin.collection(resultNameOfCollection).document("Users")
            docRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Document found in the offline cache
                        val document = task.result
                        if (document != null && document.exists()) {
                            val users: Map<String, String> =
                                document?.get(username) as Map<String, String>
                            if (users.get("User") == username && users.get("Password") == password) { //Authentication
                                dbSaveLogin.addUser(username, password)
                                progressBar.visibility = View.VISIBLE
                                STORAGE.TypeAccFree = true
                                STORAGE.UserName = username //We use the Value when Fill Menu -> UserName List
                                holdOnAllViewsOnFragments(false)
                                Navigation.findNavController(view)
                                    .navigate(R.id.action_myLoginFragment_to_itemFragment)
                            }
                            //Log.d("TAG", "Cached document data: ${users.get("Password")}")
                        }else{
                            Toast.makeText(view.context, "Неверный Логин или Пароль", Toast.LENGTH_SHORT).show()
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
}