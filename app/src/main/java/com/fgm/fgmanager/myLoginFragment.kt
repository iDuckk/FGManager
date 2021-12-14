package com.fgm.fgmanager

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.fgm.fgmanager.placeholder.PlaceholderContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [myLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

lateinit var auth : FirebaseAuth

class myLoginFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_my_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))      //Red color
        //val colorStateListBlack = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black)) //Gray color

        val et_Username = view.findViewById<EditText>(R.id.username)
        val et_Password = view.findViewById<EditText>(R.id.password)
        val b_Login = view.findViewById<Button>(R.id.login)
        val b_FreeLogin = view.findViewById<Button>(R.id.b_FreeLogin)

        //Take of Menu Bar
        val mActivity : MainActivity = activity as MainActivity
        val linear = mActivity.findViewById<LinearLayout>(R.id.layoutMenu)
        val b_AddProduct = mActivity.findViewById<Button>(R.id.button_Add_Product)
        linear.visibility = View.GONE
        b_AddProduct.visibility = View.GONE

        //et_Username.setOnFocusChangeListener { view, b -> et_Username.backgroundTintList = colorStateListBlack  }    //Change color in Gray after incorrect Typing UserName
        //et_Password.setOnFocusChangeListener { view, b -> et_Password.backgroundTintList = colorStateListBlack  }    //Change color in Gray after incorrect Typing Password

        //If you press CallBack`s Button on the phone
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //Do nothing
                }
            }
        )

        b_Login.setOnClickListener(){
            if(et_Username.text.toString() != "" && et_Password.text.toString() != ""){
                login(et_Username.text.toString(), et_Password.text.toString())
            }else {
                if(et_Username.text.toString() == "") {
                    et_Username.setError("Fill the gap")
                    et_Username.backgroundTintList =
                        colorStateList     //Change color in Gray If incorrect Typing UserName or not Typing
                }
                if(et_Password.text.toString() == "") {
                    et_Password.setError("Fill the gap")
                    et_Password.backgroundTintList =
                        colorStateList     //Change color in Gray If incorrect Typing Password or not Typing
                }

                Toast.makeText(context, "Заполните оба поля Логин и Пароль", Toast.LENGTH_SHORT).show()
            }
        }

        b_FreeLogin.setOnClickListener(){   //Log In Free account with offline DataBase
            freeLogin()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment myLoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            myLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun login(username: String, password: String){
        auth = Firebase.auth
        val mActivity : MainActivity = activity as MainActivity
        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
        val progressBar = mActivity.findViewById<ProgressBar>(R.id.progressBar)

        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(){
            if(it.isSuccessful){
                //SET User Name in Menu
                progressBar.visibility = View.VISIBLE
                STORAGE.TypeAccFree = true
                tv_Users.setText(username)
                Navigation.findNavController(this.requireView()).navigate(R.id.action_myLoginFragment_to_itemFragment)
            }else{
                Toast.makeText(context, "Неверный Логин или Пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun freeLogin(){
        val mActivity : MainActivity = activity as MainActivity
        val tv_Users = mActivity.findViewById<TextView>(R.id.tv_User)
        tv_Users.setText("Free account")
        STORAGE.TypeAccFree = false
        Navigation.findNavController(this.requireView()).navigate(R.id.action_myLoginFragment_to_itemFragment)
    }
}