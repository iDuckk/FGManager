package com.fgm.fgmanager


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.fgm.fgmanager.WorkerManagers.DailyWorker
import com.fgm.fgmanager.WorkerManagers.UploadWorker

import com.fgm.fgmanager.placeholder.PlaceholderContent
import com.fgm.fgmanager.placeholder.PlaceholderContent.ITEMS
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

val database = FirebaseDatabase.getInstance()
val myRef = database.getReference(STORAGE.FireBasePath)
val NOTIFICATION_ID = 101
val CHANNEL_ID = "channelID"
val KEY_COUNT_VALUE = "key_count"

class MainActivity : AppCompatActivity() {
    //*************************************
    //  -Added DailyWorker - Worker request - Foreground
    // - Set Notification on 10 o`clock
    // - Added New Login fun with Fire Store
    // - Added New fun Create Data Base with Fire Store
    // - Added New fun AddItem with Fire Store
    // - Add New fun deleteItem
    // - Fix fun SetExistNameToEditTextNameOfProduct
    //*************************************

    //val CAMERA_RQ = 102
    //val itemsList : MutableList<PlaceholderContent.PlaceholderItem> = ArrayList()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        createNotificationChannel()//зарегистрировать канал уведомлений, Вызвать при запуске приложения

        setDailyWorker()
        //SetOneTimeWorkerRequest()
        //setPeriodicWorkRequest()

//        notificationDate("product : String")
//        mActivity.notificationDate(username) //notification
//        CreateFireDB()
        //deleteFireBaseItem()

        //var currentDate = Calendar.getInstance()
        //Log.d("TAG", "Activity ${currentDate.time}")
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)
        //onChangeListener(myRef)
        //checkForPermissions(android.Manifest.permission.CAMERA, "camera", CAMERA_RQ)


    }
    //**************************************************************************************************
    fun setDailyWorker(){
        var currentDate = Calendar.getInstance()
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
        WorkManager.getInstance(this).enqueue(dailyWorkRequest)
    }

    fun setPeriodicWorkRequest(){
        val periodicWorkRequest : WorkRequest = PeriodicWorkRequestBuilder<UploadWorker>(1, TimeUnit.DAYS)
            .build()
        val workManager = WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }

    fun SetOneTimeWorkerRequest(){ //Background Working
//        val data : Data = Data.Builder()
//            .putInt(KEY_COUNT_VALUE, 125)
//            .build()
        val constraints = Constraints.Builder()
        .build()
        //setInputData(data)
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>()
                .setConstraints(constraints)
                .build()
        val workManager = WorkManager.getInstance(this).enqueue(uploadWorkRequest)
    }

    fun notificationDate(product : String){
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_fg_128)
            .setContentTitle(getString(R.string.Notification))
            .setContentText(product)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "nameChanel"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //**************************************************************************************************
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun CreateFireDB() {
//        //val database = FirebaseDatabase.getInstance()
//        //val myRef = database.getReference(STORAGE.FireBasePath)
//        val formatter: DateTimeFormatter =
//            DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
//        val value = LocalDate.now().format(formatter) // Current Date
//        val parseDateNow = LocalDate.parse(
//            value,
//            formatter
//        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
//
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (ITEMS.size > 0) ITEMS.clear() //IF ArrayList doesn't empty
//                //First element
//                ITEMS.add(
//                    PlaceholderContent.PlaceholderItem(
//                        "check",
//                        "check",
//                        "check",
//                        "23/6/1987",
//                        "0",
//                        0
//                    )
//                )
//                for (ds: DataSnapshot in snapshot.children) //Get All children from FireBase
//                {
//                    val item =
//                        ds.getValue(PlaceholderContent.PlaceholderItem::class.java)  //Take ONE item
//                    if (item != null) {
//                        item.keyProduct = ds.key.toString()     //get KEY of CHILDREN
//                        //COUNTING amount of Days
//                        val parseDateItem =
//                            LocalDate.parse(item.productDate, formatter) //Date of Product
//                        val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
//                            .toString() //Amount of days
//                        if (numberOfDays.toInt() > 0) { // If amount Of days lower then Zero
//                            item.amountDays = numberOfDays
//                            item.numberForSorting = numberOfDays.toInt()
//                        }
//                        else {
//                            item.amountDays = ""
//                            item.numberForSorting = 0
//                        }
//                        ITEMS.add(item)//Set Item in Array
//                        //Log.d("TAG", item.numberOfItems.toString())
//                    }
//                }
//                ITEMS.sortBy { it.numberForSorting } // Sorting of List
//                //view.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

//**************************************************************************************************

    /*fun showDialog(permission: String, name: String, requestCode: Int){
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("OK"){
                    dialog, which ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()

        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
    }*/

    /*  fun checkForPermissions(permission: String, name: String, requestCode: Int){
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
              when{
                  ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED->{
                      Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
                  }
                  shouldShowRequestPermissionRationale(permission)-> ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)

                  else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
              }
          }
      }*/

}