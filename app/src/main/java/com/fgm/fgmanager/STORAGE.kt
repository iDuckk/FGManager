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

        const val FireBasePath = "Product"
    }
}