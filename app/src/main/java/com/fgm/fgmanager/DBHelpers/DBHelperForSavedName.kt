package com.fgm.fgmanager.DBHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperForSavedName(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given

        // we are calling sqlite
        // method for executing our query
        val query =
            ("CREATE TABLE $TABLE_NAMES_PRODUCT($PRODUCT_NAME TEXT, $PRODUCT_BARCODE TEXT, $ID_COL INTEGER PRIMARY KEY)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES_PRODUCT)
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addProduct(productName : String, productBarcode : String ){

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair

        values.put(PRODUCT_NAME, productName)
        values.put(PRODUCT_BARCODE, productBarcode)


        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAMES_PRODUCT, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    fun getProduct(): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAMES_PRODUCT, null)

    }

    // below is the method for deleting our course.
    fun deleteCourse(courseID: Int) {

        // on below line we are creating
        // a variable to write our database.
        val db = this.writableDatabase

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_NAMES_PRODUCT, "id=?", arrayOf(courseID.toString()))
        db.close()
    }

    companion object{ //var productName: String = "", val productBarcode: String = "", val productDate: String = "", var amountDays: String = ""
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "FREE_ACCOUNT_FOR_NAME" //FREE_ACCOUNT_FOR_NAME

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAMES_PRODUCT = "productsName_table" //productsName_table

        // below is the variable for id column
        val ID_COL = "id"

        //****************************************

        val PRODUCT_NAME = "productName"
        val PRODUCT_BARCODE = "productBarcode"
    }
}