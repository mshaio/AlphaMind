package com.example.alphamind

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm
import io.realm.RealmResults
import org.bson.types.ObjectId
import java.lang.Exception
import java.util.*

class ExerciseSettingsActivity : AppCompatActivity() {

//    lateinit var exerciseTextView: AutoCompleteTextView
//    exerciseTextView = findViewById(R.id.exercise_list)
//    lateinit var setTextView: AutoCompleteTextView
//    setTextView = findViewById(R.id.set_number)
//    lateinit var repTextView: AutoCompleteTextView
//    repTextView = findViewById(R.id.rep_number)
//    lateinit var weightTextView: AutoCompleteTextView
//    weightTextView = findViewById(R.id.weight)
//    lateinit var noteInputEditText: TextInputEditText
//    noteInputEditText = findViewById(R.id.notes)
//    lateinit var logTextView: EditText
//    logTextView = findViewById(R.id.logs)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.autumn_dark_1)

        try {
            val exerciseDate = intent.getStringExtra("date")
            getExistingActivityInfo(exerciseDate)
        } catch (e: Exception){
            println(e)
        }

        val exercises = listOf(
            "Bicep Curls",
            "Cables (down)",
            "Cables (up)",
            "Dead Lift",
            "Decline Bench Press",
            "Dumbbell Row",
            "Flat Bench Press",
            "Incline Bench Press",
            "Lat Pressdown",
            "Lat Pulldown",
            "Leg Press",
            "Leg Extension",
            "Pec Deck",
            "Pull-up",
            "Tricep Extension",
            "Tricep Pull Over",
            "Tricep Pushdown",
            "Seated Row",
            "Shrugs",
            "Squats",
        )
        val weights = listOf(5,7.5,10,12.5,15,17.5,20,22.5,25,30,35,40,45,90,180,270)
        val sets = listOf(1..5).flatten()
        val reps = listOf(1..30).flatten()

        val autotextView = findViewById<AutoCompleteTextView>(R.id.exercise_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, exercises)
        autotextView.setAdapter(adapter)

