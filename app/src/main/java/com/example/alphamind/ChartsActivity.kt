package com.example.alphamind

import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.alphamind.databinding.ActivityChartsBinding

import com.example.alphamind.ExerciseSettingsActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.chip.Chip
import io.realm.Realm
import io.realm.RealmResults
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

//import kotlin.math

class ChartsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityChartsBinding
//    private lateinit var dataForBarChart: MutableMap<String, Int>
    private var dataFromBarChart = arrayListOf<Int>()
    private var datesFromBarChart = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)
        window.statusBarColor = ContextCompat.getColor(this, R.color.brown_300)
        window.decorView.setBackgroundColor(resources.getColor(R.color.brown_300))
        window.navigationBarColor = ContextCompat.getColor(this, R.color.brown_300)
        var topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        AppBarUtils(topAppBar).setUpDefaultAppBar()

        val exerciseDate = intent.getStringExtra("date")
        val totalExerciseVolume = intent.getStringExtra("totalVolume")
        val dateDate = intent.getStringExtra("dateDate")

        var realDate: Date = stringToDate(dateDate)

        val chart_one: CircularProgressIndicator = findViewById(R.id.chart_one)
        val chart_two: CircularProgressIndicator = findViewById(R.id.chart_two)
        val chart_three: CircularProgressIndicator = findViewById(R.id.chart_three)
        val chart_four: CircularProgressIndicator = findViewById(R.id.chart_four_)
        val chart_five: CircularProgressIndicator = findViewById(R.id.chart_five)

        val chip_one: Chip = findViewById<Chip>(R.id.activity_one)
        val chip_two: Chip = findViewById<Chip>(R.id.activity_two)
        val chip_three: Chip = findViewById<Chip>(R.id.activity_three)
        val chip_four: Chip = findViewById<Chip>(R.id.activity_four)
        val chip_five: Chip = findViewById<Chip>(R.id.activity_five)

        val activityTextViewOne: TextView = findViewById(R.id.text_view_act_one)
        val activityTextViewTwo: TextView = findViewById(R.id.text_view_act_two)
        val activityTextViewThree: TextView = findViewById(R.id.text_view_act_three)
        val activityTextViewFour: TextView = findViewById(R.id.text_view_act_four)
        val activityTextViewFive: TextView = findViewById(R.id.text_view_act_five)

        val dayExerciseVolume: TextView = findViewById(R.id.day_stats_volume)

        val tv_today_best_one: TextView = findViewById(R.id.tv_today_best_one)
        val tv_today_best_two: TextView = findViewById(R.id.tv_today_best_two)
        val tv_today_best_three: TextView = findViewById(R.id.tv_today_best_three)
        val tv_today_best_four: TextView = findViewById(R.id.tv_today_best_four)
        val tv_today_best_five: TextView = findViewById(R.id.tv_today_best_five)
        var tv_group_todays_best = arrayListOf<TextView>()
        tv_group_todays_best.add(tv_today_best_one)
        tv_group_todays_best.add(tv_today_best_two)
        tv_group_todays_best.add(tv_today_best_three)
        tv_group_todays_best.add(tv_today_best_four)
        tv_group_todays_best.add(tv_today_best_five)

        val tv_personal_best: TextView = findViewById(R.id.tv_personal_best)

        dayExerciseVolume.text = totalExerciseVolume
