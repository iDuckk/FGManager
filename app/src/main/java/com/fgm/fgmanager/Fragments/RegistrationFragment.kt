package com.fgm.fgmanager.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fgm.fgmanager.R
import com.google.android.gms.ads.AdView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b_AddProduct = requireActivity().findViewById<Button>(R.id.button_Add_Product)
        val linear = requireActivity().findViewById<LinearLayout>(R.id.layoutMenu)
        val ad_FirstBaneer = requireActivity().findViewById<AdView>(R.id.adViewFirstBanner)
        //SET OFF Menu Bar
        linear.visibility = View.GONE
        b_AddProduct.visibility = View.GONE // Take off Button of Ad new Product
        //Set Off AdView Banner
        ad_FirstBaneer.visibility = View.GONE

        var amountOfUser : Int = 0

        val b_Cancel_Registration = view.findViewById<Button>(R.id.b_Reg_Cancel)
        val b_Ok_Registration = view.findViewById<Button>(R.id.b_Reg_Ok)
        val et_Type_Database = view.findViewById<EditText>(R.id.et_Name_Of_Database)
        val rg_Type_Plan = view.findViewById<RadioGroup>(R.id.id_Radio_Group)

        rg_Type_Plan.setOnCheckedChangeListener{group, Id->
            when(Id){
                R.id.radioButton_5u -> amountOfUser = 5
                R.id.radioButton_15 -> amountOfUser = 15
                R.id.radioButton_25 -> amountOfUser = 25
                R.id.radioButton_50 -> amountOfUser = 50
                R.id.radioButton_100 -> amountOfUser = 100
            }
        }

        b_Ok_Registration.setOnClickListener{
            if(et_Type_Database.text.toString() != ""){


                //Navigation.findNavController(this.requireView()).navigate(R.id.action_registrationFragment_to_myLoginFragment)
            }else{
                Toast.makeText(context, getString(R.string.ET_DatabaseName), Toast.LENGTH_SHORT).show()
            }
        }

        b_Cancel_Registration.setOnClickListener{
            Navigation.findNavController(this.requireView()).navigate(R.id.action_registrationFragment_to_myLoginFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}