package com.fgm.fgmanager.Fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.fgm.fgmanager.*
import com.fgm.fgmanager.Adapters.MyItemRecyclerViewAdapter
import com.fgm.fgmanager.DBHelpers.DBHelper
import com.fgm.fgmanager.DBHelpers.DBHelperLogIn
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent
import com.google.android.gms.ads.AdView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * A fragment representing a list of Items.
 */

class ItemFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)

        //val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        //val value = LocalDate.now().format(formatter) // Current Date
        //val parseDateNow = LocalDate.parse(value,formatter)      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
        //Log.d("TAG", value)

        val mActivity : MainActivity = activity as MainActivity
        val tv_SignOut = mActivity.findViewById<TextView>(R.id.tv_SignOut)
        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
        val linear = mActivity.findViewById<LinearLayout>(R.id.layoutMenu)
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
        val b_AddProduct = mActivity.findViewById<Button>(R.id.button_Add_Product)
        val ad_FirstBaneer = mActivity.findViewById<AdView>(R.id.adViewFirstBanner)

        //Set AdView Banner
        if(STORAGE.TypeAccFree){
            ad_FirstBaneer.visibility = View.GONE
        }else
            ad_FirstBaneer.visibility = View.VISIBLE

        //Set Visible Button Add New Product
        b_AddProduct.visibility = View.VISIBLE
        //SET Menu Bar
        linear.visibility = View.VISIBLE
        //Set VISIBLE TextView SignOut
        tv_SignOut.visibility = View.VISIBLE
        //Set VISIBLE TextView User Name
        tv_Users.visibility = View.VISIBLE
        //Set User Name inText View
        tv_Users.setText(STORAGE.UserName)


        b_AddProduct.setOnClickListener{
            if(!STORAGE.TypeAccFree) {
                var currentDate = Calendar.getInstance() // Receive current Date
                if(STORAGE.AdPressButton) { //Show Ad only each 4 hours (currentDate.time.hours % 4 == 0) OR If you Press Button First time until you close App
                    mActivity.showInterAd() //Show Add
                    STORAGE.AdPressButton = false
                }
            }
                Navigation.findNavController(view)
                .navigate(R.id.action_itemFragment_to_craeteDateFragment) // Go to Create Fragment
        }

        tv_SignOut.setOnClickListener{
            //LoginDataSource::logout
            if(STORAGE.TypeAccFree){
                DialogToQuit(view)     //From Online DataBase
            }else{
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_myLoginFragment) //Offline DataBase
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(      //If press CallBack BUtton on phone
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(STORAGE.TypeAccFree) {
                        DialogToQuit(view)
                    }else
                        Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_myLoginFragment)
                }
            }
        )
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                //Set Database for RecViewList
                if(STORAGE.TypeAccFree){
                    //CreateFireDB(view)        //Online DataBase
                    CreateFireStoreDB(view)     //FireStore DB
                }else{
                    CreateLocalDataBase(view)//Offline DataBase
                }
                adapter = MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS)
                //addNewItem(view)
                //adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
            }
        }
        return view
    }

