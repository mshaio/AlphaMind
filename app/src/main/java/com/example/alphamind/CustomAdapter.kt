package com.example.alphamind

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.alphamind.R.color.brown_200
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

internal class CustomAdapter(private var mContext: Context, private var itemsList: List<String>, private var dateList: List<String>, private var selectionList: MutableList<Boolean>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.exercise_type)
        var dateTextView: TextView = view.findViewById(R.id.date)
        var selectedItem: SwitchMaterial = view.findViewById(R.id.unique_item)
        var itemLayout: LinearLayout = view.findViewById(R.id.item)
        var exerciseIconImageView: ImageView = view.findViewById(R.id.exercise_icon)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val exerciseDate: String = dateList[position]
                val todaysExercise: String = itemsList[position]
                var dateDate: String = dateFormatter((exerciseDate))
                println(position)
                println("LKJHGFDS")
                println(stringToDate(dateFormatter((exerciseDate))))
//                mContext.startActivity(Intent(mContext, ExerciseSettingsActivity::class.java))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    itemView.setBackgroundColor(Color.rgb(166,166,166))
                }

                var intent = Intent(mContext, ExerciseSettingsActivity::class.java)
                intent.putExtra("date", exerciseDate)
                intent.putExtra("dateDate", dateDate)
                intent.putExtra("exercise", todaysExercise)
                intent.putExtra("selected", position)
                mContext.startActivity(intent)
            }

            selectedItem.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                selectionList[position] = selectedItem.isChecked
            }
        }
    }

    private fun dateFormatter(date: String): String {
        //Converts dd-MM-yyyy to yyyy-MM-dd
        var yearMonthDay: String = ""
        for (d in date.split('-').reversed()) {
            yearMonthDay += d
            yearMonthDay += "-"
        }
        return yearMonthDay.dropLast(1)
    }

    private fun stringToDate(date: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.parse(date)
    }
//    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
//    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        var itemTextView: TextView = view.findViewById(R.id.exercise_type)
//        var dateTextView: TextView = view.findViewById(R.id.date)
//    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.individual_exercise_item, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        println("AAA")
//        println(itemsList)
//        println(position)
//        println("BBB")
        val item = itemsList[position]
        holder.itemTextView.text = item
        holder.dateTextView.text =  dateList[position]
//        holder.itemLayout.setBackgroundColor(Color.rgb(166,166,166))

        val exerciseType = holder.itemTextView.text
        when (exerciseType) {
//            "Arms" -> holder.itemTextView.setTextColor(Color.rgb(246,114,128))
//            "Back" -> holder.itemTextView.setTextColor(Color.rgb(248,177,149))
//            "Chest" -> holder.itemTextView.setTextColor(Color.rgb(192,108,132))
//            "Legs" -> holder.itemTextView.setTextColor(Color.rgb(53,92,125))
//            "Arms" -> holder.itemLayout.setBackgroundColor(Color.rgb(246,114,128))

//            "Back" -> holder.exerciseIconImageView.setBackgroundColor(Color.rgb(248,177,149))
//            "Chest" -> holder.exerciseIconImageView.setBackgroundColor(Color.rgb(192,108,132))
//            "Legs" -> holder.exerciseIconImageView.setBackgroundColor(Color.rgb(53,92,125))
//            "Arms" -> holder.exerciseIconImageView.setBackgroundColor(Color.rgb(246,114,128))

            "Arms" -> holder.exerciseIconImageView.setImageResource(R.drawable.ic_baseline_fitness_center_24_sunset2)
            "Back" -> holder.exerciseIconImageView.setImageResource(R.drawable.ic_baseline_fitness_center_24_sunset1)
            "Chest" -> holder.exerciseIconImageView.setImageResource(R.drawable.ic_baseline_fitness_center_24_sunset3)
            "Legs" -> holder.exerciseIconImageView.setImageResource(R.drawable.ic_baseline_fitness_center_24_sunset5)
        }

    }
    override fun getItemCount(): Int {
        return itemsList.size
    }
}