//        dayExerciseVolume.setTextColor(Color.parseColor("#A92A04"))
        dayExerciseVolume.setTypeface(null,Typeface.BOLD)
        chart_one.progress = 0
        chart_two.progress = 0
        chart_three.progress = 0
        chart_four.progress = 0
        chart_five.progress = 0

        chip_one.isInvisible = true
        chip_two.isInvisible = true
        chip_three.isInvisible = true
        chip_four.isInvisible = true
        chip_five.isInvisible = true

        var exerciseVolumeMap = mutableMapOf<String,Int>()

        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).equalTo("date", exerciseDate).findAll()
        lateinit var exerciseType: String

        for (activity in activities) {
            exerciseType = activity.exerciseType
            if (!exerciseVolumeMap.containsKey(activity.activity)) {
                exerciseVolumeMap[activity.activity] = activity.weights * activity.reps
            } else {
                exerciseVolumeMap[activity.activity] = exerciseVolumeMap[activity.activity]!! + activity.weights * activity.reps
            }
        }

        println("{{{")
        println(exerciseVolumeMap)
        println("}}}")
        var exerciseVolume = arrayListOf<Int>()
        var sortedExerciseName = arrayListOf<String>()
        var totalVolume: Float = 0f

        for ((k, v) in exerciseVolumeMap) {
            exerciseVolume.add(v)
            totalVolume += v
        }

        exerciseVolume.sort()

        for (i in exerciseVolume) {
            for ((k,v) in exerciseVolumeMap) {
                if (i == v)  {
                    sortedExerciseName.add(k)
                    break
                }
            }
        }
        println("$$$")
        println(exerciseVolume)
        println(sortedExerciseName)
        println("$$$")

        if (exerciseVolume.size >= 1) {
//            chart_one.progress = (exerciseVolume[0]/totalVolume * 100).toInt()
            chart_one.progress = 100
            chip_one.text = "%.2f".format((exerciseVolume[0]/totalVolume * 100)).toString() + " %"
            activityTextViewOne.text = sortedExerciseName[0]
            chip_one.isVisible = true
            chip_one.setChipBackgroundColorResource(R.color.sun_set_1)
        }
        if (exerciseVolume.size >= 2) {
//            chart_two.progress = (exerciseVolume[1]/totalVolume * 100).toInt()
            chart_two.progress = chart_one.progress - (exerciseVolume[0]/totalVolume * 100).toInt()
            chip_two.text = "%.2f".format((exerciseVolume[1]/totalVolume * 100)).toString() + " %"
            activityTextViewTwo.text = sortedExerciseName[1]
            chip_two.isVisible = true
            chip_two.setChipBackgroundColorResource(R.color.sun_set_2)
        }
        if (exerciseVolume.size >= 3) {
//            chart_three.progress = (exerciseVolume[2]/totalVolume * 100).toInt()
            chart_three.progress = chart_two.progress - (exerciseVolume[1]/totalVolume * 100).toInt()
            chip_three.text = "%.2f".format((exerciseVolume[2]/totalVolume * 100)).toString() + " %"
            activityTextViewThree.text = sortedExerciseName[2]
            chip_three.isVisible = true
            chip_three.setChipBackgroundColorResource(R.color.sun_set_3)
        }
        if (exerciseVolume.size >= 4) {
//            chart_four.progress = (exerciseVolume[3]/totalVolume * 100).toInt()
            chart_four.progress = chart_three.progress - (exerciseVolume[2]/totalVolume * 100).toInt()
            chip_four.text = "%.2f".format((exerciseVolume[3]/totalVolume * 100)).toString() + " %"
            activityTextViewFour.text = sortedExerciseName[3]
            chip_four.isVisible = true
            chip_four.setChipBackgroundColorResource(R.color.sun_set_4)
        }
        if (exerciseVolume.size >= 5) {
//            chart_five.progress = (exerciseVolume[5]/totalVolume * 100).toInt()
            chart_five.progress = chart_four.progress - (exerciseVolume[3]/totalVolume * 100).toInt()
            chip_five.text = "%.2f".format((exerciseVolume[4]/totalVolume * 100)).toString() + " %"
            activityTextViewFive.text = sortedExerciseName[4]
            chip_five.isVisible = true
            chip_five.setChipBackgroundColorResource(R.color.sun_set_5)
        }

        println(getTodaysBests(exerciseDate))
        var todaysBests: MutableMap<String,Int> = getTodaysBests(exerciseDate)
        var personalBests: MutableMap<String,Int> = getPersonalBests(exerciseType)

        for ((exerciseItem, heaviestWeight) in todaysBests) {
            tv_today_best_one.text = tv_today_best_one.text.toString() + exerciseItem + ": " + heaviestWeight.toString() + " lbs" + "\n"
        }

        for ((exerciseItem, heaviestWeight) in personalBests) {
            tv_personal_best.text = tv_personal_best.text.toString() + exerciseItem + ": " + heaviestWeight.toString() + " lbs" + "\n"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println(getLastSameSevenExercises(exerciseType, realDate))
            var dataForBarChart: MutableMap<String, Int> =
                getLastSameSevenExercises(exerciseType, realDate)

//            var dataFromBarChart = arrayListOf<Int>()
            for ((k,v) in dataForBarChart) {
                dataFromBarChart.add(v)
                datesFromBarChart.add(k)
            }

            val entries: ArrayList<BarEntry> = ArrayList()
//            entries.add(BarEntry(1f, dataForBarChart["29-8-2021"]!!.toFloat()))
//            entries.add(BarEntry(2f, 100f))
//            entries.add(BarEntry(3f, 200f))
//            entries.add(BarEntry(4f, 150f))
//            entries.add(BarEntry(5f, 130f))
//            entries.add(BarEntry(6f, 200f))
            println("@AA@")
            println(dataFromBarChart)
            println(datesFromBarChart)
            println("@BB@")

            val dataForBarChartSize: Int = dataForBarChart.size
            for (i in 0..dataForBarChartSize-1) {
                entries.add(BarEntry(i+1.toFloat(), dataFromBarChart[i].toFloat()))
            }

            val barDataSet = BarDataSet(entries, "")
//            barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

            var colorTestList: MutableList<Int> = mutableListOf()
            colorTestList.add(resources.getColor(R.color.sun_set_1))
            colorTestList.add(resources.getColor(R.color.sun_set_2))
            colorTestList.add(resources.getColor(R.color.sun_set_3))
            colorTestList.add(resources.getColor(R.color.sun_set_4))
            colorTestList.add(resources.getColor(R.color.sun_set_5))
            barDataSet.setColors(colorTestList)

            val data = BarData(barDataSet)
            val barChart = findViewById<BarChart>(R.id.barChart)
            barChart.data = data

            val xAxis: XAxis = barChart.xAxis
            //hide grid lines
            barChart.axisLeft.setDrawGridLines(false)
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.setDrawAxisLine(false)

            //remove right y-axis
            barChart.axisRight.isEnabled = false

            //remove legend
            barChart.legend.isEnabled = false


            //remove description label
            barChart.description.isEnabled = false


            //add animation
            barChart.animateY(3000)

//            // to draw label on xAxis
//            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
//            xAxis.valueFormatter = MyAxisFormatter()
//            xAxis.setDrawLabels(true)
//            xAxis.granularity = 1f
//            xAxis.labelRotationAngle = +90f

            //draw chart
            barChart.invalidate()
        }
    }

