package com.example.alphamind

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

internal class CustomAdapter(private var mContext: Context, private var itemsList: List<String>, private var dateList: List<String>, private var selectionList: MutableList<Boolean>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.exercise_type)
        var dateTextView: TextView = view.findViewById(R.id.date)
        var selectedItem: SwitchMaterial = view.findViewById(R.id.unique_item)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val exerciseDate: String = dateList[position]
                val todaysExercise: String = itemsList[position]
                println(position)
                println("LKJHGFDS")
//                mContext.startActivity(Intent(mContext, ExerciseSettingsActivity::class.java))

                var intent = Intent(mContext, ExerciseSettingsActivity::class.java)
                intent.putExtra("date", exerciseDate)
                intent.putExtra("exercise", todaysExercise)
                intent.putExtra("selected", position)
                mContext.startActivity(intent)
            }

            selectedItem.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                println("@@@")
                selectionList[position] = selectedItem.isChecked
                println(selectionList)
                println("###")
            }
        }
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
        val item = itemsList[position]
        holder.itemTextView.text = item
        holder.dateTextView.text =  dateList[position]
    }
    override fun getItemCount(): Int {
        return itemsList.size
    }
}