package com.example.alphamind

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.chip.Chip
import io.realm.Realm
import io.realm.RealmResults

//import kotlin.math

class ChartsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityChartsBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)
        window.statusBarColor = ContextCompat.getColor(this, R.color.autumn_dark_1)

        val exerciseDate = intent.getStringExtra("date")
        val totalExerciseVolume = intent.getStringExtra("totalVolume")

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

        dayExerciseVolume.text = totalExerciseVolume
//        dayExerciseVolume.setTextColor(Color.parseColor("#A92A04"))
        dayExerciseVolume.setTypeface(null,Typeface.BOLD)
        chart_one.progress = 0
        chart_two.progress = 0
        chart_three.progress = 0
        chart_four.progress = 0
        chart_five.progress = 0

//        chip_one.setChipBackgroundColorResource(R.color.black)
//        chip_two.setChipBackgroundColorResource(R.color.black)
//        chip_three.setChipBackgroundColorResource(R.color.black)
//        chip_four.setChipBackgroundColorResource(R.color.black)
//        chip_five.setChipBackgroundColorResource(R.color.black)

        chip_one.isInvisible = true
        chip_two.isInvisible = true
        chip_three.isInvisible = true
        chip_four.isInvisible = true
        chip_five.isInvisible = true

        var exerciseVolumeMap = mutableMapOf<String,Int>()

        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).equalTo("date", exerciseDate).findAll()

        for (activity in activities) {
            if (!exerciseVolumeMap.containsKey(activity.activity)) {
                exerciseVolumeMap[activity.activity] = activity.weights * activity.reps
            } else {
                print("!@!@")
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
            chip_one.setChipBackgroundColorResource(R.color.teal_200)
        }
        if (exerciseVolume.size >= 2) {
//            chart_two.progress = (exerciseVolume[1]/totalVolume * 100).toInt()
            chart_two.progress = chart_one.progress - (exerciseVolume[0]/totalVolume * 100).toInt()
            chip_two.text = "%.2f".format((exerciseVolume[1]/totalVolume * 100)).toString() + " %"
            activityTextViewTwo.text = sortedExerciseName[1]
            chip_two.isVisible = true
            chip_two.setChipBackgroundColorResource(R.color.autumn_light)
        }
        if (exerciseVolume.size >= 3) {
//            chart_three.progress = (exerciseVolume[2]/totalVolume * 100).toInt()
            chart_three.progress = chart_two.progress - (exerciseVolume[1]/totalVolume * 100).toInt()
            chip_three.text = "%.2f".format((exerciseVolume[2]/totalVolume * 100)).toString() + " %"
            activityTextViewThree.text = sortedExerciseName[2]
            chip_three.isVisible = true
            chip_three.setChipBackgroundColorResource(R.color.autumn_dark)
        }
        if (exerciseVolume.size >= 4) {
//            chart_four.progress = (exerciseVolume[3]/totalVolume * 100).toInt()
            chart_four.progress = chart_three.progress - (exerciseVolume[2]/totalVolume * 100).toInt()
            chip_four.text = "%.2f".format((exerciseVolume[3]/totalVolume * 100)).toString() + " %"
            activityTextViewFour.text = sortedExerciseName[3]
            chip_four.isVisible = true
            chip_four.setChipBackgroundColorResource(R.color.autumn_dark_1)
        }
        if (exerciseVolume.size >= 5) {
//            chart_five.progress = (exerciseVolume[5]/totalVolume * 100).toInt()
            chart_five.progress = chart_four.progress - (exerciseVolume[3]/totalVolume * 100).toInt()
            chip_five.text = "%.2f".format((exerciseVolume[4]/totalVolume * 100)).toString() + " %"
            activityTextViewFive.text = sortedExerciseName[4]
            chip_five.isVisible = true
            chip_five.setChipBackgroundColorResource(R.color.teal_700)
        }



        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(1f, 4f))
        entries.add(BarEntry(2f, 10f))
        entries.add(BarEntry(3f, 2f))
        entries.add(BarEntry(4f, 15f))
        entries.add(BarEntry(5f, 13f))
        entries.add(BarEntry(6f, 2f))

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = BarData(barDataSet)
        val barChart = findViewById<BarChart>(R.id.barChart)
        barChart.data = data


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


        //draw chart
        barChart.invalidate()





//        binding = ActivityChartsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_charts)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_charts)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}