//    inner class MyAxisFormatter : IndexAxisValueFormatter() {
//
//        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            val index = value.toInt()-1
////            Log.d(TAG, "getAxisLabel: index $index")
//            return if (index < dataFromBarChart.size) {
//                datesFromBarChart[index]
//            } else {
//                ""
//            }
//        }
//    }

    private fun stringToDate(date: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.parse(date)
    }

    private fun computeBests(activities: RealmResults<ExerciseModel>, exerciseWeightMap: MutableMap<String,Int>): MutableMap<String,Int> {
        for (activity in activities) {
            if (!exerciseWeightMap.containsKey(activity.activity)) {
                exerciseWeightMap[activity.activity] = activity.weights
            } else {
                print("!@!@")
                if (exerciseWeightMap[activity.activity]!! < activity.weights)
                    exerciseWeightMap[activity.activity] = activity.weights
            }
        }
        return exerciseWeightMap
    }

    private fun initialiseRealmDB(): Realm {
        Realm.init(this)
        return Realm.getDefaultInstance()
    }

    private fun getTodaysBests(date:String): MutableMap<String,Int> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).equalTo("date", date).findAll()
        var exerciseWeightMap = mutableMapOf<String,Int>()
        return computeBests(activities,exerciseWeightMap)
    }

    private fun getPersonalBests(exerciseType: String):MutableMap<String,Int> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).equalTo("exerciseType", exerciseType).findAll()
        var exerciseWeightMap = mutableMapOf<String,Int>()
        return computeBests(activities,exerciseWeightMap)
    }

    private fun getPastDate(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -daysAgo)
        return calendar.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLastSameSevenExercises(exerciseType: String, exerciseDate: Date): MutableMap<String,Int> {
//        val currentDateTime = LocalDateTime.now()
//        val todaysDate = currentDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        val realm: Realm = initialiseRealmDB()
        val samePastSevenExercises = realm.where(ExerciseModel::class.java).equalTo("exerciseType", exerciseType).lessThanOrEqualTo("dateDate",exerciseDate).findAll()
        var weightMapByDate: MutableMap<String,Int> = mutableMapOf()
        for (pastExercise in samePastSevenExercises) {
            if (!weightMapByDate.containsKey(pastExercise.date)) {
                weightMapByDate[pastExercise.date.toString()] = pastExercise.weights * pastExercise.reps
            } else {
                weightMapByDate[pastExercise.date.toString()] = weightMapByDate[pastExercise.date.toString()]!! + pastExercise.weights * pastExercise.reps
            }
        }
        return weightMapByDate
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_charts)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}