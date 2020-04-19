package com.ITIMADIsmailia.COVID19

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_countery_state.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity() : ScopedActivity(),KodeinAware{

    override val kodein by closestKodein()
    private val viewModelFactory: MainViewModelFactory by instance()
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
    fun alertDialogShow(country: SharedPrefsModel){
        val mAlertDialog = AlertDialog.Builder(this@MainActivity)
        mAlertDialog.setIcon(R.mipmap.ic_launcher_round) //set alertdialog icon
        mAlertDialog.setTitle("Subscribe!") //set alertdialog title
        mAlertDialog.setMessage("Are you sure you want to subscribe to "+country.countryName) //set alertdialog message

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        mAlertDialog.setPositiveButton("Yes") { dialog, id ->
            //perform some tasks here
            Toast.makeText(this@MainActivity, "Yes", Toast.LENGTH_SHORT).show()


            editor.putString("countryName",country.countryName)
            editor.putString("newCases",country.newCases)
            editor.putString("total",country.total)
            editor.putString("recovered",country.recovered)
            editor.putString("deaths",country.deaths)
            editor.commit()

        }
        mAlertDialog.setNegativeButton("No") { dialog, id ->
            //perform som tasks here




            //Toast.makeText(this@MainActivity, getCountryPrefs(), Toast.LENGTH_SHORT).show()
        }
        mAlertDialog.show()
        fun getCountryPrefs():SharedPrefsModel{
            val obj = SharedPrefsModel()
            obj.countryName = sharedPreference.getString("countryName","Unknown").toString()
            obj.newCases = sharedPreference.getString("newCases","0").toString()
            obj.total = sharedPreference.getString("total","0").toString()
            obj.recovered = sharedPreference.getString("recovered","0").toString()
            obj.deaths = sharedPreference.getString("deaths","0").toString()
            return  obj
        }
    }

}
