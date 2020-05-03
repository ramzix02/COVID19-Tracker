package com.ITIMADIsmailia.COVID19
import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat
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
    private var times: Int  = 1
    private var intervalPeriod: Long  = 1
    var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        makeViewModel().hoursCount = intervalPeriod
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.subscribe_item -> {
                val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)}
            R.id.every_hour_item -> {
                buildWorkManager(1)
                Toast.makeText(applicationContext,"Your data will be updated every 1 hour",Toast.LENGTH_SHORT).show()
            }
            R.id.every_2_hour_item ->{
                buildWorkManager(2)
                Toast.makeText(applicationContext,"Your data will be updated every 2 hours",Toast.LENGTH_SHORT).show()
            }
            R.id.every_5_hour_item -> {
                buildWorkManager(5)
                Toast.makeText(applicationContext,"Your data will be updated every 5 hours",Toast.LENGTH_SHORT).show()
            }
            R.id.daily_item ->{
                buildWorkManager(24)
                Toast.makeText(applicationContext,"Your data will be updated every 24 hours",Toast.LENGTH_SHORT).show()
            }
            R.id.cancel_update -> {
                WorkManager.getInstance(applicationContext)
                    .cancelAllWork()
                Toast.makeText(applicationContext,"Your update be cancelled",Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
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


    fun buildWorkManager(intervalPeriod: Long) {
        //create constraints to attach it to the request
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        //create the request
        val myRequest = PeriodicWorkRequestBuilder<MyWorker>(
            repeatInterval = intervalPeriod,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "update",
                ExistingPeriodicWorkPolicy.REPLACE, myRequest
            )

        WorkManager.getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData("update")
            .observe(this, Observer {
                if (it[0].state == WorkInfo.State.SUCCEEDED) {
                    makeViewModel().countryStat
                    buildUI()
                    val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                    var countryName = sharedPreference.getString("countryName","Unknown").toString()
                    val notificationHelper = NotificationHelper(applicationContext,countryName)
                    val nb = notificationHelper.channelNotification
                    notificationHelper.manager!!.notify(1, nb.build())
                }
            })
    }

}
