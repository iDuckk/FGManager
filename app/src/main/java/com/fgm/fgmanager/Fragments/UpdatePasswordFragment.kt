package com.fgm.fgmanager.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fgm.fgmanager.DBHelpers.DBHelperForSavedName
import com.fgm.fgmanager.DBHelpers.DBHelperLogIn
import com.fgm.fgmanager.Models.modelForProductItems
import com.fgm.fgmanager.Models.modelUpPassword
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdatePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdatePasswordFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_update_password, container, false)
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

        val b_Users_Ok = view.findViewById<Button>(R.id.b_Users_Pass_Ok)
        val b_Users_Cancel = view.findViewById<Button>(R.id.b_Users_Pass_Cancel)
        val ed_OldPas = view.findViewById<EditText>(R.id.et_OldPassword)
        val ed_NewPas = view.findViewById<EditText>(R.id.et_NewPassword)
        val ed_ConfirmPas = view.findViewById<EditText>(R.id.et_ConfirmPassword)

        val modelUP = modelUpPassword(context)

        ed_OldPas.setText(STORAGE.OldPassword)

        b_Users_Ok.setOnClickListener{
            if(ed_NewPas.text.toString() != "" && ed_ConfirmPas.text.toString() != "" && ed_NewPas.text.toString() == ed_ConfirmPas.text.toString()){
                modelUP.UpdateUserPasswordFireStoreDB(STORAGE.userNameForChangePass, ed_NewPas.text.toString())
                Navigation.findNavController(view)
                    .navigate(R.id.action_updatePasswordFragment_to_itemUsersFragment) // Go to Item User Password
            }else{
                Toast.makeText(context, R.string.Uncorrect_LinesOfNewPassword, Toast.LENGTH_SHORT).show()
            }
        }

        b_Users_Cancel.setOnClickListener{
            Navigation.findNavController(view)
                .navigate(R.id.action_updatePasswordFragment_to_itemUsersFragment) // Go to Item User Password
        }

        //Navigation.findNavController(view)
        //                .navigate(R.id.action_itemFragment_to_craeteDateFragment) // Go to Create Fragment
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UpdatePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdatePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}