        val setCountView = findViewById<AutoCompleteTextView>(R.id.set_number)
        val setsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sets)
        setCountView.setAdapter(setsAdapter)

        val repCountView = findViewById<AutoCompleteTextView>(R.id.rep_number)
        val repsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reps)
        repCountView.setAdapter(repsAdapter)

        val weightView = findViewById<AutoCompleteTextView>(R.id.weight)
        val weightAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, weights)
        weightView.setAdapter(weightAdapter)

        lateinit var topAppBar: MaterialToolbar
        topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_log -> {
                    // Handle favorite icon press
                    saveExerciseData()
//                    Toast.makeText(getActivity(), "Back clicked!",     Toast.LENGTH_SHORT).show();
                    true
                }
                R.id.delete_all -> {
                    deleteAllObjectsInRealm()
                    true
                }
                else -> false
            }
        }
    }

    fun getTotalVolume(date: String): String {
        var todaysActivities = getActivitiesByDate(date)
        var totalVolume: Int = 0
        var volume: Int = 0
        for (activity in todaysActivities) {
            volume = activity.weights * activity.reps
            totalVolume += volume
        }
        println("+++")
        println(totalVolume)
        println("---")
        return totalVolume.toString() + " lbs"
    }

    fun updateLogText(existingLog: String, newSet: String, newRep: String, newWeight: String, newActivity: String): Pair<String,Boolean> {
        val newActivityInfo = arrayListOf<String>(newSet,newRep,newWeight,newActivity)
        var newLog = ""
        var detectedUpdates = false
        for (i in existingLog.lines().dropLast(1)) {
//          Examplie of i: Set 1 Reps 1 Weight 180lb Squats
            if (newSet == i.split(' ')[1] && newActivity.toRegex().containsMatchIn(i)) {
                newLog += i.replace(i.substringAfterLast("Reps "), newRep)
                newLog += " Weight "
                newLog += newWeight
                newLog += "lb "
                newLog += newActivity
                newLog += "\n"
                detectedUpdates = true
            } else {
                newLog += i
                newLog += "\n"
            }
        }
        return Pair(newLog,detectedUpdates)
    }

    fun saveExerciseData() {
        var exerciseTextView: AutoCompleteTextView = findViewById(R.id.exercise_list)
        var setTextView: AutoCompleteTextView = findViewById(R.id.set_number)
        var repTextView: AutoCompleteTextView = findViewById(R.id.rep_number)
        var weightTextView: AutoCompleteTextView = findViewById(R.id.weight)
        var noteInputEditText: TextInputEditText = findViewById(R.id.notes)
        var logTextView: EditText = findViewById(R.id.logs)
        var totalVolumeTextView: EditText = findViewById((R.id.total_volume))

        val date = intent.getStringExtra("date")
        val todaysExercise = intent.getStringExtra("exercise")

        if (exerciseTextView.text.length == 0) {
            Toast.makeText(applicationContext,"Exercise is missing",Toast.LENGTH_SHORT).show()
        } else if (setTextView.text.length == 0) {
            Toast.makeText(applicationContext,"Set # is missing",Toast.LENGTH_SHORT).show()
        } else if (repTextView.text.length == 0) {
            Toast.makeText(applicationContext,"Rep # is missing",Toast.LENGTH_SHORT).show()
        } else if (weightTextView.text.length == 0) {
            Toast.makeText(applicationContext, "Weights used is missing",Toast.LENGTH_SHORT).show()
        } else {
            var existingLogs: String = logTextView.getText().toString()
            var updatedLogs = updateLogText(existingLogs, setTextView.text.toString(), repTextView.text.toString(),
                weightTextView.text.toString(),exerciseTextView.text.toString())

            if (!updatedLogs.second) {
                logTextView.setText(existingLogs + "Set " + setTextView.text + " Reps " + repTextView.text + " Weight " + weightTextView.text + "lb " + exerciseTextView.text + "\n")
            } else {
                logTextView.setText(updatedLogs.first)
            }
            println("$$$")
            println(queryObjectInRealm())
            println("%%%")

            upsertObjectInRealm(ExerciseModel(exerciseTextView.text.toString(), todaysExercise, ObjectId(), setTextView.text.toString().toInt(),
                repTextView.text.toString().toInt(),
                weightTextView.text.toString().toInt(),
                date,noteInputEditText.text.toString()))

            totalVolumeTextView.setText(getTotalVolume(date))
//            updateObjectInRealm(ExerciseModel(exerciseTextView.text.toString(), todaysExercise, ObjectId(), setTextView.text.toString().toInt(),
//                repTextView.text.toString().toInt(),
//                weightTextView.text.toString().toInt(),
//                date,noteInputEditText.text.toString()))
            return
            saveObjectInRealm(exerciseTextView.text.toString(), todaysExercise, setTextView.text.toString().toInt(),
                                repTextView.text.toString().toInt(),
                                weightTextView.text.toString().toInt(),
                                date,noteInputEditText.text.toString())
        }
        println(queryObjectInRealm())
    }

    fun saveObjectInRealm(activityName: String, exerciseType: String, sets: Int, rep: Int, weights: Int, date: String, note: String) {
        val id = UUID.randomUUID().mostSignificantBits
        val activity = ExerciseModel(activityName, exerciseType, ObjectId(), sets, rep, weights, date, note)
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(activity)
        realm.commitTransaction()
    }

    fun deleteObjectInRealm(id: ObjectId) {
        Realm.init(this)
        val real = Realm.getDefaultInstance()
        real.beginTransaction()
        real.where(ExerciseModel::class.java).equalTo("_id", ObjectId())
    }

    fun deleteAllObjectsInRealm() {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        // delete all realm objects
        realm.deleteAll()

        //commit realm changes
        realm.commitTransaction()
    }

    fun queryObjectInRealm(): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).findAll()