//    override fun onResume() {
//        super.onResume()
//        //Take of Menu Bar
////        val mActivity : MainActivity = activity as MainActivity
////        //Set VISIBLE TextView SignOut
////        val tv_SignOut = mActivity.findViewById<TextView>(R.id.tv_SignOut)
////        tv_SignOut.visibility = View.VISIBLE
////        //Set VISIBLE TextView User Name
////        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
////        tv_Users.visibility = View.VISIBLE
//        //view.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
//        //If you press CallBack`s Button on the phone
//    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    fun DialogToQuit(view : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialog =  builder.setTitle("Выход").setMessage("Вы уверены, что хотите выйти из приложения")
            .setTitle("Выход")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage("Вы уверены, что хотите выйти из приложения?")
            .setPositiveButton("OK"){
                    dialog, id ->
                logOut()
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_myLoginFragment)
            }
            .setNegativeButton("Отмена", null)
            .create()
        dialog.show()
    }

    fun addNewItem(RecView : RecyclerView){
        val newItem =
            arguments?.getStringArrayList("NameProductForFirebase")   //Receive arrayString Title of new products from Create Date Fragment
        if (newItem != null) {
            val item = PlaceholderContent.PlaceholderItem(newItem[0].toString(), newItem[1].toString(), newItem[2]. toString(), newItem[3].toString(), newItem[4].toString())
            //addItems()
            //addItemsFS(item, (PlaceholderContent.ITEMS.size + 1))

            //myRef.push().setValue(item)
            //PlaceholderContent.ITEMS.add(item)
            //RecView.adapter?.notifyItemInserted(STORAGE.localDsArray.size)    // If change, Reload Recycler View
            newItem.clear()
            //PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
            //RecView.adapter?.notifyItemInserted(PlaceholderContent.ITEMS.lastIndex)
            RecView.adapter?.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun CreateFireDB(RecView : RecyclerView) {
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)
        val mActivity : MainActivity = activity as MainActivity
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        val value = LocalDate.now().format(formatter) // Current Date
        val parseDateNow = LocalDate.parse(
            value,
            formatter
        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
        //var iteration : Int = 0
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (PlaceholderContent.ITEMS.size > 0) PlaceholderContent.ITEMS.clear() //IF ArrayList doesn't empty
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
                        PlaceholderContent.ITEMS.add(item)//Set Item in Array
                        //Fill FireBase Store
                        //addItems(item.productName, item.productBarcode, item.productDate, item.amountDays, item.keyProduct, item.numberForSorting, iteration)
                        //iteration++
                        //Log.d("TAG", item.numberOfItems.toString())
                    }
                }
                PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
                RecView.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
                progressBar.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun logOut(){
        if(STORAGE.TypeAccFree) {
            val mActivity: MainActivity = activity as MainActivity
            val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
//            val str = tv_User
//            tv_Users.setText("username")
//        auth.signOut()
            val dbSaveLogin = DBHelperLogIn(requireContext(), null)
            dbSaveLogin.deleteCourse(STORAGE.UserName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun CreateLocalDataBase(RecView : RecyclerView) {
        var countedAmountDay: String = "0"
        var numOfSorting: Int = 0
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        val value = LocalDate.now().format(formatter) // Current Date
        val parseDateNow = LocalDate.parse(
            value,
            formatter
        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...

        // creating a DBHelper class
        // and passing context to it
        val db = DBHelper(requireContext(), null)

        // below is the variable for cursor
        // we have called method to get
        // all names from our database
        // and add to name text view
        val cursor = db.getProduct()
        // moving the cursor to first position and
        // appending value in the text view

        cursor!!.moveToFirst()

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
                    numOfSorting
                )
            )
//        Name.append(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)) + "\n")
//        Age.append(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)) + "\n")


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
            //Log.d("TAG","ItemFr Create {$cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)).toString()}")
                PlaceholderContent.ITEMS.add(
                    PlaceholderContent.PlaceholderItem(
                        cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_NAME)).toString(),
                        cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_BARCODE))
                            .toString(),
                        cursor.getString(cursor.getColumnIndex(DBHelper.PRODUCT_DATE)).toString(),
                        countedAmountDay,
                        cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL))
                            .toString(), //cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)).toString()
                        numOfSorting
                    )
                )
//            Name.append(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)) + "\n")
//            Age.append(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)) + "\n")
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
    fun CreateFireStoreDB(RecView : RecyclerView) {
        val dbFS = Firebase.firestore
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(STORAGE.FireBasePath)
        var countedAmountDay: String = "0"
        var numOfSorting: Int = 0
        val localITEMS: MutableList<PlaceholderContent.PlaceholderItem> = ArrayList()
        val mActivity : MainActivity = activity as MainActivity
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        val value = LocalDate.now().format(formatter) // Current Date
        val parseDateNow = LocalDate.parse(
            value,
            formatter
        )      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...

        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
        //resultNameOfCollection Is Name OF Main Collection
        val docRef = dbFS.collection(resultNameOfCollection).document("DataBase")
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
                //var products : MutableMap<String, Any>? = HashMap()//snapshot.data// = snapshot.get("0") as MutableList<PlaceholderContent.PlaceholderItem>
                val data = snapshot.data as Map<String, MutableMap<String, String>>
                //val item = data.get("0")!!.get("productName")
                //Log.d("TAG", "$source data: ${snapshot.data} = Products ${data.get("0")} - $item")
                if(PlaceholderContent.ITEMS.size > 0) PlaceholderContent.ITEMS.clear() //CLear up ItemsList.
//                val itemData = data["1"].toString()
//                Log.d("TAG", "ItemF Create FS ${(data.size)}++++ ${snapshot.data} =!= ${data} =/n ItemData + $itemData")


//                if (numberOfDays.toInt() > 0) { // If amount Of days lower then Zero
//                    countedAmountDay = numberOfDays
//                } else {
//                    countedAmountDay = ""
//                }

                data.forEach { t, u ->          //Add Items in Array for each
                    //Log.d("TAG", "ItemF Create FS ${data.get("productDate").toString()}")
                    val parseDateItem =LocalDate.parse("${u!!.get("productDate")}".toString(),formatter) //Date of Product
                    val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
                    if (numberOfDays.toInt() > 0) countedAmountDay = numberOfDays.toString() else countedAmountDay = "" // If amount Of days lower then Zero
                    if (numberOfDays.toInt() > 0) numOfSorting = numberOfDays.toInt() else numOfSorting = 0 // If amount Of days lower then Zero for num of Sorting
                            PlaceholderContent.ITEMS.add(
                            PlaceholderContent.PlaceholderItem(
                                "${u!!.get("productName")}",
                                "${u!!.get("productBarcode")}",
                                "${u!!.get("productDate")}",
                                countedAmountDay,//"${u!!.get("amountDays")}"
                                "${u!!.get("keyProduct")}",
                                numOfSorting
                            )
                        )
                }

//                for (i in 0 .. (data.size-1))
//                {
//                    if(data.get("$i") != null) {
//                        //Log.d("TAG", "ItemF Create FS${(data.size)}++++$i == ${data.get("0")!!.get("productName")}")
//                        PlaceholderContent.ITEMS.add(
//                            PlaceholderContent.PlaceholderItem(
//                                "${data.get("$i")!!.get("productName")}",
//                                "${data.get("$i")!!.get("productBarcode")}",
//                                "${data.get("$i")!!.get("productDate")}",
//                                "${data.get("$i")!!.get("amountDays")}",
//                                "${data.get("$i")!!.get("keyProduct")}",
//                                data.get("$i")!!.get("amountDays")!!.toInt()
//                            )
//                        )
//                        //Log.d("TAG", "${(data.size)}++++$i == ${data.get("1")!!.get("productName")}")
//                    }
//                }
            } else {
                Log.d("TAG", "$source data: null")
            }
