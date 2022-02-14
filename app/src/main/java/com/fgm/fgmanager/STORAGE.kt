package com.fgm.fgmanager

import com.fgm.fgmanager.PoJo.User
import com.google.android.gms.ads.interstitial.InterstitialAd
import java.util.ArrayList

class STORAGE {
    companion object{
        var NameEdit = ""
        var BarcodeEdit = ""
        var Date = ""

        //CreateDateParameters
        var booleanForRestoreCreateDateParameters : Boolean = false
        var booleanForCheckExistsOfNameOfProducts = true

        const val FireBasePath = "Product"
        const val FireBasePathForSaveProduct = "ProductDB"

        //DataBase FireStore
        val docPathSavedNameDB = "NameDataBase"
        val docPathProductDB = "DataBase"
        val ProductName = "productName"
        val ProductDate = "productDate"
        val ProductKey = "keyProduct"
        val ProductBarcode = "productBarcode"
        val dateFormat = "d/M/yyyy"

        //DataBaseOfItemFragment When Log in
       var TypeAccFree : Boolean = false

        //SQL DataBase
        val TABLE_PRODUCTS = "products_table"
        val TABLE_NAME_PRODUCTS = "productsName_table"

        //Store Data Base LogIn
        var docPathLogInDB = "Users"
        var collectionUser = "User"
        var collectionPassword = "Password"
        var collectionIsOnline = "isOnline"
        var UserName = ""
        var Password = ""
        var isAdmin = ""
        val Admin = "001"
        var isOnline : Boolean = false //For Rejoin to account. When you do not LogOut

        //DailyWorkers
        val HOUR : Int = 10
        val MIN : Int = 0
        val SEC : Int = 0

        //Notification
        val NOTIFICATION_ID = 101
        val CHANNEL_ID = "channelID"

        //dailyWorkerFS
        val ID_DAILYWORKERFS = "send_reminder_periodic_FS"
        val GROUP_KEY_WORK_FS = "Group_of_Daily_WorkerFS"
        val NOTIFICATION_ID_DAILYWORKER_FS = 10
        var appWorkingCheckFS : Boolean = true

        //dailyWorker
        val ID_DAILYWORKER = "send_reminder_periodic"
        val GROUP_KEY_WORK = "Group of Daily Worker"
        val NOTIFICATION_ID_DAILYWORKER = 11

        //Ad
        var AdPressButton : Boolean = true
        var interAd : InterstitialAd? = null

        //ListUsers in Menu
        val ITEMS: MutableList<User> = ArrayList()
        var OldPassword : String = ""
        var userNameForChangePass : String = ""
        val buttonString = "Button"

        //Quit Fun From model Helpers
        val itemFrag = "ItemFragment"
        val MenuFrag = "MenuFragment"

        //Camera
        val CAMERA_RQ = 102

        //Theme
        var typeTheme = 0
    }
}