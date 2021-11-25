package com.fgm.fgmanager.placeholder

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.fgm.fgmanager.STORAGE
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.ArrayList

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<PlaceholderItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    //val ITEM_MAP: MutableMap<String, PlaceholderItem> = HashMap()

    //private val COUNT = 25
    //private var COUNT = addFireBaseList().size - 1

    init {
        // Add some sample items.
        /*for (i in 0..COUNT) {
            addItem(createPlaceholderItem(i))
        }*/

    }

    /*private fun addItem(item: PlaceholderItem) {
        ITEMS.add(item)
        //ITEM_MAP.put(item.id, item)
    }*/

   /* private fun createPlaceholderItem(position: Int): PlaceholderItem {
        //return PlaceholderItem(position.toString(), "Item " + position, makeDetails(position))
        return addFireBaseList()[position]
    }*/

    /*fun addFireBaseList() : MutableList<PlaceholderItem>{
        val fireBaseList : MutableList<PlaceholderItem> = ArrayList()
        return fireBaseList
    }*/

    /*private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }*/

    /**
     * A placeholder item representing a piece of content.
     */
    data class PlaceholderItem(var productName: String = "", val productBarcode: String = "", val productDate: String = "", var amountDays: String = "", var keyProduct: String = "", var numberForSorting : Int = 0) { //val id: String, val content: String, val details: String
        //override fun toString(): String = content
    }
}