package com.fgm.fgmanager

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()
        val arrayOfSaveprod = CreateFireDBForSaveProduct() //Receive database ofName Of products for fill TextEdit Name

        val et_NameProduct = view.findViewById<EditText>(R.id.et_Name)
        val et_Barcode = view.findViewById<EditText>(R.id.et_Barcode)
        val tv_Date = view.findViewById<TextView>(R.id.tv_Date)
        val b_Ok = view.findViewById<Button>(R.id.b_Ok)
        val b_Cancel = view.findViewById<Button>(R.id.b_Cancel)
        val image_Calendar = view.findViewById<ImageView>(R.id.im_Calendar)
        val image_Bacode = view.findViewById<ImageView>(R.id.im_scan_Barcode)
        val tv_AmountDate = view.findViewById<TextView>(R.id.tv_amount_days)

        val mActivity : MainActivity = activity as MainActivity
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
            if(SetExistNameToEditTextNameOfProduct(BarcodeResult, arrayOfSaveprod) != ""){
                Log.d("TAG", SetExistNameToEditTextNameOfProduct(BarcodeResult, arrayOfSaveprod))   //Receive Name
                et_NameProduct.setText(SetExistNameToEditTextNameOfProduct(BarcodeResult, arrayOfSaveprod)) //Set Text
            }
        }else{
            //Set Name Product from ProductNameFDB we receive Barcode From TYPING
            et_Barcode.setOnClickListener(){
                if(SetExistNameToEditTextNameOfProduct(et_Barcode.text.toString(), arrayOfSaveprod) != ""){
                    et_NameProduct.setText(SetExistNameToEditTextNameOfProduct(et_Barcode.text.toString(), arrayOfSaveprod))    //Set Text
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
            //Add Arguments in FireBase IF arguments do not NULL
            if(et_NameProduct.text.toString() != "" && et_Barcode.text.toString() != ""&& tv_Date.text.toString() != ""
                && amountDays.toString() != "" && amountDays!!.toInt() > 0) {
            //Add new Name of Product
                    for (i in 0..arrayOfSaveprod.lastIndex) // arrayOfSaveprod.lastIndex - amount of SaveProductNameDB
                        if (et_Barcode.text.toString() != arrayOfSaveprod[i].productBarcode){
                            STORAGE.booleanForCheckExistsOfNameOfProducts = true //If new name doesn't exist
                        }
                if (STORAGE.booleanForCheckExistsOfNameOfProducts){ //add new Name product to ProductDB
                    myRef.push().setValue(
                        SaveProductNameDB(
                            et_NameProduct.text.toString(),
                            et_Barcode.text.toString()
                        )
                    )
                    STORAGE.booleanForCheckExistsOfNameOfProducts = false
                }
                //add arguments in array for create new Items
                val arrayOfTitle: ArrayList<String> = ArrayList()
                arrayOfTitle+=et_NameProduct.text.toString()
                arrayOfTitle+=et_Barcode.text.toString()
                arrayOfTitle+=tv_Date.text.toString()
                arrayOfTitle+=amountDays
                //Send array to ItemActivity, I do it because I need refresh immediately RecycleView
                bundle.putStringArrayList("NameProductForFirebase", arrayOfTitle)

//                myRef.push().setValue(
//                    PlaceholderContent.PlaceholderItem(
//                        et_NameProduct.text.toString(),
//                        et_Barcode.text.toString(),
//                        tv_Date.text.toString(),
//                        "" //Because Amount changes everyday, need to reCount Value in ItemFragment. I just do not want reWrit my Code =) And do not delete calculating
//                    )
//                )
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

//************************************************************************************************************************************
        /*   fun checkForPermissions(permission: String, name: String, requestCode: Int){
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
           when{
               ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED->{
                   Navigation.findNavController(view).navigate(R.id.action_craeteDateFragment_to_scanBarcodeFragment)
                   //Toast.makeText(requireContext(), "$name permission granted", Toast.LENGTH_SHORT).show()
               }
               shouldShowRequestPermissionRationale(permission)-> getActivity()?.let {
                   ActivityCompat.requestPermissions(
                       it, arrayOf(permission), requestCode)
               }

               else -> getActivity()?.let { ActivityCompat.requestPermissions(it, arrayOf(permission), requestCode) }
           }
       }
   }
   checkForPermissions(android.Manifest.permission.CAMERA, "camera", CAMERA_RQ)
   */
    }

    fun CreateFireDBForSaveProduct() : ArrayList<SaveProductNameDB> {
        var arrayOfSaveProducts: ArrayList<SaveProductNameDB> = ArrayList()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (arrayOfSaveProducts.size > 0) arrayOfSaveProducts.clear() //IF ArrayList doesn't empty
                for (ds: DataSnapshot in snapshot.children) //Get All children from FireBase
                {
                    val item =
                        ds.getValue(SaveProductNameDB::class.java)  //Take ONE item
                    if (item != null) {
                        arrayOfSaveProducts.add(item)   //Add item
//                        Log.d("TAG", item.productName)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return arrayOfSaveProducts
    }

    fun SetExistNameToEditTextNameOfProduct(name : String, arrayOfSaveProduct: ArrayList<SaveProductNameDB>) : String {
        var strName : String = ""
        for (i in 0..arrayOfSaveProduct.lastIndex) { // arrayOfSaveprod.lastIndex - amount of SaveProductNameDB
            if (name == arrayOfSaveProduct[i].productBarcode) {
                strName = arrayOfSaveProduct[i].productName.toString()
            } else strName = ""
        }
        return strName
    }


}