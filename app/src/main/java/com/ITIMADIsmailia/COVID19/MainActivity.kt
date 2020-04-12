package com.ITIMADIsmailia.COVID19

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "COVID19 - Tracker"
        viewModel = ViewModelProviders.of(this,viewModelFactory)
            .get(MainViewModel::class.java)
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
}
