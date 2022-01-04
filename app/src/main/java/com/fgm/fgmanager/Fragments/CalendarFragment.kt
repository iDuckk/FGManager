package com.fgm.fgmanager.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.fgm.fgmanager.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var numberOfDays : Int = 0
        var selectedDate : String = ""
        val bundle = Bundle()
        val calendar = view.findViewById<CalendarView>(R.id.calendarView_My)
        val tv_SelectedDate = view.findViewById<TextView>(R.id.tv_SelectedDate)

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")  //Format for Date
        val value = LocalDate.now().format(formatter)       //Set Format// Current Date
        val parseDateNow = LocalDate.parse(value,formatter)      //This Argument for counting number of days. If I set it in TextView, that Format is YYYY/MM/DD...
        tv_SelectedDate.setText("$value")   //Set Text to TextView SelectedDate for current date


        calendar.setOnDateChangeListener { calendarView, y, m, d ->
            selectedDate = "$d/${m.toInt()+1}/$y" //String with selected Date
            val parseSelectedDate = LocalDate.parse(selectedDate, formatter)     //This Argument for counting number of days. //Date of Product
            numberOfDays = ChronoUnit.DAYS.between(parseDateNow, parseSelectedDate).toInt() //Amount of days
            tv_SelectedDate.setText(selectedDate)     //Set Text to TextView SelectedDate for selected date
        }


        val b_Calendar_Ok = view.findViewById<Button>(R.id.b_Calendar_Ok)
        b_Calendar_Ok.setOnClickListener {
            if (selectedDate != ""){
                bundle.putString("Date", selectedDate);     //Send String contains Selected Date to Create Date Fragment
            }else{
                bundle.putString("Date", tv_SelectedDate.text.toString());  //Send String contains Selected Date to Create Date Fragment IF DATE DOES NOT SELECT
            }
            bundle.putString("NumberOfDays", numberOfDays.toString())   //Send String contains Number Of Dates  to Create Date Fragment
            Navigation.findNavController(view).navigate(R.id.action_calendarFragment_to_craeteDateFragment, bundle) }

        val b_Calendar_Cancel = view.findViewById<Button>(R.id.b_Calendar_Cancel)
        b_Calendar_Cancel.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_calendarFragment_to_craeteDateFragment) }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CalendarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}