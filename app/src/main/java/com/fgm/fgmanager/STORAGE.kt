package com.fgm.fgmanager

import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.fgm.fgmanager.placeholder.PlaceholderContent
import java.util.ArrayList

class STORAGE {
    companion object{
        var NameEdit = ""
        var BarcodeEdit = ""
        var Date = ""

        //CreateDateParameters
        var booleanForRestoreCreateDateParameters : Boolean = false
        var booleanForCheckExistsOfNameOfProducts = false

        const val FireBasePath = "Product"
        const val FireBasePathForSaveProduct = "ProductDB"

        //DataBaseOfItemFragment When Log in
       var TypeAccFree : Boolean = false

        //SQL DataBase
        val TABLE_PRODUCTS = "products_table"
        val TABLE_NAME_PRODUCTS = "productsName_table"

    }
}