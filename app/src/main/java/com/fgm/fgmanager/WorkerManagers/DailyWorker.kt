package com.fgm.fgmanager.WorkerManagers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.fgm.fgmanager.*
import com.fgm.fgmanager.DBHelpers.DBHelper
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent
import com.fgm.fgmanager.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.O)
class DailyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        var strAmountOfDay : String = ""
        var NOTIFICATION_ID : Int = 601
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
            val value = LocalDate.now().format(formatter) // Current Date
            val parseDateNow = LocalDate.parse(
                value,
                formatter
            )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
            // creating a DBHelper class
            // and passing context to it
            val db = DBHelper(applicationContext, null)
            // below is the variable for cursor
            // we have called method to get
            // all names from our database
            // and add to name text view
            val cursor = db.getProduct()
            // moving the cursor to first position and
            // appending value in the text view

            cursor!!.moveToFirst()

            if(cursor.count != 0) {
                //COUNTING amount of Days
                val parseDateItem =
                    LocalDate.parse(
                        cursor?.getString(cursor.getColumnIndex(DBHelper.PRODUCT_DATE)),
                        formatter
                    ) //Date of Product
                val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
                    .toString() //Amount of days
                //val strAmountOfDay : String = "Осталось $numberOfDays"
                // If amount Of days lower then 30 but not null. Repeat it each Weak
                if (numberOfDays.toInt() != 0 && numberOfDays.toInt() < 30 && (numberOfDays.toInt() % 7) == 0) {
                    if(numberOfDays.toInt() > 0){
                        strAmountOfDay = "Осталось $numberOfDays"
                        NOTIFICATION_ID++
                    }else{
                        strAmountOfDay = "Срок истек"
                    }
                    // Create an explicit intent for an Activity in your app
                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                    val builder = NotificationCompat.Builder(applicationContext, STORAGE.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_fg_128)
                        .setContentTitle(cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_NAME)).toString())
                        .setContentText(strAmountOfDay)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setGroup(STORAGE.GROUP_KEY_WORK)
                            //.setGroupSummary(true)
                        .setAutoCancel(true)

                    val summaryNotification = NotificationCompat.Builder(applicationContext, STORAGE.CHANNEL_ID)
                        .setContentTitle("Content Title")
                        //set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setSmallIcon(R.drawable.ic_logo_fg_128)
                        //build summary info into InboxStyle template
                        //specify which group this notification belongs to
                        .setGroup(STORAGE.GROUP_KEY_WORK)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)


                    with(NotificationManagerCompat.from(applicationContext)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(NOTIFICATION_ID, builder.build())
                        notify(STORAGE.NOTIFICATION_ID_DAILYWORKER, summaryNotification.build())
                    }
                }

                // moving our cursor to next
                // position and appending values
                while (cursor.moveToNext()) {

                    //COUNTING amount of Days
                    val parseDateItem =
                        LocalDate.parse(cursor?.getString(cursor.getColumnIndex(DBHelper.PRODUCT_DATE)), formatter) //Date of Product
                    val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
                        .toString() //Amount of days
                    if (numberOfDays.toInt() != 0 && numberOfDays.toInt() < 30 && (numberOfDays.toInt() % 7) == 0) { // If amount Of days lower then 30 but not null
                        if(numberOfDays.toInt() > 0){
                            strAmountOfDay = "Осталось $numberOfDays"
                            NOTIFICATION_ID++
                        }else{
                            strAmountOfDay = "Срок истек"
                        }
                        // Create an explicit intent for an Activity in your app
                        val intent = Intent(applicationContext, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                        val builder = NotificationCompat.Builder(applicationContext, STORAGE.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_logo_fg_128)
                            .setContentTitle(cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_NAME)).toString())
                            .setContentText(strAmountOfDay)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setGroup(STORAGE.GROUP_KEY_WORK)
                            //.setGroupSummary(true)
                            .setAutoCancel(true)

                        val summaryNotification = NotificationCompat.Builder(applicationContext, STORAGE.CHANNEL_ID)
                            .setContentTitle("Content Title")
                            //set content text to support devices running API level < 24
                            .setContentText("Two new messages")
                            .setSmallIcon(R.drawable.ic_logo_fg_128)
                            //build summary info into InboxStyle template
                            //specify which group this notification belongs to
                            .setGroup(STORAGE.GROUP_KEY_WORK)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)

                        with(NotificationManagerCompat.from(applicationContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(NOTIFICATION_ID, builder.build())
                            notify(STORAGE.NOTIFICATION_ID_DAILYWORKER, summaryNotification.build())
                        }
                    }
                }

                // at last we close our cursor
                cursor.close()
            }else{
                Log.d("TAG","DailyWorker. DbHelper of Product is empty")
            }

        //***
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        // Set Execution around 24:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, STORAGE.HOUR)
        dueDate.set(Calendar.MINUTE, STORAGE.MIN)
        dueDate.set(Calendar.SECOND, STORAGE.SEC)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresDeviceIdle(true)
            .build()

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(STORAGE.ID_DAILYWORKER)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)
        return Result.success()
    }
}
