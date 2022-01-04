package com.fgm.fgmanager.WorkerManagers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.fgm.fgmanager.*
import com.fgm.fgmanager.DBHelpers.DBHelper
import com.fgm.fgmanager.R
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

class DailyWorkerFSdb(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        var NOTIFICATION_ID : Int = 101
        val dbFSnot = Firebase.firestore
        var strAmountOfDay: String = ""
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        val value = LocalDate.now().format(formatter) // Current Date
        val parseDateNow = LocalDate.parse(
            value,
            formatter
        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
        val resultNameOfCollection =
            STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        //resultNameOfCollection Is Name OF Main Collection
        val docRef = dbFSnot.collection(resultNameOfCollection).document("DataBase")
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
                if (PlaceholderContent.ITEMS.size > 0) PlaceholderContent.ITEMS.clear() //CLear up ItemsList.

                data.forEach { t, u ->          //Add Items in Array for each
                    //Log.d("TAG", "Notify Create FS ${u!!.get("productName").toString()}")
                    val parseDateItem = LocalDate.parse(
                        "${u!!.get("productDate")}".toString(),
                        formatter
                    ) //Date of Product
                    val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)

                    if (numberOfDays.toInt() != 0 && numberOfDays.toInt() < 30 && (numberOfDays.toInt() % 7) == 0) {                    // Create an explicit intent for an Activity in your app  && (numberOfDays.toInt() % 7) == 0
                        if(numberOfDays.toInt() > 0){
                            strAmountOfDay = "Осталось $numberOfDays"
                            NOTIFICATION_ID++ //Create new ID for a new notification
                        }else{
                            strAmountOfDay = "Срок истек"
                        }
                        val intent = Intent(applicationContext, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        val builder = NotificationCompat.Builder(applicationContext, STORAGE.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_logo_fg_128)
                            .setContentTitle("${u!!.get("productName")}".toString())
                            .setContentText(strAmountOfDay)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setGroup(STORAGE.GROUP_KEY_WORK_FS)
//                            .setGroupSummary(true)
                            .setAutoCancel(true)

                        val summaryNotification = NotificationCompat.Builder(applicationContext, STORAGE.CHANNEL_ID)
                            .setContentTitle("Content Title")
                            //set content text to support devices running API level < 24
                            .setContentText("Two new messages")
                            .setSmallIcon(R.drawable.ic_logo_fg_128)
                            //build summary info into InboxStyle template
                            //specify which group this notification belongs to
                            .setGroup(STORAGE.GROUP_KEY_WORK_FS)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)
//                            .build()


                        with(NotificationManagerCompat.from(applicationContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(NOTIFICATION_ID, builder.build())
                            notify(STORAGE.NOTIFICATION_ID_DAILYWORKER_FS, summaryNotification.build())
                        }
                    }

                }
            } else {
                Log.d("TAG", "$source data: null")
            }
        }

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

        val dailyWorkFSdbRequest = OneTimeWorkRequestBuilder<DailyWorkerFSdb>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(STORAGE.ID_DAILYWORKERFS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(dailyWorkFSdbRequest)
        return Result.success()
    }
}