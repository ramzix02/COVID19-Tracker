package com.ITIMADIsmailia.COVID19

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat
import com.ITIMADIsmailia.COVID19.workmanagertask.MyJobService
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity() : ScopedActivity(),KodeinAware{

    override val kodein by closestKodein()
    private val viewModelFactory: MainViewModelFactory by instance()
    private lateinit var viewModel: MainViewModel
    private var times: Int  = 1
    private lateinit var scheduler: JobScheduler
    var TAG = "MainActivity"
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scheduler = this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        supportActionBar?.title = "COVID19 - Tracker"
        viewModel = ViewModelProviders.of(this,viewModelFactory)
            .get(MainViewModel::class.java)

        //** Set the colors of the Pull To Refresh View
        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        itemsswipetorefresh.setOnRefreshListener {
            buildUI()
            Toast.makeText(applicationContext,"Refreshed Successfully!",Toast.LENGTH_LONG).show()
            itemsswipetorefresh.isRefreshing = false
        }

        buildUI()
    }

    private fun buildUI() = launch(Dispatchers.Main) {
        //passing hours to fetch data after
        viewModel.hoursCount = 2
        val countryState = viewModel.countryStat.await()

        countryState.observe(this@MainActivity, androidx.lifecycle.Observer {
            if (it == null) return@Observer
            group_loading.visibility = View.GONE


            //print(it.toString())
            initRecyclerView(it.toItemModel())
        })
    }

    private fun List<UnitCountriesStat>.toItemModel(): List<ItemModel>{
        return this.map {
            ItemModel(it)
        }
    }

    private fun initRecyclerView(items: List<ItemModel>){
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }
        recyclerView.apply {
            //******************************************************************//
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = groupAdapter
        }
    }

    // Wagdy start
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater : MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.three_dots_main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // var bool : Boolean = false
        when(item.itemId){
            R.id.subscribe_item -> {Toast.makeText(this, "subscribe_item",Toast.LENGTH_SHORT).show()}
            R.id.every_hour_item -> scheduleJob(everyHour)
            R.id.every_2_hour_item -> scheduleJob(everyTwoHour)
            R.id.every_5_hour_item -> scheduleJob(everyFiveHour)
            R.id.daily_item -> scheduleJob(daily)
            R.id.cancel_update -> cancelJob()
//            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }


    fun scheduleJob(time:Int) {
        val componentName = ComponentName(this, MyJobService::class.java)
        val info = JobInfo.Builder(123, componentName)
            // .setRequiresCharging(true)
            // .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(time * 60 * 1000.toLong())
            .build()

        var scheduler =  this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
        val resultCode = scheduler!!.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled")
        } else {
            Log.d(TAG, "Job scheduling failed")
        }
    }

    fun cancelJob() {
        var scheduler =  this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
        scheduler!!.cancel(123)
        Log.d(TAG, "Job cancelled")
    }

    companion object {
        private const val everyHour : Int = 15
        private const val everyTwoHour :Int = 16
        private const val everyFiveHour : Int = 17
        private const val daily : Int = 18
    }
    //Wagdy end
}
