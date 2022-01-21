package com.fgm.fgmanager.Models

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.fgm.fgmanager.DBHelpers.DBHelperForSavedName
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.PoJo.SaveProductNameDB
import com.fgm.fgmanager.STORAGE
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class modelCreateFragment(val activity: MainActivity) {

    fun hideKeyboardFrom(view: View?) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    fun SetExistNameToEditTextNameOfProduct(name : String, arrayOfSaveProduct: ArrayList<SaveProductNameDB>) : String {
        var strName : String = ""

        arrayOfSaveProduct.forEach { row ->
            if (name == row.productBarcode) {
                return row.productName.toString()
            }else strName = ""
        }
        return strName
    }

    fun addNameProductSQLdb(name : String, barCode : String){
        val dbName = DBHelperForSavedName(activity, null)

        dbName.addProduct(name, barCode)

    }

    fun addItemSavedName(item : SaveProductNameDB){
        val dbFSSaveName = Firebase.firestore
        var currentDate = Calendar.getInstance() //For Unics name of Field
        //Add new Item in Firestore NameDataBase
        val user = mapOf<String, SaveProductNameDB>(currentDate.time.toString().trim() to item) //Create Map for sending
        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        dbFSSaveName.collection(resultNameOfCollection).document("NameDataBase")   //Создаёт Новый Документ. Set Стирает данные документа и перезаписывает данные
            .set(user, SetOptions.merge()) // Без SetOptions.merge(), Set перезапишет данные
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun CreateDBForSaveProduct() : ArrayList<SaveProductNameDB> {
        if(STORAGE.TypeAccFree) {
            return CreateFireStoreDBofSavedName() //Create FireStore DB
        }else{
            return CreateLocalDBForSavedNames() // It Free Account
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun CreateFireStoreDBofSavedName() : ArrayList<SaveProductNameDB> {
        val dbFSNameCreate = Firebase.firestore
        var arrayOfSaveProducts: ArrayList<SaveProductNameDB> = ArrayList()
        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        val docRef = dbFSNameCreate.collection(resultNameOfCollection).document("NameDataBase")
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
                if(arrayOfSaveProducts.size > 0) arrayOfSaveProducts.clear() //CLear up ItemsList.
                data.forEach { t, u ->      //Add Items in Array for each
                    arrayOfSaveProducts.add(
                        SaveProductNameDB(
                            "${u!!.get("productName")}",
                            "${u!!.get("productBarcode")}",
                        )
                    )
                }
            } else {
                Log.d("TAG", "$source data: null")
            }
        }
        return arrayOfSaveProducts
    }

    fun CreateLocalDBForSavedNames() : ArrayList<SaveProductNameDB>{
        var arrayOfSaveProducts: ArrayList<SaveProductNameDB> = ArrayList()
        val dbName = DBHelperForSavedName(activity, null)

        // below is the variable for cursor
        // we have called method to get
        // all names from our database
        // and add to name text view
        val cursor = dbName.getProduct()
        // moving the cursor to first position and
        // appending value in the text view
        cursor!!.moveToFirst()
        if(cursor.count != 0) {
            arrayOfSaveProducts.add(SaveProductNameDB(cursor.getString(cursor.getColumnIndex(
                DBHelperForSavedName.PRODUCT_NAME)).toString(), cursor.getString(cursor.getColumnIndex(
                DBHelperForSavedName.PRODUCT_BARCODE)).toString()))
            // moving our cursor to next
            // position and appending values
            while(cursor.moveToNext()){
                arrayOfSaveProducts.add(SaveProductNameDB(cursor.getString(cursor.getColumnIndex(
                    DBHelperForSavedName.PRODUCT_NAME)).toString(), cursor.getString(cursor.getColumnIndex(
                    DBHelperForSavedName.PRODUCT_BARCODE)).toString()))
            }

            // at last we close our cursor
        }
        cursor.close()
        return arrayOfSaveProducts
    }
}