package com.example.alphamind

import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class dashboard : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.autumn_dark_1)
        }
        var radarChart: RadarChart = findViewById(R.id.radar_chart)
        var radarChart2: RadarChart = findViewById(R.id.radar_chart2)
        exerciseTypeTotalCount(radarChart)
        exerciseTypeTotalCountForThisMonth(radarChart2)

        var cubicChart: LineChart = findViewById(R.id.cubic_chart)
        cubicChart(cubicChart)
    }

    private fun queryObjectInRealm(queryField: String, queryValue: String = ""): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).sort("date", Sort.DESCENDING).equalTo(queryField,queryValue).findAll()
        return activities
    }

    private fun queryObjectInRealmInMonth(startOfMonth: Date, endOfMonth: Date, queryField: String, queryValue: String): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).greaterThanOrEqualTo("dateDate",startOfMonth).lessThan("dateDate",endOfMonth).equalTo(queryField,queryValue).findAll()
        return activities
    }

    private fun cubicChart(cubicChart: LineChart) {
        lateinit var lineData: LineData
        lateinit var lineDataSet: LineDataSet
        var lineEntries: ArrayList<Entry> = ArrayList<Entry>()
        lineEntries.add(Entry(0F,1F))
        lineEntries.add(Entry(1F,2F))
        lineEntries.add(Entry(2F,1F))
        lineEntries.add(Entry(4F,3F))
        lineEntries.add(Entry(5F,2F))

        lineDataSet = LineDataSet(lineEntries,"")
        lineData = LineData(lineDataSet)
        cubicChart.setData(lineData)
        cubicChart.xAxis.setDrawGridLines(false)
        cubicChart.setBackgroundColor(Color.TRANSPARENT)
        cubicChart.description.text = " "

        lineDataSet.setColors(Color.rgb(53,92,125));
        lineDataSet.setValueTextColor(Color.CYAN)
        lineDataSet.valueTextSize = 14f
        lineDataSet.setDrawFilled(true)
//        lineDataSet.setDrawValues(false)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    }

    private fun radarChart(radarChart: RadarChart, data: ArrayList<Int>) {
        var entries: ArrayList<RadarEntry> = ArrayList<RadarEntry>()
        for (i in data) {
            entries.add(RadarEntry(i.toFloat()))
        }
        var radarDataSet = RadarDataSet(entries, " ")
        radarDataSet.setColors(Color.rgb(248,177,149))
        radarDataSet.setLineWidth(2f)
        radarDataSet.setValueTextColor(Color.rgb(248,177,149))
        radarDataSet.setValueTextSize(0f)

        var radarData: RadarData = RadarData()
        radarData.addDataSet(radarDataSet)

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
        radarChart.xAxis.textColor = Color.rgb(246,114,128)
        radarChart.yAxis.textColor = Color.rgb(246,114,128)
//        radarChart.legend.textColor = Color.rgb(246,114,128)
        radarChart.setData(radarData)
    }

    private fun exerciseTypeTotalCountForThisMonth(radarChart: RadarChart) {
        val EXERCISE_TYPES = arrayListOf<String>("Arms","Back","Chest","Legs","Push","Pull")
        var exerciseTypesCountThisMonth = arrayListOf<Int>()

        val defaultZoneId = ZoneId.systemDefault()
        val date = Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1 //Not sure why Calendar.Month returns 1 month less
        val firstOfThisMonth = Date.from(LocalDate.of(year,month,1).atStartOfDay(defaultZoneId).toInstant())
        val lastDayOfThisMonth = Date.from(LocalDate.of(year,month,date.getActualMaximum(Calendar.DAY_OF_MONTH)).atStartOfDay(defaultZoneId).toInstant())

        for (exerciseType in EXERCISE_TYPES) {
            exerciseTypesCountThisMonth.add(queryObjectInRealmInMonth(firstOfThisMonth,lastDayOfThisMonth,"exerciseType",exerciseType).size)
        }
        radarChart(radarChart,exerciseTypesCountThisMonth)
    }

    private fun exerciseTypeTotalCount(radarChart: RadarChart) {
        val EXERCISE_TYPES = arrayListOf<String>("Arms","Back","Chest","Legs","Push","Pull")
        var exerciseTypesCount = arrayListOf<Int>()
        for (exerciseType in EXERCISE_TYPES) {
            exerciseTypesCount.add(queryObjectInRealm("exerciseType",exerciseType).size)
        }
        radarChart(radarChart,exerciseTypesCount)

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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}