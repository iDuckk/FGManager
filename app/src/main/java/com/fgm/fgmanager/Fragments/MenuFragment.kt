package com.fgm.fgmanager.Fragments

import android.app.AlertDialog
import android.os.Bundle
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fgm.fgmanager.DBHelpers.DBHelperLogIn
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b_Back = view.findViewById<Button>(R.id.b_Menu_Back)
        val b_Exit = view.findViewById<Button>(R.id.b_Menu_LogOut)
        val b_Users = view.findViewById<Button>(R.id.b_ListOfUsers)

        val mActivity : MainActivity = activity as MainActivity
        val b_AddProduct = mActivity.findViewById<Button>(R.id.button_Add_Product)
        val linear = mActivity.findViewById<LinearLayout>(R.id.layoutMenu)
        //SET OFF Menu Bar
        linear.visibility = View.GONE
        b_AddProduct.visibility = View.GONE // Take off Button of Ad new Product

        requireActivity().onBackPressedDispatcher.addCallback(      //If press CallBack BUtton on phone
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_itemFragment)
                }
            }
        )

        b_Users.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_itemUsersFragment)
        }

        //Return to ItemFragment
        b_Back.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_itemFragment)
        }

        b_Exit.setOnClickListener{
            DialogToQuit(view)     //Quit From Online DataBase
        }

    }

    fun DialogToQuit(view : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialog =  builder.setTitle(R.string.Log_Out_Title).setMessage(R.string.Question_To_LogOut)
            .setTitle(R.string.Log_Out_Title)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(R.string.Question_To_LogOut)
            .setPositiveButton(R.string.Answer_Delete_Item_Yes){
                    dialog, id ->
                logOut()
                Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_myLoginFragment)
            }
            .setNegativeButton(R.string.Answer_Delete_Item_No, null)
            .create()
        dialog.show()
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}