//            for(i in 0 .. (localITEMS.size)){
//                PlaceholderContent.ITEMS.add(localITEMS[i])
//            }
            //Log.d("TAG", "${PlaceholderContent.ITEMS.size}")
            PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
            RecView.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
            //RecView.adapter?.notifyItemInserted(PlaceholderContent.ITEMS.lastIndex)

//            var prevSize : Int = PlaceholderContent.ITEMS.size
//            RecView.adapter?.notifyItemRangeChanged(prevSize, PlaceholderContent.ITEMS.size)
            progressBar.visibility = View.INVISIBLE
        }



//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (PlaceholderContent.ITEMS.size > 0) PlaceholderContent.ITEMS.clear() //IF ArrayList doesn't empty
//                for (ds: DataSnapshot in snapshot.children) //Get All children from FireBase
//                {
//                    val item =
//                        ds.getValue(PlaceholderContent.PlaceholderItem::class.java)  //Take ONE item
//                    if (item != null) {
//                        item.keyProduct = ds.key.toString()     //get KEY of CHILDREN
//                        //COUNTING amount of Days
//                        val parseDateItem =
//                            LocalDate.parse(item.productDate, formatter) //Date of Product
//                        val numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseDateItem)
//                            .toString() //Amount of days
//                        if (numberOfDays.toInt() > 0) { // If amount Of days lower then Zero
//                            item.amountDays = numberOfDays
//                            item.numberForSorting = numberOfDays.toInt()
//                        }
//                        else {
//                            item.amountDays = ""
//                            item.numberForSorting = 0
//                        }
//                        PlaceholderContent.ITEMS.add(item)//Set Item in Array
//                        //Log.d("TAG", item.numberOfItems.toString())
//                    }
//                }
//                PlaceholderContent.ITEMS.sortBy { it.numberForSorting } // Sorting of List
//                RecView.adapter?.notifyDataSetChanged()    // If change, Reload Recycler View
//                progressBar.visibility = View.INVISIBLE
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

//    fun addItems(productName: String, productBarcode: String, productDate: String, amountDays: String, keyProduct: String = "", numberForSorting : Int, NameOfMapProd : Int){
//        val dbFS = Firebase.firestore
//        //Add new Item in Firestore
//            var item : PlaceholderContent.PlaceholderItem = PlaceholderContent.PlaceholderItem(productName, productBarcode, productDate, amountDays, keyProduct, numberForSorting)  //Create User
//            val user = mapOf<String, PlaceholderContent.PlaceholderItem>(NameOfMapProd.toString() to item) //Create Map for sending
//            val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
//        dbFS.collection(resultNameOfCollection).document("DataBase")   //Создаёт Новый Документ. Set Стирает данные документа и перезаписывает данные
//                .set(user, SetOptions.merge()) // Без SetOptions.merge(), Set перезапишет данные
//                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
//                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
//    }
//    fun addItemsFS(item : PlaceholderContent.PlaceholderItem, NameOfMapProd : Int){
//        val dbFSAdd = Firebase.firestore
//        //Add new Item in Firestore
//        val user = mapOf<String, PlaceholderContent.PlaceholderItem>(NameOfMapProd.toString() to item) //Create Map for sending
//        val resultNameOfCollection = STORAGE.UserName.split("0")[0] //Delete Number of Users from end of the line
//    dbFSAdd.collection(resultNameOfCollection).document("DataBase")   //Создаёт Новый Документ. Set Стирает данные документа и перезаписывает данные
//            .set(user, SetOptions.merge()) // Без SetOptions.merge(), Set перезапишет данные
//            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
//            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
//    }

}