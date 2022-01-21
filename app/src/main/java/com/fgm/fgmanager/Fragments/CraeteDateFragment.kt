package com.fgm.fgmanager.Fragments

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.fgm.fgmanager.DBHelpers.DBHelper
import com.fgm.fgmanager.DBHelpers.DBHelperForSavedName
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.Models.modelAdvertisment
import com.fgm.fgmanager.Models.modelCreateFragment
import com.fgm.fgmanager.Models.modelForProductItems
import com.fgm.fgmanager.Models.modelMyLogin
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.fgm.fgmanager.PoJo.SaveProductNameDB
import com.fgm.fgmanager.PoJo.placeholder.PlaceholderContent
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CraeteDateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CraeteDateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val CAMERA_RQ = 102

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference(STORAGE.FireBasePathForSaveProduct)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_craete_date, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()
        val model = modelForProductItems(context)
        val modelCreateFrag = modelCreateFragment(activity as MainActivity)


        val arrayOfSaveprod = modelCreateFrag.CreateDBForSaveProduct() //Receive database ofName Of products for fill TextEdit Name

        val et_NameProduct = view.findViewById<EditText>(R.id.et_Name)
        val et_Barcode = view.findViewById<EditText>(R.id.et_Barcode)
        val tv_Date = view.findViewById<TextView>(R.id.tv_Date)
        val b_Ok = view.findViewById<Button>(R.id.b_Ok)
        val b_Cancel = view.findViewById<Button>(R.id.b_Cancel)
        val image_Calendar = view.findViewById<ImageView>(R.id.im_Calendar)
        val image_Bacode = view.findViewById<ImageView>(R.id.im_scan_Barcode)
        val tv_AmountDate = view.findViewById<TextView>(R.id.tv_amount_days)
        val mActivity : MainActivity = activity as MainActivity
        val b_AddProduct = mActivity.findViewById<Button>(R.id.button_Add_Product)
        val ad_FirstBaneer = mActivity.findViewById<AdView>(R.id.adViewFirstBanner)

        val modelAdv = modelAdvertisment(activity as MainActivity)

        //Set AdView Banner
        if(STORAGE.TypeAccFree){
            ad_FirstBaneer.visibility = View.GONE
        }else {
            ad_FirstBaneer.visibility = View.VISIBLE
        }
        //Set Visible Button Add New Product
        b_AddProduct.visibility = View.GONE
        //Take off TextView SignOut
        val tv_SignOut = mActivity.findViewById<TextView>(R.id.tv_SignOut)
        tv_SignOut.visibility = View.GONE
        //Take off TextView User Name
        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
        tv_Users.visibility = View.GONE

        if (STORAGE.booleanForRestoreCreateDateParameters) {    //When we return to Fragment from other Fragment, we Restore Parameters
            et_NameProduct.setText(STORAGE.NameEdit)
            et_Barcode.setText(STORAGE.BarcodeEdit)
            tv_Date.setText(STORAGE.Date)
            STORAGE.booleanForRestoreCreateDateParameters = false
        }

        val BarcodeResult =
            arguments?.getString("MyArg")   //Receive String Barcode from Barcode Fragment
        if (BarcodeResult != null) {
            et_Barcode.setText(BarcodeResult)
            //Set Name Product from ProductNameFDB IF we receive Barcode From CAMERA
            if(modelCreateFrag.SetExistNameToEditTextNameOfProduct(BarcodeResult, arrayOfSaveprod) != ""){
                //Log.d("TAG", SetExistNameToEditTextNameOfProduct(BarcodeResult, arrayOfSaveprod))   //Receive Name
                et_NameProduct.setText(modelCreateFrag.SetExistNameToEditTextNameOfProduct(BarcodeResult, arrayOfSaveprod)) //Set Text
            }
        }else{
            //Set Name Product from ProductNameFDB we receive Barcode From TYPING
            et_Barcode.setOnClickListener(){
                if(modelCreateFrag.SetExistNameToEditTextNameOfProduct(et_Barcode.text.toString(), arrayOfSaveprod) != ""){
                    et_NameProduct.setText(modelCreateFrag.SetExistNameToEditTextNameOfProduct(et_Barcode.text.toString(), arrayOfSaveprod))    //Set Text
                }
            }
        }

        val SelectDate = arguments?.getString("Date")   //Receive String Date from Calendar Fragment
        if (SelectDate != null) {
            if(SelectDate != null)
            STORAGE.Date = SelectDate       //This is need If we Clicked "Cancel" in Calendar Fragment. It is the last value
            tv_Date.setText(SelectDate)
        }

        val amountDays = arguments?.getString("NumberOfDays")   //Receive String Amount Of Date from Calendar Fragment
        /*if (amountDays != null) {
            et_Barcode.setText(BarcodeResult)
        }*/

        b_Ok.setOnClickListener {
            //Advertisement
            if(!STORAGE.TypeAccFree) {
                var currentDate = Calendar.getInstance() // Receive current Date
                if(STORAGE.AdPressButton) { //Show Ad only each 4 hours (currentDate.time.hours % 4 == 0) OR If you Press Button First time until you close App
                    modelAdv.showInterAd() //Show Add
                    STORAGE.AdPressButton = false
                }
            }

            //Add Arguments in FireBase IF arguments do not NULL
            if(et_NameProduct.text.toString() != "" && et_Barcode.text.toString() != ""&& tv_Date.text.toString() != ""
                && amountDays.toString() != "" && amountDays!!.toInt() > 0) {
            //Add new Name of Product
                if(STORAGE.TypeAccFree) {
                    arrayOfSaveprod.forEach { row -> if (et_Barcode.text.toString() != row.productBarcode) {
                        STORAGE.booleanForCheckExistsOfNameOfProducts = true //If new name doesn't exist
                        }
                        //Log.d("TAG", "arrayOfSaveprod = ${row.productBarcode}")
                    }

                    if (STORAGE.booleanForCheckExistsOfNameOfProducts) { //add new Name product to ProductDB
                        modelCreateFrag.addItemSavedName(
                            SaveProductNameDB(
                                et_NameProduct.text.toString(),
                                et_Barcode.text.toString())
                        )
                        STORAGE.booleanForCheckExistsOfNameOfProducts = false
                    }

                    model.addItemsForFireStoreDB(
                        PlaceholderContent.PlaceholderItem(
                        et_NameProduct.text.toString(),
                        et_Barcode.text.toString(),
                        tv_Date.text.toString(),
                        amountDays,
                        "0"))
                }else{
                    //LOCAL DATA BASE if Free account
                        if(arrayOfSaveprod.count() == 0){
                            modelCreateFrag.addNameProductSQLdb(et_NameProduct.text.toString(), et_Barcode.text.toString()) // First element we have to Add/ Because arrayOfSaveprod.lastIndex Always Null and we Cannot call addNameProductSQLdb()
                        }
                    for (i in 0..arrayOfSaveprod.lastIndex) // arrayOfSaveprod.lastIndex - amount of SaveProductNameDB
                        if (et_Barcode.text.toString() != arrayOfSaveprod[i].productBarcode) {
                            STORAGE.booleanForCheckExistsOfNameOfProducts =
                                true //If new name doesn't exist
                        }
                    if (STORAGE.booleanForCheckExistsOfNameOfProducts) { //add new Name product to ProductDB
                        modelCreateFrag.addNameProductSQLdb(et_NameProduct.text.toString(), et_Barcode.text.toString())
                        STORAGE.booleanForCheckExistsOfNameOfProducts = false
                    }

                    //Add new SQL Item
                    model.addItemSQLdbProducts(et_NameProduct.text.toString(), et_Barcode.text.toString(), tv_Date.text.toString(), amountDays)
                }

                Navigation.findNavController(view).navigate(R.id.action_craeteDateFragment_to_itemFragment, bundle)
            }else{
                Toast.makeText(requireContext(), "Please fill all gaps or maybe data is not correct", Toast.LENGTH_SHORT).show()
            }
        }

        b_Cancel.setOnClickListener {
            //Clean Storage when we left this fragment
            STORAGE.NameEdit = ""
            STORAGE.BarcodeEdit = ""
            STORAGE.Date = ""
            STORAGE.booleanForRestoreCreateDateParameters = false
            Navigation.findNavController(view).navigate(R.id.action_craeteDateFragment_to_itemFragment)
        }

        image_Calendar.setOnClickListener {
            modelCreateFrag.hideKeyboardFrom(view)
            STORAGE.NameEdit = et_NameProduct.text.toString() // Save Name For return to Fragment
            STORAGE.BarcodeEdit = et_Barcode.text.toString()    // Save Barcode For return to Fragment
            STORAGE.booleanForRestoreCreateDateParameters = true
            Navigation.findNavController(view).navigate(R.id.action_craeteDateFragment_to_calendarFragment)
        }

        image_Bacode.setOnClickListener {
            STORAGE.NameEdit = et_NameProduct.text.toString() // Save Name For return to Fragment
            //STORAGE.BarcodeEdit = et_Barcode.text.toString()    // Save Barcode For return to Fragment
            STORAGE.Date = tv_Date.text.toString()
            STORAGE.booleanForRestoreCreateDateParameters = true
            CamPermission(view)     //Take permission to Camera
        }

        //intent.extras?.getstring("tvBarcode")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CraeteDateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CraeteDateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun CamPermission(view : View){
        when{
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED->{
                Navigation.findNavController(view).navigate(R.id.action_craeteDateFragment_to_scanBarcodeFragment)
                //Toast.makeText(requireContext(), "$name permission granted", Toast.LENGTH_SHORT).show()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)-> getActivity()?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(android.Manifest.permission.CAMERA), CAMERA_RQ)
            }

            else -> getActivity()?.let { ActivityCompat.requestPermissions(it, arrayOf(android.Manifest.permission.CAMERA), CAMERA_RQ) }
        }
    }
}