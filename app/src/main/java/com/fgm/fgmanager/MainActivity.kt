package com.fgm.fgmanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.fgm.fgmanager.placeholder.PlaceholderContent
import com.fgm.fgmanager.placeholder.PlaceholderContent.ITEMS
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val database = FirebaseDatabase.getInstance()
val myRef = database.getReference(STORAGE.FireBasePath)

class MainActivity : AppCompatActivity() {

    //val CAMERA_RQ = 102
    //val itemsList : MutableList<PlaceholderContent.PlaceholderItem> = ArrayList()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        CreateFireDB()
        //deleteFireBaseItem()
        //Log.d(TAG, "Activity back pressed invoked")
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)
        //onChangeListener(myRef)
        //checkForPermissions(android.Manifest.permission.CAMERA, "camera", CAMERA_RQ)


    }


    //**************************************************************************************************
    @RequiresApi(Build.VERSION_CODES.O)
    fun CreateFireDB() {
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        val value = LocalDate.now().format(formatter) // Current Date
        val parseDateNow = LocalDate.parse(
            value,
            formatter
        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (ITEMS.size > 0) ITEMS.clear() //IF ArrayList doesn't empty
                //First element
                ITEMS.add(
                    PlaceholderContent.PlaceholderItem(
                        "check",
                        "check",
                        "check",
                        "23/6/1987",
                        "0",
                        0
                    )
                )
                for (ds: DataSnapshot in snapshot.children) //Get All children from FireBase
                {
                    val item =
                        ds.getValue(PlaceholderContent.PlaceholderItem::class.java)  //Take ONE item
                    if (item != null) {
                        item.keyProduct = ds.key.toString()     //get KEY of CHILDREN
                        //COUNTING amount of Days
                        val parseDateItem =
                            LocalDate.parse(item.productDate, formatter) //Date of Product
                        val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
                            .toString() //Amount of days
                        if (numberOfDays.toInt() > 0) { // If amount Of days lower then Zero
                            item.amountDays = numberOfDays
                            item.numberForSorting = numberOfDays.toInt()
                        }
                        else {
                            item.amountDays = ""
                            item.numberForSorting = 0
                        }
                        ITEMS.add(item)//Set Item in Array
                        //Log.d("TAG", item.numberOfItems.toString())
                    }
                }
                ITEMS.sortBy { it.numberForSorting } // Sorting of List
                //view.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

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