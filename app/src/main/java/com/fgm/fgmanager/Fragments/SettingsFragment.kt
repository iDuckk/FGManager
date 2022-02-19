package com.fgm.fgmanager.Fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fgm.fgmanager.R
import com.fgm.fgmanager.STORAGE
import com.google.android.gms.ads.AdView
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val LIGHT_THEME = 0
    val DARK_THEME = 1
    val TYPE_THEME = "themeType"

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonBack = view.findViewById<Button>(R.id.button_settings_back)
        val switchTheme = view.findViewById<Switch>(R.id.switch_theme)

        val b_AddProduct = requireActivity().findViewById<Button>(R.id.button_Add_Product)
        val linear = requireActivity().findViewById<LinearLayout>(R.id.layoutMenu)
        val ad_FirstBaneer = requireActivity().findViewById<AdView>(R.id.adViewFirstBanner)
        //SET OFF Menu Bar
        linear.visibility = View.GONE
        b_AddProduct.visibility = View.GONE

        //Set AdView Banner
        if(STORAGE.TypeAccFree){
            ad_FirstBaneer.visibility = View.GONE
        }else {
            ad_FirstBaneer.visibility = View.VISIBLE
        }

        buttonBack.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_menuFragment)
        }

        //Set Switch on if dark theme
        if(STORAGE.typeTheme == DARK_THEME)
        switchTheme.isChecked = true

        switchTheme.setOnClickListener{
            if(switchTheme.isChecked){
                setTheme(AppCompatDelegate.MODE_NIGHT_YES, DARK_THEME)

//                Log.d("TAG", value)
            }else{
                setTheme(AppCompatDelegate.MODE_NIGHT_NO, LIGHT_THEME)
            }
        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)

        STORAGE.typeTheme = prefsMode
        //Save statement of Theme in Sharepreference
        val sharedPref = requireActivity()?.getPreferences(MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
        putInt(TYPE_THEME, prefsMode)
        apply()
    }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}