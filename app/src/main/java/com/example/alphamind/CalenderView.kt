package com.example.alphamind

//import android.R

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.navigation.ui.AppBarConfiguration
import com.example.alphamind.databinding.ActivityCalenderViewBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList



class CalenderView : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCalenderViewBinding

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.autumn_dark_1)

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(2)
        val lastMonth = currentMonth.plusMonths(2)
//        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val datesWithExercises: MutableMap<String, ArrayList<LocalDate>> = getDatesWithExercises()


        setContentView(R.layout.activity_calender_view)
        var calendarView: com.kizitonwose.calendarview.CalendarView = findViewById(R.id.calendarView)
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                val date = Calendar.getInstance()
                val year = date.get(Calendar.YEAR)
                val month = date.get(Calendar.MONTH) + 1 //Not sure why Calendar.Month returns 1 month less
                val dayDay = date.get(Calendar.DAY_OF_MONTH)
                val myldt = LocalDate.of(year,month,dayDay)

                for ((exerciseType, exerciseDate) in datesWithExercises) {
                    for (date in exerciseDate)
                        if (day.date.equals(date)) {
                            when (exerciseType)  {
                                "Arms" -> container.textView.setBackgroundResource(R.drawable.calendar_arms_bg)
                                "Back" -> container.textView.setBackgroundResource(R.drawable.calendar_back_bg)
                                "Chest" -> container.textView.setBackgroundResource(R.drawable.calendar_chest_bg)
                                "Legs" -> container.textView.setBackgroundResource(R.drawable.calendar_legs_bg)
                            }
                        }
                }
                container.textView.isInvisible = day.owner != DayOwner.THIS_MONTH

                if (day.date.equals(myldt)) {
                    container.textView.setTextColor(R.color.purple_700)
                    container.textView.setBackgroundResource(R.drawable.calendar_today_bg)
//                    container.textView.setBackgroundColor(R.color.purple_500)
                }

            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
            }
        }

        val daysOfWeek = arrayOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )


        calendarView.setup(firstMonth, lastMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)
    }

    private fun queryObjectInRealm(): RealmResults<ExerciseModel> {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val activities = realm.where(ExerciseModel::class.java).sort("date", Sort.DESCENDING).findAll()
        return activities
    }

    private fun stringToDate(date: String): LocalDate {
        //eg: 21-8-2021 (String) -> 2021-8-21 (Date)
        val parsedDate = date.split('-')
        val result = date.split('-')[2] + '-' + date.split('-')[1] + '-' + date.split('-')[0]
        return LocalDate.of(parsedDate[2].toInt(), parsedDate[1].toInt(), parsedDate[0].toInt())

    }

    private fun getDatesWithExercises(): MutableMap<String, ArrayList<LocalDate>> {
        var datesWithActivities: MutableMap<String, ArrayList<LocalDate>> = mutableMapOf()
        for (activity in queryObjectInRealm()) {
            if (!datesWithActivities.containsKey(activity.exerciseType)) {
                datesWithActivities[activity.exerciseType] = arrayListOf()
                datesWithActivities[activity.exerciseType]!!.add(stringToDate(activity.date!!))
            } else if (!datesWithActivities[activity.exerciseType]!!.contains(stringToDate(activity.date!!))) {
                datesWithActivities[activity.exerciseType]!!.add(stringToDate(activity.date!!))
            }
        }
        return datesWithActivities
    }

}

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.headerTextView)
}