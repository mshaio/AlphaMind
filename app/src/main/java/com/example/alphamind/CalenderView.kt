package com.example.alphamind

//import android.R

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.alphamind.databinding.ActivityCalenderViewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.LocalDateTime


class CalenderView : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCalenderViewBinding

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivityCalenderViewBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbar0)

        setContentView(R.layout.activity_calender_view)
        var calendarActivity: CalendarView = findViewById(R.id.next_month_calendar_view)
        var todaysDate = calendarActivity.date
        val currentDateTime = LocalDateTime.now()
        val selectedDate = "12/09/2021"
        calendarActivity.setDate(
            SimpleDateFormat("dd/MM/yyyy").parse(selectedDate).getTime(),
            true,
            true
        )
        println(calendarActivity.dateTextAppearance)
        println(currentDateTime.toString())
        println(todaysDate)

//        var nextMonthCalenderView: View = calendarActivity.findViewById<View>(R.id.next_month_calendar_view)

//        val navController = findNavController(R.id.nav_host_fragment_content_calender_view)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_calender_view)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}