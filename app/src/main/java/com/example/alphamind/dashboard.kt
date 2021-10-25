package com.example.alphamind

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.appbar.MaterialToolbar
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class dashboard : AppCompatActivity() {
    object ActivityConstants {
        const val BICEPT_CURLS = "Bicep Curls"
        const val CABLES_BICEPT_CURLS = "Cables Bicep Curls"
        const val CABLES_DOWN = "Cables (down)"
        const val CABLES_UP = "Cables (up)"
        const val DEAD_LIFT = "Dead Lift"
        const val DECLINE_BENCH_PRESS = "Decline Bench Press"
        const val DUMBBELL_ROW = "Dumbbell Row"
        const val FLAT_BENCH_PRESS = "Flat Bench Press"
        const val INCLINE_BENCH_PRESS = "Incline Bench Press"
        const val LAT_PRESSDOWN = "Lat Pressdown"
        const val LAT_PULLDOWN = "Lat Pulldown"
        const val LEG_CURLS = "Leg Curls"
        const val LEG_EXTENSION = "Leg Extension"
        const val LEG_PRESS = "Leg Press"
        const val PEC_DECK = "Pec Deck"
        const val PULL_UP = "Pull-up"
        const val ROW = "Row"
        const val TRICEP_EXT_CABLE = "Tricep Extension (Cable)"
        const val TRICEP_EXT_DUMBBELL = "Tricep Extension (Dumbbell)"
        const val TRICEP_PUSHDOWN = "Tricep Pushdown"
        const val SEATED_CABLE_ROW = "Seated Cable Row"
        const val SHRUGS = "Shrugs"
        const val SQUATS = "Squats"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.brown_300)
        }
        window.decorView.setBackgroundColor(resources.getColor(R.color.brown_300))


        var topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        AppBarUtils(topAppBar).setUpDefaultAppBar()

        val menuItem = topAppBar.menu.findItem(R.id.save_log)
        menuItem.isVisible = false
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.calendar_view -> {
                    displayCalenderView()
                    true
                }
                else -> false
            }
        }

        var radarChart: RadarChart = findViewById(R.id.radar_chart)
        var radarChart2: RadarChart = findViewById(R.id.radar_chart2)
        exerciseTypeTotalCount(radarChart)
        exerciseTypeTotalCountForThisMonthAndLast(radarChart2)

        var cubicChart: LineChart = findViewById(R.id.cubic_chart)
        cubicChart(cubicChart)

        var daysExercised: TextView = findViewById(R.id.tv_total_days_exercised)
        var daysExercisedThisMonth: TextView = findViewById(R.id.tv_total_days_exercised_this_month)
        var daysExercisedLastMonth: TextView = findViewById(R.id.tv_total_days_exercised_last_month)
        daysExercised.text = getNumberOfDaysExercised(queryObjectInRealm())
        daysExercisedThisMonth.text = getNumberOfDaysExercisedThisMonth()
        daysExercisedLastMonth.text = getNumberOfDaysExercisedThisMonth("LAST")
        if (daysExercised.length() > 2) {
            daysExercised.textSize = 50f
        } else if (daysExercised.length() > 3) {
            daysExercised.textSize = 40f
        }
    }

    private fun queryObjectInRealm(): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).sort("date", Sort.DESCENDING).findAll()
        return activities
    }

    private fun queryObjectInRealm(queryField: String, queryValue: String = ""): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).sort("date", Sort.DESCENDING).equalTo(queryField,queryValue).findAll()
        return activities
    }

    private fun queryMaxObjectInRealm(queryField: String, queryValue: String = ""): Int {
        //Queries the database to get the maximum weight lifted for a given activity type, ie Bicep Curls
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        return if (realm.where(ExerciseModel::class.java).equalTo(queryField,queryValue).sort("weights",Sort.DESCENDING).findAll().size > 0)
            realm.where(ExerciseModel::class.java).equalTo(queryField,queryValue).sort("weights",Sort.DESCENDING).findFirst()!!.weights
        else {
            0
        }
    }

    private fun queryObjectInRealmInMonth(startOfMonth: Date, endOfMonth: Date, queryField: String? = "", queryValue: String? = ""): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        lateinit var activities: RealmResults<ExerciseModel>
        activities = if (queryField!!.isEmpty() || queryValue!!.isEmpty()) {
            realm.where(ExerciseModel::class.java).greaterThanOrEqualTo("dateDate",startOfMonth).lessThanOrEqualTo("dateDate",endOfMonth).findAll()
        } else {
            realm.where(ExerciseModel::class.java).greaterThanOrEqualTo("dateDate",startOfMonth).lessThanOrEqualTo("dateDate",endOfMonth).equalTo(queryField,queryValue).findAll()
        }

        return activities
    }

    private fun cubicChart(cubicChart: LineChart) {
        lateinit var lineData: LineData
        lateinit var lineDataSet: LineDataSet
        var lineEntries: ArrayList<Entry> = ArrayList<Entry>()
        var exercises = mutableListOf(
            "Bicep Curls",
            "Cables Bicep Curls",
            "Cables Fly",
            "Dead Lift",
            "Decline Bench Press",
            "Dumbbell Row",
            "Flat Bench Press",
            "Front Raise",
            "Incline Bench Press",
            "Lat Pressdown",
            "Lat Pulldown",
            "Lateral Raise",
            "Leg Curls",
            "Leg Extension",
            "Leg Press",
            "Overhead Press",
            "Pec Deck",
            "Pull up",
            "Rear Delt Fly",
            "Row",
            "Tricep Extension Cable",
            "Tricep Extension Dumbbell",
            "Tricep Pushdown",
            "Seated Cable Row",
            "Shoulder Press",
            "Shrugs",
            "Squats",
        )

        for (exerciseIndex in 0..exercises.size-1) {
            //lineEntries.add(Entry(0F,1F))
            var maxWeight = queryMaxObjectInRealm("activity",exercises[exerciseIndex]).toFloat()
            lineEntries.add(Entry(exerciseIndex.toFloat(),maxWeight))
            exercises[exerciseIndex] = exercises[exerciseIndex] + "\n" + maxWeight.toInt().toString()
        }

        lineDataSet = LineDataSet(lineEntries,"")
        lineData = LineData(lineDataSet)
        cubicChart.setData(lineData)
        cubicChart.xAxis.setDrawGridLines(false)
        cubicChart.setBackgroundColor(Color.TRANSPARENT)
        cubicChart.description.text = " "

        lineDataSet.setColors(resources.getColor(R.color.sun_set_3));
        lineDataSet.setValueTextColor(resources.getColor(R.color.white))
        lineDataSet.fillColor = resources.getColor(R.color.sun_set_3)
        lineDataSet.setCircleColor(resources.getColor(R.color.sun_set_3))
        lineDataSet.valueTextSize = 0f
        lineDataSet.setDrawFilled(true)
//        lineDataSet.setDrawValues(false)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawVerticalHighlightIndicator(false)

        val marker = CustomMarkerView(this, R.layout.custom_marker_view, exercises)
        cubicChart.marker = marker //when tap on line chart show the exercise"
        cubicChart.xAxis.setDrawLabels(false)

        println("---")
        println(lineDataSet.values)
        println(lineDataSet.values::class.simpleName)
        println("---")
    }

    private fun radarChart(radarChart: RadarChart, data: ArrayList<Int>, lastMonthsData: ArrayList<Int>) {
        var entries: ArrayList<RadarEntry> = ArrayList<RadarEntry>()
        var lastMonthsEntries: ArrayList<RadarEntry> = ArrayList<RadarEntry>()
        for (i in data) {
            entries.add(RadarEntry(i.toFloat()))
        }
        for (i in lastMonthsData) {
            lastMonthsEntries.add(RadarEntry(i.toFloat()))
        }
        var radarDataSet = RadarDataSet(entries, " ")
//        radarDataSet.setColors(Color.rgb(248,177,149))
        radarDataSet.setColors(resources.getColor(R.color.sun_set_2))
        radarDataSet.setLineWidth(2f)
        radarDataSet.setValueTextColor(resources.getColor(R.color.white))
        radarDataSet.setValueTextSize(0f)

        var radarDataSet2 = RadarDataSet(lastMonthsEntries," ")
        radarDataSet2.setColors(resources.getColor(R.color.sun_set_4))
        radarDataSet2.setLineWidth(2f)
        radarDataSet2.setValueTextColor(resources.getColor(R.color.white))
        radarDataSet2.setValueTextSize(0f)

        var radarData: RadarData = RadarData()
        radarData.addDataSet(radarDataSet)
        radarData.addDataSet(radarDataSet2)

        var labels: ArrayList<String> = ArrayList<String>()
        labels.add("Arms")
        labels.add("Back")
        labels.add("Chest")
        labels.add("Legs")
        labels.add("Push")
        labels.add("Pull")
        var xAxis: XAxis = radarChart.getXAxis()
        xAxis.setValueFormatter(IndexAxisValueFormatter(labels))
        radarChart.getDescription().setText("")
        radarChart.scaleX = 1.2f
        radarChart.scaleY = 1.2f
        radarChart.setExtraOffsets(0F,20F,0F,0F)
        radarChart.xAxis.textColor = resources.getColor(R.color.white)
        radarChart.yAxis.textColor = resources.getColor(R.color.white)//Color.rgb(246,114,128)
//        radarChart.legend.textColor = Color.rgb(246,114,128)
        radarChart.setData(radarData)
    }

    private fun exerciseTypeTotalCountForThisMonthAndLast(radarChart: RadarChart) {
        val EXERCISE_TYPES = arrayListOf<String>("Arms","Back","Chest","Legs","Push","Pull")
        var exerciseTypesCountThisMonth = arrayListOf<Int>()
        val exerciseTypesCountLastMonth = arrayListOf<Int>()

        val defaultZoneId = ZoneId.systemDefault()
        val date = Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1 //Not sure why Calendar.Month returns 1 month less
        val firstOfThisMonth = Date.from(LocalDate.of(year,month,1).atStartOfDay(defaultZoneId).toInstant())
        val lastDayOfThisMonth = Date.from(LocalDate.of(year,month,date.getActualMaximum(Calendar.DAY_OF_MONTH)).atStartOfDay(defaultZoneId).toInstant())

        val lastMonth = date.get(Calendar.MONTH)
        val lastMonthLength = LocalDate.of(year,lastMonth,1).lengthOfMonth()
        val firstOfLastMonth = Date.from(LocalDate.of(year,lastMonth,1).atStartOfDay(defaultZoneId).toInstant())
        val lastDayofLastMonth = Date.from(LocalDate.of(year,lastMonth,lastMonthLength).atStartOfDay(defaultZoneId).toInstant())

        for (exerciseType in EXERCISE_TYPES) {
            exerciseTypesCountThisMonth.add(queryObjectInRealmInMonth(firstOfThisMonth,lastDayOfThisMonth,"exerciseType",exerciseType).size)
            exerciseTypesCountLastMonth.add(queryObjectInRealmInMonth(firstOfLastMonth,lastDayofLastMonth,"exerciseType",exerciseType).size)
        }
        radarChart(radarChart,exerciseTypesCountThisMonth, exerciseTypesCountLastMonth)
    }

    private fun exerciseTypeTotalCount(radarChart: RadarChart) {
        val EXERCISE_TYPES = arrayListOf<String>("Arms","Back","Chest","Legs","Push","Pull")
        var exerciseTypesCount = arrayListOf<Int>()
        for (exerciseType in EXERCISE_TYPES) {
            exerciseTypesCount.add(queryObjectInRealm("exerciseType",exerciseType).size)
        }
        radarChart(radarChart,exerciseTypesCount, arrayListOf())

//        var entries: ArrayList<RadarEntry> = ArrayList<RadarEntry>();
//        entries.add(RadarEntry(220F))
//        entries.add(RadarEntry(330F))
//        entries.add(RadarEntry(440F))
//        entries.add(RadarEntry(550F))
//        entries.add(RadarEntry(660F))
//        entries.add(RadarEntry(210F))
//        var radarDataSet = RadarDataSet(entries,"Entry 1")
//        radarDataSet.setColors(Color.rgb(248,177,149))
//        radarDataSet.setLineWidth(2f)
//        radarDataSet.setValueTextColor(Color.rgb(248,177,149))
//        radarDataSet.setValueTextSize(0f)
////entry 2
//        var entries2: ArrayList<RadarEntry> = ArrayList<RadarEntry>();
//        entries2.add(RadarEntry(120F));
//        entries2.add(RadarEntry(310F));
//        entries2.add(RadarEntry(340F));
//        entries2.add(RadarEntry(450F));
//        entries2.add(RadarEntry(560F));
//        entries2.add(RadarEntry(310F));
//        var radarDataSet2 = RadarDataSet(entries2,"Entry 2");
//        radarDataSet2.setColors(Color.rgb(246,114,128));
//        radarDataSet2.setLineWidth(2f);
//        radarDataSet2.setValueTextColor(Color.rgb(246,114,128));
//        radarDataSet2.setValueTextSize(0f);
////set the data
//        var radarData: RadarData = RadarData()
//        radarData.addDataSet(radarDataSet)
//        radarData.addDataSet(radarDataSet2)
//
//        var labels: ArrayList<String> = ArrayList<String>()
//        labels.add("Arms")
//        labels.add("Back")
//        labels.add("Chest")
//        labels.add("Legs")
//        labels.add("Push")
//        labels.add("Pull")
//        var xAxis: XAxis = radarChart.getXAxis()
//        xAxis.setValueFormatter(IndexAxisValueFormatter(labels))
//        radarChart.getDescription().setText("")
//        radarChart.scaleX = 1.2f
//        radarChart.scaleY = 1.2f
//        radarChart.setExtraOffsets(0F,20F,0F,0F)
//        radarChart.xAxis.textColor = Color.rgb(246,114,128)
//        radarChart.yAxis.textColor = Color.rgb(246,114,128)
////        radarChart.legend.textColor = Color.rgb(246,114,128)
//        radarChart.setData(radarData)
    }

    private fun getStrengthToWeightRatio() {
        
    }

    private fun getNumberOfDaysExercised(allActivities: RealmResults<ExerciseModel>): String {
        var daysExercised = arrayListOf<Date>()
        for (activity in allActivities) {
            if (!(activity.dateDate in daysExercised)) {
                daysExercised.add(activity.dateDate!!)
            }
        }
        return daysExercised.size.toString()
    }

    private fun getNumberOfDaysExercisedThisMonth(monthStatus: String = "CURRENT"): String {
        val defaultZoneId = ZoneId.systemDefault()
        val date = Calendar.getInstance()
        var year = date.get(Calendar.YEAR)
        var month = date.get(Calendar.MONTH) + 1 //Not sure why Calendar.Month returns 1 month less
        if (monthStatus != "CURRENT") {
            month -= 1
            if (month == 0) {
                month = 12
                year -= 1
            }
        }

        val monthLength = LocalDate.of(year,month,1).lengthOfMonth()
        val firstOfThisMonth = Date.from(LocalDate.of(year,month,1).atStartOfDay(defaultZoneId).toInstant())
        val lastDayOfThisMonth = Date.from(LocalDate.of(year,month,monthLength).atStartOfDay(defaultZoneId).toInstant())
        var daysExercisedThisMonth = arrayListOf<Date>()

        for (activity in queryObjectInRealmInMonth(firstOfThisMonth,lastDayOfThisMonth)) {
            if (activity.dateDate !in daysExercisedThisMonth) {
                daysExercisedThisMonth.add(activity.dateDate!!)
            }
        }
        return daysExercisedThisMonth.size.toString()

    }

    private fun displayCalenderView() {
        val intent = Intent(this, CalenderView::class.java)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}