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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fgm.fgmanager.CHANNEL_ID
import com.fgm.fgmanager.DBHelpers.DBHelper
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.NOTIFICATION_ID
import com.fgm.fgmanager.R
import com.fgm.fgmanager.placeholder.PlaceholderContent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class DailyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        //***
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
                val strAmountOfDay : String = "Осталось $numberOfDays"
                // If amount Of days lower then 30 but not null. Repeat it each Weak
                if (numberOfDays.toInt() != 0 && numberOfDays.toInt() < 30 && (numberOfDays.toInt() % 7) == 0) {
                    // Create an explicit intent for an Activity in your app
                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                    val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_fg_128)
                        .setContentTitle(cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_NAME)).toString())
                        .setContentText(strAmountOfDay)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    with(NotificationManagerCompat.from(applicationContext)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(NOTIFICATION_ID, builder.build())
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
                    if (numberOfDays.toInt() != 0 && numberOfDays.toInt() < 30) { // If amount Of days lower then 30 but not null
                        // Create an explicit intent for an Activity in your app
                        val intent = Intent(applicationContext, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_logo_fg_128)
                            .setContentTitle(cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_NAME)).toString())
                            .setContentText(strAmountOfDay)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)

                        with(NotificationManagerCompat.from(applicationContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(NOTIFICATION_ID, builder.build())
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
        dueDate.set(Calendar.HOUR_OF_DAY, 10)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag("send_reminder_periodic")
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)
        return Result.success()
    }
}
