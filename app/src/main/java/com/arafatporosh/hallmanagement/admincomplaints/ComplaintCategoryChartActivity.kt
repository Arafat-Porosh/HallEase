package com.arafatporosh.hallmanagement.admincomplaints

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arafatporosh.hallmanagement.AdminDashboard
import com.arafatporosh.hallmanagement.Complaints
import com.arafatporosh.hallmanagement.R
import com.google.firebase.database.*
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.formatter.ValueFormatter

class ComplaintCategoryChartActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_category_chart)

        database = FirebaseDatabase.getInstance().getReference("complaints")
        getAcceptedComplaints()
    }

    private fun getAcceptedComplaints() {
        val complaintsList = mutableListOf<Complaints>()

        database.orderByChild("status").equalTo("Accepted").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintsList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val complaint = data.getValue(Complaints::class.java)
                        if (complaint != null) {
                            complaintsList.add(complaint)
                        }
                    }
                    if (complaintsList.isNotEmpty()) {
                        val complaintsByCategory = countComplaintsByCategory(complaintsList)
                        val pieChart = findViewById<PieChart>(R.id.pieChart)
                        setupPieChart(complaintsByCategory, pieChart)
                    } else {
                        Toast.makeText(this@ComplaintCategoryChartActivity, "No accepted complaints found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ComplaintCategoryChartActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun countComplaintsByCategory(complaintsList: List<Complaints>): Map<String, Int> {
        return complaintsList.groupingBy { it.type }.eachCount()
    }

    private fun setupPieChart(complaintsByCategory: Map<String, Int>, pieChart: PieChart) {
        val entries = mutableListOf<PieEntry>()

        for ((category, count) in complaintsByCategory) {
            entries.add(PieEntry(count.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Complaint Categories")

        val aestheticColors = listOf(
            Color.parseColor("#FF6F61"),
            Color.parseColor("#A8D5BA"),
            Color.parseColor("#6EC1E4"),
            Color.parseColor("#B28DFF"),
            Color.parseColor("#FFDA6B")
        )

        dataSet.setColors(aestheticColors)

        val data = PieData(dataSet)

        pieChart.data = data

        pieChart.setUsePercentValues(false)

        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.TRANSPARENT)

        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 12f

        val countFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val category = entries.find { it.value == value }?.label
                val count = complaintsByCategory[category] ?: 0
                return count.toString()
            }
        }


        dataSet.valueFormatter = countFormatter

        pieChart.setDrawEntryLabels(true)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.setEntryLabelColor(Color.BLACK)

        pieChart.setExtraOffsets(20f, 20f, 20f, 20f)

        pieChart.invalidate()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ComplaintActivity::class.java)
        startActivity(intent)
        finish()
    }
}
