package com.fgm.fgmanager.Models

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.Navigation
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.fgm.fgmanager.DBHelpers.DBHelperLogIn
import com.fgm.fgmanager.Fragments.CraeteDateFragment
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.fgm.fgmanager.WorkerManagers.DailyWorker
import com.fgm.fgmanager.WorkerManagers.DailyWorkerFSdb
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class modelHelpers(val activity: MainActivity) {

    @RequiresApi(Build.VERSION_CODES.M)
    fun setDailyWorkerFSdb(){ //Background Working
//        val data : Data = Data.Builder()
//            .putInt(KEY_COUNT_VALUE, 125)
//            .build()
        var currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
// Set Execution around 24:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, STORAGE.HOUR)
        dueDate.set(Calendar.MINUTE, STORAGE.MIN)
        dueDate.set(Calendar.SECOND, STORAGE.SEC)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresDeviceIdle(true)
            .build()
//        //setInputData(data)

        val dailyWorkFSdbRequest = OneTimeWorkRequestBuilder<DailyWorkerFSdb>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            //.setConstraints(constraints)
            .addTag(STORAGE.ID_DAILYWORKERFS)
            .build() //                .setConstraints(constraints)
        WorkManager.getInstance(activity).enqueue(dailyWorkFSdbRequest)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setDailyWorker(){
        var currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
// Set Execution around 24:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, STORAGE.HOUR)
        dueDate.set(Calendar.MINUTE, STORAGE.MIN)
        dueDate.set(Calendar.SECOND, STORAGE.SEC)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresDeviceIdle(true)
            .build()

        val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            //.setConstraints(constraints)
            .addTag(STORAGE.ID_DAILYWORKER)
            .build()
        WorkManager.getInstance(activity).enqueue(dailyWorkRequest)
    }

    fun DialogToQuit(view : View, nameFragment : String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        val dialog =  builder.setTitle(R.string.Log_Out_Title).setMessage(R.string.Question_To_LogOut)
            .setTitle(R.string.Log_Out_Title)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(R.string.Question_To_LogOut)
            .setPositiveButton(R.string.Answer_Delete_Item_Yes){
                    dialog, id ->
                if(STORAGE.TypeAccFree) {
                    isOnlineUserLogOut()
                    val dbSaveLogin = DBHelperLogIn(view.context, null)
                    dbSaveLogin.deleteCourse(STORAGE.UserName)
                }
                if(nameFragment == STORAGE.itemFrag) //Go to myLogin from ItemFragment
                    Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_myLoginFragment)
                if(nameFragment == STORAGE.MenuFrag) //Go to myLogin from Menu Fragment
                    Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_myLoginFragment)
            }
            .setNegativeButton(R.string.Answer_Delete_Item_No, null)
            .create()
        dialog.show()
    }

        fun isOnlineUserLogOut(){
            sentIsOnlineUserLogOut()
                .subscribeOn(Schedulers.io())
                .subscribe ( {
                    Log.w("TAG", "logOut")
                },{
                    Log.w("TAG", "Error: not logOut")
                })
    }

    fun sentIsOnlineUserLogOut(): Completable {
        val dbFSAddIsOnline = Firebase.firestore

        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSAddIsOnline.collection(resultNameOfCollection).document(STORAGE.docPathLogInDB)

        val updates = hashMapOf<String, Any>( //Create new element for Fire Store
            STORAGE.collectionUser to STORAGE.UserName,
            STORAGE.collectionPassword to STORAGE.Password,
            STORAGE.collectionIsOnline to true
        )

        return Completable.create { emitter ->
            docRef
                .update(STORAGE.UserName, updates)   //Replace Element in FS
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }



//    fun isOnlineUserLogOut1(){
//        val dbFSAddIsOnline = Firebase.firestore
//
//        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
//        val docRef = dbFSAddIsOnline.collection(resultNameOfCollection).document(STORAGE.docPathLogInDB)
//
//        val updates = hashMapOf<String, Any>( //Create new element for Fire Store
//            STORAGE.collectionUser to STORAGE.UserName,
//            STORAGE.collectionPassword to STORAGE.Password,
//            STORAGE.collectionIsOnline to true
//        )
//
//        docRef
//            .update(STORAGE.UserName, updates)   //Replace Element in FS
//            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
//            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
//    }

}