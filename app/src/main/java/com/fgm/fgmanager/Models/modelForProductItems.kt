package com.fgm.fgmanager.Models

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.fgm.fgmanager.Adapters.MyItemRecyclerViewAdapter
import com.fgm.fgmanager.DBHelpers.DBHelper
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class modelForProductItems(val ctx : Context?) { //(Rec : RecyclerView)

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertItemsList(RecView: RecyclerView, adapter: MyItemRecyclerViewAdapter){
        var countedAmountDay: String = "0"
        var numOfSorting: Int = 0

        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern(STORAGE.dateFormat)  //Format for Date
        val value = LocalDate.now().format(formatter) // Current Date
        val parseDateNow = LocalDate.parse(
            value,
            formatter
        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...

        // Add the List to the Database
        getRealTimeCollection()
            ?.subscribeOn(Schedulers.io()) // Background thread
            ?.subscribe({ data ->   //            ?.observeOn(AndroidSchedulers.mainThread())
                if(PlaceholderContent.ITEMS.size > 0) PlaceholderContent.ITEMS.clear() //CLear up ItemsList.
                var id : Int = 0
                Log.w("TAG", "Observe")
                data.forEach { t, u ->          //Add Items in Array for each
                    val parseDateItem =
                        LocalDate.parse("${u!!.get(STORAGE.ProductDate)}".toString(),formatter) //Date of Product
                    val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
                    if (numberOfDays.toInt() > 0) countedAmountDay = numberOfDays.toString() else countedAmountDay = "" // If amount Of days lower then Zero
                    if (numberOfDays.toInt() > 0) numOfSorting = numberOfDays.toInt() else numOfSorting = 0 // If amount Of days lower then Zero for num of Sorting
                    PlaceholderContent.ITEMS.add(
                        PlaceholderContent.PlaceholderItem(
                            "${u!!.get(STORAGE.ProductName)}",
                            "${u!!.get(STORAGE.ProductBarcode)}",
                            "${u!!.get(STORAGE.ProductDate)}",
                            countedAmountDay,//"${u!!.get("amountDays")}"
                            "${u!!.get(STORAGE.ProductKey)}",
                            numOfSorting,
                            id++
                        )
                    )
                }
                //RecView.adapter = adapter
                PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
                val list = PlaceholderContent.ITEMS.toMutableList() //That adapter think, it is new list
                adapter.values = list
            }, { exception -> Log.w("TAG", "Listen failed.", exception) })
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRealTimeCollection(): Observable<Map<String, MutableMap<String, String>>> {
        val dbFS = Firebase.firestore
        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line

        return Observable.create { emitter ->
            val docRef = dbFS.collection(resultNameOfCollection).document(STORAGE.docPathProductDB)
                .addSnapshotListener{ snapshot, e ->
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e)
                        emitter.onError(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val data = snapshot.data as Map<String, MutableMap<String, String>>
                        emitter.onNext(data)
                    } else {
                        Log.d("test", "Current data: null")

                    }
                }
            emitter.setCancellable { docRef.remove() }
        }
    }

//    @SuppressLint("NotifyDataSetChanged")
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun CreateFireStoreDB(Rec : RecyclerView) : Boolean { //RecView : RecyclerView
//        val dbFS = Firebase.firestore
//
//        var countedAmountDay: String = "0"
//        var numOfSorting: Int = 0
//
//        val formatter: DateTimeFormatter =
//            DateTimeFormatter.ofPattern(STORAGE.dateFormat)  //Format for Date
//        val value = LocalDate.now().format(formatter) // Current Date
//        val parseDateNow = LocalDate.parse(
//            value,
//            formatter
//        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
//
//        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
//        val docRef = dbFS.collection(resultNameOfCollection).document(STORAGE.docPathProductDB)
//        docRef.addSnapshotListener() { snapshot, e -> //MetadataChanges.INCLUDE
//            if (e != null) {
//                Log.w("TAG", "Listen failed.", e)
//                return@addSnapshotListener
//            }
//
//            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
//                "Local"
//            else
//                "Server"
//            var i = 0
//            if (snapshot != null && snapshot.exists()) {
//                val data = snapshot.data as Map<String, MutableMap<String, String>>
//                if(PlaceholderContent.ITEMS.size > 0) PlaceholderContent.ITEMS.clear() //CLear up ItemsList.
//                var id : Int = 0
//                data.forEach { t, u ->          //Add Items in Array for each
//                    Log.d("TAG", "${id++}")
//                    val parseDateItem =
//                        LocalDate.parse("${u!!.get(STORAGE.ProductDate)}".toString(),formatter) //Date of Product
//                    val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
//                    if (numberOfDays.toInt() > 0) countedAmountDay = numberOfDays.toString() else countedAmountDay = "" // If amount Of days lower then Zero
//                    if (numberOfDays.toInt() > 0) numOfSorting = numberOfDays.toInt() else numOfSorting = 0 // If amount Of days lower then Zero for num of Sorting
//                    PlaceholderContent.ITEMS.add(
//                        PlaceholderContent.PlaceholderItem(
//                            "${u!!.get(STORAGE.ProductName)}",
//                            "${u!!.get(STORAGE.ProductBarcode)}",
//                            "${u!!.get(STORAGE.ProductDate)}",
//                            countedAmountDay,//"${u!!.get("amountDays")}"
//                            "${u!!.get(STORAGE.ProductKey)}",
//                            numOfSorting,
//                            id++
//                        )
//                    )
//                }
//            } else {
//                Log.d("TAG", "$source data: null")
//            }
//            PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
//            Rec.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
//        }
//        return true
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun CreateLocalDataBase(RecView : RecyclerView) {
        var countedAmountDay: String = "0"
        var numOfSorting: Int = 0
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern(STORAGE.dateFormat)  //Format for Date
        val value = LocalDate.now().format(formatter) // Current Date
        val parseDateNow = LocalDate.parse(
            value,
            formatter
        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...

        // creating a DBHelper class
        // and passing context to it
        val db = DBHelper(ctx, null)

        // below is the variable for cursor
        // we have called method to get
        // all names from our database
        // and add to name text view
        val cursor = db.getProduct()
        // moving the cursor to first position and
        // appending value in the text view

        cursor!!.moveToFirst()
        var id : Int = 0
        if(cursor.count != 0) {
            //COUNTING amount of Days
            val parseDateItem =
                LocalDate.parse(
                    cursor?.getString(cursor.getColumnIndex(DBHelper.PRODUCT_DATE)),
                    formatter
                ) //Date of Product
            val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
                .toString() //Amount of days

            if (numberOfDays.toInt() > 0) { // If amount Of days lower then Zero
                countedAmountDay = numberOfDays
            } else {
                countedAmountDay = ""
            }
            if (numberOfDays.toInt() > 0) numOfSorting = numberOfDays.toInt() else numOfSorting = 0 // If amount Of days lower then Zero for num of Sorting
            PlaceholderContent.ITEMS.clear()
            PlaceholderContent.ITEMS.add(
                PlaceholderContent.PlaceholderItem(
                    cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_NAME)).toString(),
                    cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_BARCODE)).toString(),
                    cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_DATE)).toString(),
                    countedAmountDay, //cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_AMOUT_DAYS)).toString()
                    cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)).toString(),
                    numOfSorting,
                    id++
                )
            )

            // moving our cursor to next
            // position and appending values
            while (cursor.moveToNext()) {
                //COUNTING amount of Days
                val parseDateItem =
                    LocalDate.parse(cursor?.getString(cursor.getColumnIndex(DBHelper.PRODUCT_DATE)), formatter) //Date of Product
                val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
                    .toString() //Amount of days
                var countedAmountDay : String
                if (numberOfDays.toInt() > 0) { // If amount Of days lower then Zero
                    countedAmountDay = numberOfDays
                }
                else {
                    countedAmountDay = ""
                }
                if (numberOfDays.toInt() > 0) numOfSorting = numberOfDays.toInt() else numOfSorting = 0 // If amount Of days lower then Zero for num of Sorting
                PlaceholderContent.ITEMS.add(
                    PlaceholderContent.PlaceholderItem(
                        cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_NAME)).toString(),
                        cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_BARCODE))
                            .toString(),
                        cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_DATE)).toString(),
                        countedAmountDay,
                        cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL))
                            .toString(), //cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)).toString()
                        numOfSorting,
                        id++
                    )
                )
            }

            // at last we close our cursor
            cursor.close()
            PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
            RecView.adapter?.notifyItemInserted(PlaceholderContent.ITEMS.lastIndex)    // If change, Reload Recycler View
        }else{
            PlaceholderContent.ITEMS.clear()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun DialogToDelete(holder : MyItemRecyclerViewAdapter.ViewHolder, itemId : String, position : Int) {
        holder.im_Delete_Item.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)     //Set Dialog
            val dialog =
                builder.setTitle(R.string.Delete_Item).setMessage(R.string.Question_Delete_Item)
                    .setTitle(R.string.Delete_Item)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.Question_Delete_Item)
                    .setPositiveButton(R.string.Answer_Delete_Item_Yes) {       //When click "Yes"
                            dialog, id ->
                        PlaceholderContent.ITEMS.removeAt(position)
                        if(STORAGE.TypeAccFree) {
                            deleteFromFireStoreDB(itemId)
//                            Navigation.findNavController(holder.itemView)
//                                .navigate(R.id.action_itemFragment_self) // That refresh ItemFragment
                        }else{
                            val db = DBHelper(ctx,null)
                            db.deleteCourse(itemId.toInt())
                                                    Navigation.findNavController(holder.itemView)
                            .navigate(R.id.action_itemFragment_self) // That refresh ItemFragment
                        }
                        //action_itemFragment_self
//                        Navigation.findNavController(holder.itemView)
//                            .navigate(R.id.action_itemFragment_self) // That refresh ItemFragment
                    }
                    .setNegativeButton(R.string.Answer_Delete_Item_No, null)
                    .create()
            dialog.show()
        }
    }

    fun deleteFromFireStoreDB(ItemId : String){
        val dbFSDelete = Firebase.firestore
        //Add new Item in Firestore
        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSDelete.collection(resultNameOfCollection).document(STORAGE.docPathProductDB)
        // Remove the 'Item with number of keyProduct' field from the document
        val updates = hashMapOf<String, Any>(
            "${ItemId}" to FieldValue.delete()
        )
        docRef.update(updates).addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully update!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addItemsForFireStoreDB(item : PlaceholderContent.PlaceholderItem){
        val dbFSName = Firebase.firestore
        var currentDate = Calendar.getInstance() //For Unics name of Field
        //Add new
        item.keyProduct = currentDate.time.toString().trim()
//        PlaceholderContent.ITEMS.add(item) // Add to list
//        PlaceholderContent.ITEMS.sortBy { it.numberForSorting }
        val user = mapOf<String, PlaceholderContent.PlaceholderItem>(currentDate.time.toString().trim() to item) //Create Map for sending NameOfMapProd.toString()
        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        dbFSName.collection(resultNameOfCollection).document(STORAGE.docPathProductDB)   //Создаёт Новый Документ. Set Стирает данные документа и перезаписывает данные
            .set(user, SetOptions.merge()) // Без SetOptions.merge(), Set перезапишет данные
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addItemSQLdbProducts(productName : String, productBarcode : String, productDate : String, productAmountDays : String){
        val db = DBHelper(ctx, null)

        //PlaceholderContent.ITEMS.add(PlaceholderContent.PlaceholderItem(productName, productBarcode, productDate, productAmountDays))
        //PlaceholderContent.ITEMS.sortBy { it.numberForSorting }

        db.addProduct(productName, productBarcode, productDate, productAmountDays)
    }
}