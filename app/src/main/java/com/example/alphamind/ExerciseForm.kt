package com.example.alphamind

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import androidx.recyclerview.widget.DefaultItemAnimator
import java.util.*
import kotlin.collections.ArrayList

class ExeciseForm : AppCompatActivity() {

    private val itemsList = ArrayList<String>()
    private val dateList = ArrayList<String>()
    private val selectionList = ArrayList<Boolean>()
    private lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_form)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(this, itemsList, dateList, selectionList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()
        val tabs: TabLayout = findViewById(R.id.tabs)
        val exerciseChoice: FloatingActionButton = findViewById(R.id.execrise_choice)
        val calendar: FloatingActionButton = findViewById(R.id.caledar)

        calendar.setOnClickListener { view ->
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()
            picker.show(supportFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener {
                val dateSelected = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                dateSelected.time = Date(it)
                dateList.add(dateSelected.get(Calendar.DAY_OF_MONTH).toString() + "-" + (dateSelected.get(Calendar.MONTH)+1).toString() + "-" + dateSelected.get(Calendar.YEAR).toString())
                customAdapter.notifyDataSetChanged()
            }
        }

        exerciseChoice.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            println("TEST1")
            val singleItems = arrayOf("Flat Bench Press", "Incline Bench Press", "Decline Bench Press",
                "Pec Deck", "Cables (up)", "Cables (down)", "Bicep Curls", "Tricep Extension",
                "Tricep Pushdown", "Squats", "Leg Press", "Leg Extension", "Dead Lift", "Lat Pulldown", "Pull-up", "Dumbbell Row")
            val checkedItem = 1
            var selectedExercise: String = ' '.toString()

            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.title))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to neutral button press
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    itemsList.add(selectedExercise)
                    customAdapter.notifyDataSetChanged()
                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                    // Respond to item chosen
                    println(singleItems[which])
                    println(singleItems[which]::class.simpleName)
                    selectedExercise = singleItems[which]
                }
                .show()
        }
    }

    private fun prepareItems() {
        itemsList.add("Item 1")
        dateList.add("1-8-2021")
        itemsList.add("Item 2")
        dateList.add("2-8-2021")
        customAdapter.notifyDataSetChanged()
    }
}




//            var intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            MaterialAlertDialogBuilder(this)
//                .setTitle(resources.getString(R.string.title))
//                .setMessage(resources.getString(R.string.supporting_text))
//                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
//                    // Respond to neutral button press
//                }
//                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
//                    // Respond to negative button press
//                }
//                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
//                    // Respond to positive button press
//                }
//                .show()