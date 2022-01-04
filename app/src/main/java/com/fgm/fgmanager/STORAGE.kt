package com.fgm.fgmanager

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

        //DataBaseOfItemFragment When Log in
       var TypeAccFree : Boolean = false

        //SQL DataBase
        val TABLE_PRODUCTS = "products_table"
        val TABLE_NAME_PRODUCTS = "productsName_table"

        //Store Data Base
        var UserName = ""

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

        //dailyWorker
        val ID_DAILYWORKER = "send_reminder_periodic"
        val GROUP_KEY_WORK = "Group of Daily Worker"
        val NOTIFICATION_ID_DAILYWORKER = 11

    }
}