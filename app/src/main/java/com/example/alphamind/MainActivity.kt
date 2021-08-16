package com.example.alphamind

import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
//import com.example.alphamind.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import java.util.*
import kotlin.collections.ArrayList

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import org.bson.types.ObjectId

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityMainBinding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//
//            val intent = Intent(this, ExeciseForm::class.java)
////            intent.putExtra("key",value)
//            startActivity(intent)
//        }
//    }

    private val itemsList = ArrayList<String>()
    private val dateList = ArrayList<String>()
    private val selectionList = ArrayList<Boolean>()
    private lateinit var customAdapter: CustomAdapter

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(this, itemsList, dateList, selectionList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()

        println("^^^")
        println(queryObjectInRealm())
        println("***")

        window.statusBarColor = ContextCompat.getColor(this, R.color.autumn_dark_1)
        val exerciseChoice: FloatingActionButton = findViewById(R.id.execrise_choice)
        val calendar: FloatingActionButton = findViewById(R.id.caledar)
//        var selectedItem: SwitchMaterial = findViewById(R.id.unique_item)

        lateinit var topAppBar: MaterialToolbar
        topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_log -> {
                    // Handle favorite icon press
//                    saveExerciseData()
//                    Toast.makeText(getActivity(), "Back clicked!",     Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.delete_all -> {
//                    deleteAllObjectsInRealm()
                    true
                }
                R.id.delete_one -> {
                    deleteActivity(selectionList)
                    true
                }
                R.id.calendar_view -> {
                    displayCalenderView()
                    true
                }
                else -> false
            }
        }


        calendar.setOnClickListener { view ->
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
            val picker = builder.build()
            picker.show(supportFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener {
                val dateSelected = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                dateSelected.time = Date(it)
                dateList.add(dateSelected.get(Calendar.DAY_OF_MONTH).toString() + "-" + (dateSelected.get(
                    Calendar.MONTH)+1).toString() + "-" + dateSelected.get(Calendar.YEAR).toString())
                customAdapter.notifyDataSetChanged()
            }
        }

        exerciseChoice.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            println("TEST1")
            val singleItems = arrayOf("Arms","Back","Chest","Legs","Pull","Push")
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
                    selectionList.add(false)
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

//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        super.onResume()
        println("WWW")
        prepareItems()
    }

    private fun prepareItems() {
        println("QQQ")
        println(queryObjectInRealm())
        for (item in queryObjectInRealm()) {
            if (!dateList.contains(item.date)) {
                itemsList.add(item.exerciseType)
                dateList.add(item.date!!)
                selectionList.add(false)
            }
        }
        customAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun queryObjectInRealm(): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).sort("date", Sort.DESCENDING).findAll()
        println(activities.javaClass.name)
        return activities
    }

    fun deleteActivity(selections: ArrayList<Boolean>) {
        var deletedDates: ArrayList<String> = arrayListOf()
        var badIndex: ArrayList<Int> = arrayListOf()

        for (i in 0..selections.size-1) {
            if (selections[i] == true && badIndex.size == 0) {
                badIndex.add(i)
                deletedDates.add(dateList[i])
            } else if (selections[i] == true && badIndex.size > 0){
                badIndex.add(i-badIndex.size)
                deletedDates.add(dateList[i])
            }
        }

        for (i in badIndex) {
            itemsList.removeAt(i)
            dateList.removeAt(i)
            selectionList.removeAt(i)
        }
        customAdapter.notifyDataSetChanged()

        for (i in 0..deletedDates.size-1) {
            deleteSelectedObjectsInRealm(deletedDates[i])
        }
    }

    fun deleteSelectedObjectsInRealm(date: String) {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.where(ExerciseModel::class.java).equalTo("date", date).findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    fun displayCalenderView() {
        val intent = Intent(this, CalenderView::class.java)
        startActivity(intent)
    }
}