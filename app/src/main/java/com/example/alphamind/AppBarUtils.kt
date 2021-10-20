package com.example.alphamind

import com.google.android.material.appbar.MaterialToolbar

class AppBarUtils (topAppBar: MaterialToolbar){

    private val saveLog = topAppBar.menu.findItem(R.id.save_log)
    private val toChartView = topAppBar.menu.findItem(R.id.charts)
    private val armsFilter = topAppBar.menu.findItem(R.id.arms_filter)
    private val backFilter = topAppBar.menu.findItem(R.id.back_filter)
    private val chestFilter = topAppBar.menu.findItem(R.id.chest_filter)
    private val legsFilter = topAppBar.menu.findItem(R.id.legs_filter)
    private val pullFilter = topAppBar.menu.findItem(R.id.pull_filter)
    private val pushFilter = topAppBar.menu.findItem(R.id.push_filter)
    private val noFilter = topAppBar.menu.findItem(R.id.all)
    private val deleteSingle = topAppBar.menu.findItem(R.id.delete_one)
    private val deleteAll = topAppBar.menu.findItem(R.id.delete_all)
    private val saveToCloud = topAppBar.menu.findItem(R.id.save_to_firestore)

    fun setUpDefaultAppBar()  {
        saveLog.isVisible = false
        toChartView.isVisible = false
        armsFilter.isVisible = false
        backFilter.isVisible = false
        chestFilter.isVisible = false
        legsFilter.isVisible = false
        pullFilter.isVisible = false
        pushFilter.isVisible = false
        noFilter.isVisible = false
        deleteSingle.isVisible = false
        deleteAll.isVisible = false
        saveToCloud.isVisible = false
    }

    fun setUpAppBarWithoutFilter() {
        armsFilter.isVisible = false
        backFilter.isVisible = false
        chestFilter.isVisible = false
        legsFilter.isVisible = false
        pullFilter.isVisible = false
        pushFilter.isVisible = false
        noFilter.isVisible = false
    }

}