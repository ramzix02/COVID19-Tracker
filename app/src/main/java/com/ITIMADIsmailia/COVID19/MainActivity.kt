package com.ITIMADIsmailia.COVID19
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.app.AlertDialog
import android.app.job.JobParameters
import android.content.Intent
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat
import com.ITIMADIsmailia.COVID19.workmanagertask.MyJobService
import com.ITIMADIsmailia.COVID19.workmanagertask.MyWorker
import com.ITIMADIsmailia.COVID19.workmanagertask.NotificationHelper
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_countery_state.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit

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
        // jobScheduler start
        scheduler = this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        // jobScheduler End
        supportActionBar?.title = "COVID19 - Tracker"
        makeViewModel()

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

    fun makeViewModel() : MainViewModel {
        return ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private fun buildUI() = launch(Dispatchers.Main) {
        //passing hours to fetch data after
        makeViewModel().hoursCount = 2
        val countryState = makeViewModel().countryStat.await()

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
            //Toast.makeText(this@MainActivity, items.toString(), Toast.LENGTH_SHORT).show()
            val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            for (item: ItemModel in items) {
                // Hold my country to check for changes
                if (item.countryStat.countryName == getCountryPrefs().countryName) {

                    if (item.countryStat.newCases != getCountryPrefs().newCases || item.countryStat.totalDeath != getCountryPrefs().deaths ||
                        item.countryStat.totalRecovered != getCountryPrefs().recovered || item.countryStat.totalCases != getCountryPrefs().total){
                        editor.putString("countryName",item.countryStat.countryName)
                        editor.putString("newCases",item.countryStat.newCases)
                        editor.putString("total",item.countryStat.totalCases)
                        editor.putString("recovered",item.countryStat.totalRecovered)
                        editor.putString("deaths",item.countryStat.totalDeath)
                        editor.commit()

                        //show notification when data updated
                        val notificationHelper = NotificationHelper(applicationContext,"${getCountryPrefs().countryName}")
                        val nb = notificationHelper.channelNotification
                        notificationHelper.manager!!.notify(1, nb.build())
                        //end notification

                        Toast.makeText( this@MainActivity, "Notification", Toast.LENGTH_SHORT ).show()
                    }
                }
            }
        }
        recyclerView.apply {
            //******************************************************************//
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = groupAdapter
        }
        groupAdapter.setOnItemClickListener{ item, view ->
           // Toast.makeText(applicationContext,view.country_name.text ,Toast.LENGTH_LONG).show()
            val countryName = view.country_name.text.toString()
            val newCases = view.lblNewCases.text.toString()
            val total = view.lblTotalCases.text.toString()
            val recovered = view.lblTotalRecovered.text.toString()
            val deaths = view.lblTotalDeath.text.toString()

            val obj = SharedPrefsModel()
            obj.countryName = countryName
            obj.newCases = newCases
            obj.total = total
            obj.recovered = recovered
            obj.deaths = deaths

            alertDialogShow(obj)
        }
    }

    // Wagdy start
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater : MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.three_dots_main_menu, menu)
        return true
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // var bool : Boolean = false
        when(item.itemId){
            R.id.subscribe_item -> {
                val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)}
            R.id.every_hour_item -> scheduleJob(everyHour)
            R.id.every_2_hour_item -> scheduleJob(everyTwoHour)
            R.id.every_5_hour_item -> scheduleJob(everyFiveHour)
            R.id.daily_item -> scheduleJob(daily)
            R.id.cancel_update -> cancelJob()
//            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun scheduleJob(time:Int) {
        val componentName = ComponentName(this, MyJobService::class.java)
        val info = JobInfo.Builder(123, componentName)
            // .setRequiresCharging(true)
            // .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(time * 60 * 60 * 1000.toLong())
            .build()

        var scheduler =  this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
        val resultCode = scheduler!!.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled")

        } else {
            Log.d(TAG, "Job scheduling failed")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun cancelJob() {
        var scheduler =  this.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
        scheduler!!.cancel(123)
        Log.d(TAG, "Job cancelled")
    }

    companion object {
        private const val everyHour : Int = 1
        private const val everyTwoHour :Int = 2
        private const val everyFiveHour : Int = 5
        private const val daily : Int = 24
    }
    //Wagdy end
    fun alertDialogShow(country: SharedPrefsModel){
        val mAlertDialog = AlertDialog.Builder(this@MainActivity)

        mAlertDialog.setIcon(R.mipmap.ic_launcher_round) //set alertdialog icon
        mAlertDialog.setTitle("Subscribe!") //set alertdialog title
        mAlertDialog.setMessage("Are you sure you want to subscribe to "+country.countryName) //set alertdialog message

        mAlertDialog.setPositiveButton("Subscripe") { dialog, id ->
            setCountryPrefs(country)
        }
        mAlertDialog.setNegativeButton("Cancel") { dialog, id ->
        }
        mAlertDialog.show()

    }

    fun setCountryPrefs(country: SharedPrefsModel){
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("countryName",country.countryName)
        editor.putString("newCases",country.newCases)
        editor.putString("total",country.total)
        editor.putString("recovered",country.recovered)
        editor.putString("deaths",country.deaths)
        editor.commit()
        Toast.makeText(this@MainActivity, country.countryName, Toast.LENGTH_SHORT).show()
    }

    fun getCountryPrefs():SharedPrefsModel{
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val obj = SharedPrefsModel()
        obj.countryName = sharedPreference.getString("countryName","Unknown").toString()
        obj.newCases = sharedPreference.getString("newCases","0").toString()
        obj.total = sharedPreference.getString("total","0").toString()
        obj.recovered = sharedPreference.getString("recovered","0").toString()
        obj.deaths = sharedPreference.getString("deaths","0").toString()
        return  obj
    }

    fun bulidNotification() {


    }

    fun buildWorkManager() {
        //create constraints to attach it to the request
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        //create the request
        val myRequest = PeriodicWorkRequestBuilder<MyWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "update",
                ExistingPeriodicWorkPolicy.REPLACE, myRequest
            )
        Toast.makeText(
            applicationContext,
            "you will be notified every $1 hour(s)",
            Toast.LENGTH_SHORT
        ).show()
        WorkManager.getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData("update")
            .observe(this, Observer {
                if (it[0].state == WorkInfo.State.SUCCEEDED) {
                    viewModel.countryStat
                }
            })
    }

}
