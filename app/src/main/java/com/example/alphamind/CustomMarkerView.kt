package com.example.alphamind

import android.content.Context
import android.os.DropBoxManager
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

//class CustomMarkerView {
//}

class CustomMarkerView(
    context: Context,
    layout: Int,
//    private val dataToDisplay: MutableList<Float>
    private val dataToDisplay: List<String>
) : MarkerView(context, layout) {

    private var txtViewData: TextView? = null

    init {
        txtViewData = findViewById(R.id.txtViewData)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        try {
            val xAxis = e?.x?.toInt() ?: 0
            txtViewData?.text = dataToDisplay[xAxis].toString()
        } catch (e: IndexOutOfBoundsException) { }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}