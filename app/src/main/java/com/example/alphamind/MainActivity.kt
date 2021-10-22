package com.example.alphamind

import android.content.ClipData
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
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
//import com.example.alphamind.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.ArrayList

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.util.zip.Inflater
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val itemsList = ArrayList<String>()
    private var filteredItemsList = ArrayList<String>()
    private val dateList = ArrayList<String>()
    private val selectionList = ArrayList<Boolean>()
    private lateinit var customAdapter: CustomAdapter

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(this, itemsList, dateList, selectionList)
//        customAdapter = CustomAdapter(this, filteredItemsList, dateList, selectionList)

        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()

        println("^^^")
        println(queryObjectInRealm())
        println("***")

        window.statusBarColor = ContextCompat.getColor(this, R.color.brown_300)
        window.decorView.setBackgroundColor(resources.getColor(R.color.brown_300))
        val exerciseChoice: FloatingActionButton = findViewById(R.id.execrise_choice)
        val calendar: FloatingActionButton = findViewById(R.id.caledar)
//        val search: SearchView = findViewById<SearchView>(R.id.searchView)

        lateinit var topAppBar: MaterialToolbar
        topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val menuItem = topAppBar.menu.findItem(R.id.save_log)
        menuItem.isVisible = false

        checkSignInStatus(topAppBar)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_log -> {
                    // Handle favorite icon press
//                    saveExerciseData()
//                    Toast.makeText(getActivity(), "Back clicked!",     Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.charts -> {
                    displayDashboard()
                    true
                }
                R.id.arms_filter -> {
                    filterExercises("Arms")
                    true
                }
                R.id.back_filter -> {
                    filterExercises("Back")
                    true
                }
                R.id.chest_filter -> {
                    filterExercises("Chest")
                    true
                }
                R.id.legs_filter  -> {
                    filterExercises("Legs")
                    true
                }
                R.id.pull_filter -> {
                    filterExercises("Pull")
                    true
                }
                R.id.push_filter -> {
                    filterExercises("Push")
                    true
                }
                R.id.all -> {
                    filterExercises()
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
                R.id.action_settings -> {
                    displayGoogleSignIn()
                    true
                }
                R.id.save_to_firestore -> {
                    saveDataToCloud()
                    true
                }
                else -> false
            }
        }

        exerciseChoice.isEnabled = false

        calendar.setOnClickListener { view ->
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
            val picker = builder.build()
            picker.show(supportFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener {
                if (exerciseChoice.isEnabled) {
                    dateList.removeAt(dateList.size-1)
                }
                val dateSelected = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                dateSelected.time = Date(it)
                dateList.add(dateSelected.get(Calendar.DAY_OF_MONTH).toString() + "-" + (dateSelected.get(
                    Calendar.MONTH)+1).toString() + "-" + dateSelected.get(Calendar.YEAR).toString())
                customAdapter.notifyDataSetChanged()
                exerciseChoice.isEnabled = true
            }
        }

        exerciseChoice.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            val singleItems = arrayOf("Arms","Back","Chest","Legs","Pull","Push")
            val checkedItem = 0
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
                    exerciseChoice.isEnabled = false
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

//        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (itemsList.contains(query)) {
//                    Realm.init(applicationContext)
//                    val realm = Realm.getDefaultInstance()
//                    val activities = realm.where(ExerciseModel::class.java).sort("date", Sort.DESCENDING).equalTo("exerciseType",query).findAll()
//                    for (item in activities) {
//                        if (!dateList.contains(item.date)) {
//                            itemsList.add(item.exerciseType)
//                            dateList.add(item.date!!)
//                            filteredItemsList.addAll(itemsList)
//                            selectionList.add(false)
//                        }
//                    }
////                    filteredItemsList.addAll(itemsList)
//                    customAdapter.notifyDataSetChanged()
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (filteredItemsList.contains(newText)) {
//                    println("VVV")
//                    println(filteredItemsList)
//                }
//                if (itemsList.contains(newText)) {
//                    println("VVB")
//                    println(itemsList)
//                }
//                return false
//            }
//        })

    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        super.onResume()
        prepareItems()
        checkSignInStatus(findViewById<MaterialToolbar>(R.id.topAppBar))
}

    private fun prepareItems(exerciseType: String? = "") {
        itemsList.clear()
        dateList.clear()
        selectionList.clear()
        filteredItemsList.clear()
        for (item in queryObjectInRealm(exerciseType)) {
            if (!dateList.contains(item.date)) {
                itemsList.add(item.exerciseType)
                dateList.add(item.date!!)
                selectionList.add(false)
                filteredItemsList.add(item.exerciseType)
            }
        }
        customAdapter.notifyDataSetChanged()
    }

    private fun checkSignInStatus(topAppBar: MaterialToolbar) {
        if (Authentication().isSignedIn()) {
            topAppBar.menu.findItem(R.id.action_settings).title = "Logout"
        } else {
            topAppBar.menu.findItem(R.id.action_settings).title = "Login"
        }
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

    private fun queryObjectInRealm(exerciseType: String? = ""): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        lateinit var activities: RealmResults<ExerciseModel>
        if (exerciseType?.length!! == 0) {
            println("length is 0")
            activities = realm.where(ExerciseModel::class.java).sort("dateDate", Sort.DESCENDING).findAll()
        } else {
            println("length is not 0")
            activities = realm.where(ExerciseModel::class.java).sort("dateDate", Sort.DESCENDING).equalTo("exerciseType",exerciseType).findAll()
        }
        return activities
    }

    private fun queryObjectInRealmRequiresCloudUpdate():RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        return realm.where(ExerciseModel::class.java).sort("dateDate",Sort.DESCENDING).equalTo("savedInCloud",false).findAll()
    }

    private fun filterExercises(visibleExerciseType: String? = "") {
        prepareItems(visibleExerciseType)
    }

    private fun deleteActivity(selections: ArrayList<Boolean>) {
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

    private fun deleteSelectedObjectsInRealm(date: String) {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.where(ExerciseModel::class.java).equalTo("date", date).findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    private fun displayCalenderView() {
        val intent = Intent(this, CalenderView::class.java)
        startActivity(intent)
    }

    private fun displayDashboard() {
        val intent = Intent(this, dashboard::class.java)
        startActivity(intent)
    }

    private fun displayGoogleSignIn() {
        val intent = Intent(this, Authentication::class.java)
        startActivity(intent)
    }

    private fun upsertObjectInRealm(activityToUpsert: ExerciseModel) {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(activityToUpsert)
        realm.commitTransaction()
    }

    private fun saveDataToCloud() {
        val activitiesInRealm = queryObjectInRealmRequiresCloudUpdate() //queryObjectInRealm()
        val db = FirebaseFirestore.getInstance()
        val uid = Authentication().getUserId()

        data class ExerciseData(
            var activity: String = "",
            var exerciseType: String = "",
            var _id: String = ObjectId().toString(),
            var sets: Int = 0,
            var reps: Int = 0,
            var weights: Int = 0,
            var date: String? = null,
            var dateDate: Date? = null,
            var notes: String? = null
        )

        if (!Authentication().isSignedIn() && uid.length <= 0) {
            Toast.makeText(this,"Please Sign in with Google to sync",Toast.LENGTH_SHORT).show()
            return
        }
//        val city = hashMapOf(
//            "name" to "Los Angeles",
//            "state" to "CA",
//            "country" to "USA"
//        )
//        db.collection("cities").document("LA")
//            .set(city)
//            .addOnSuccessListener { Toast.makeText(this,"DocumentSnapshot successfully written!", Toast.LENGTH_LONG).show() }
//            .addOnFailureListener { e -> println(e) }

        if (activitiesInRealm.size == 0) {
            Toast.makeText(this,"Everything is up to date", Toast.LENGTH_SHORT).show()
            return
        }

        for (activityInRealm in activitiesInRealm) {
            if (!activityInRealm.savedInCloud) {
                var exerciseDataForCloud = ExerciseData(activityInRealm.activity,
                    activityInRealm.exerciseType,
                    activityInRealm._id.toString(),
                    activityInRealm.sets,
                    activityInRealm.reps,
                    activityInRealm.weights,
                    activityInRealm.date,
                    activityInRealm.dateDate,
                    activityInRealm.notes)
                db.collection("ExerciseData").document(uid+"_"+activityInRealm._id.toString())
                    .set(exerciseDataForCloud, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this,"Data Transferring, Please Wait", Toast.LENGTH_SHORT).show()
                        upsertObjectInRealm(ExerciseModel(activityInRealm.activity,
                                                          activityInRealm.exerciseType,
                                                          activityInRealm._id,
                                                          activityInRealm.sets,
                                                          activityInRealm.reps,
                                                          activityInRealm.weights,
                                                          activityInRealm.date,
                                                          activityInRealm.dateDate,
                                                          activityInRealm.notes,
                                                          true)
                        )
                    }
                    .addOnFailureListener { e -> println(e) }
            }
        }
    }
}