//            .equalTo("_id", ObjectId.get()).findAll()
        return activities
    }

    fun getActivitiesByDate(date: String): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activitiesByDate = realm.where(ExerciseModel::class.java).equalTo("date", date).findAll()
        return activitiesByDate
    }

    fun getActivityToUpdate(activityToUpseart: ExerciseModel): ExerciseModel {
        val existingActivitiesByDate = getActivitiesByDate(activityToUpseart.date!!)
        println("000")
        println(existingActivitiesByDate)
        println("111")
        for (existingActivity in existingActivitiesByDate) {
            if (existingActivity.date == activityToUpseart.date && existingActivity.sets == activityToUpseart.sets && existingActivity.activity == activityToUpseart.activity) {
//                existingActivity.reps = activityToUpseart.reps
//                existingActivity.weights = activityToUpseart.weights
//                existingActivity.notes = activityToUpseart.notes
                activityToUpseart._id = existingActivity._id
                println("112")
                println(activityToUpseart._id)
//                println(existingActivity)
                return activityToUpseart
            }
        }
        println("113")
        return activityToUpseart
    }

    fun updateObjectInRealm(activity: ExerciseModel) {
        if (activity == null) {
            return
        }
        val activityToUpdate = getActivityToUpdate(activity)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
            // Get a turtle to update.
        val existingActivity = realm.where(ExerciseModel::class.java).equalTo("date",activityToUpdate!!.date).
        equalTo("sets", activityToUpdate.sets).equalTo("activity", activityToUpdate.activity).findFirst()
        // Update properties on the instance.
        // This change is saved to the realm.

        realm.executeTransaction { r: Realm ->
            existingActivity!!.reps = activityToUpdate.reps
            existingActivity!!.weights = activityToUpdate.weights
            existingActivity!!.notes = activityToUpdate.notes
        }

        realm.commitTransaction()

    }

    fun upsertObjectInRealm(activity: ExerciseModel) {
        val activityToUpsert = getActivityToUpdate(activity)
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(activityToUpsert)
        realm.commitTransaction()
    }

    private fun getExistingActivityInfo(exerciseDate: String) {

        var exerciseTextView: AutoCompleteTextView = findViewById(R.id.exercise_list)
        var setTextView: AutoCompleteTextView = findViewById(R.id.set_number)
        var repTextView: AutoCompleteTextView = findViewById(R.id.rep_number)
        var weightTextView: AutoCompleteTextView = findViewById(R.id.weight)
        var noteInputEditText: TextInputEditText = findViewById(R.id.notes)
        var logTextView: EditText = findViewById(R.id.logs)
        var totalVolumeTextView: EditText = findViewById(R.id.total_volume)

//        val existingActivity: RealmResults<ExerciseModel> = queryObjectInRealm()
        val existingActivity: RealmResults<ExerciseModel> = getActivitiesByDate(exerciseDate)
        var selectedItemPosition = intent.getIntExtra("selected",0)

        if (existingActivity.size > 0) {
            println("AAA")
            println(existingActivity.size)
            println(existingActivity)
            println("BBB")
            for (item in 0..existingActivity.size-1) {
                exerciseTextView.setText(existingActivity[item]?.activity)
                setTextView.setText(existingActivity[item]?.sets.toString())
                repTextView.setText(existingActivity[item]?.reps.toString())
                weightTextView.setText(existingActivity[item]?.weights.toString())
                noteInputEditText.setText(existingActivity[item]?.notes)
                logTextView.setText(logTextView.text.toString() + "Set " + setTextView.text + " Reps " + repTextView.text +
                        " Weight " + weightTextView.text + "lb " + exerciseTextView.text + "\n")
            }
            totalVolumeTextView.setText(getTotalVolume(exerciseDate))
        }
    }
}

//Database idea: one table, columns: id, date, exercise, set, weight, rep, notes