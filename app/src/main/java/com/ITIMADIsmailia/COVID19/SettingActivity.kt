package com.ITIMADIsmailia.COVID19

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.activity_setting.*
import kotlin.properties.Delegates

class SettingActivity : AppCompatActivity() {
    var times: Int = 0
    val TAG = "Time Picker Value"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        getCountryPrefs()
        time_picker.maxValue = 24
        time_picker.minValue = 1

        time_picker.setOnValueChangedListener { picker, oldVal, newVal -> times = newVal
            Log.d(TAG,"time$times")}

    }

    fun getCountryPrefs(){
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        country_name_sec.text = "Country Name: "+sharedPreference.getString("countryName","Unknown").toString()
        new_cases_sec.text = "New Cases: "+sharedPreference.getString("newCases","0").toString()
        total_sec.text = "Total: "+sharedPreference.getString("total","0").toString()
        recovered_sec.text = "Recovery: "+sharedPreference.getString("recovered","0").toString()
        deaths_sec.text ="Deaths: "+sharedPreference.getString("deaths","0").toString()
    }
}
