package com.fgm.fgmanager.DBHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperLogIn(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given

        // we are calling sqlite
        // method for executing our query
        val query =
            ("CREATE TABLE $TABLE_NAME($USER_NAME TEXT, $USER_PAS TEXT, $ID_COL INTEGER PRIMARY KEY)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addUser(productName : String, productBarcode : String ){

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair

        values.put(USER_NAME, productName)
        values.put(USER_PAS, productBarcode)


        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    fun getUser(): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

    }

    // below is the method for deleting our course.
    fun deleteCourse(courseID: String) {

        // on below line we are creating
        // a variable to write our database.
        val db = this.writableDatabase

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_NAME, "$USER_NAME=?", arrayOf(courseID.toString()))
        db.close()
    }

    companion object{ //var productName: String = "", val productBarcode: String = "", val productDate: String = "", var amountDays: String = ""
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "LOG_IN"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "user_values" //productsName_table

        // below is the variable for id column
        val ID_COL = "id"

        //****************************************

        val USER_NAME = "productName"
        val USER_PAS = "productBarcode"
    }
}