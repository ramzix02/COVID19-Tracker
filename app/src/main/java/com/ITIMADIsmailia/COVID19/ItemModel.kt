package com.ITIMADIsmailia.COVID19

import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_countery_state.*

class ItemModel(
    val countryStat: UnitCountriesStat
): Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            if(countryStat.countryName != ""){
                country_name.text = countryStat.countryName
            }else
            country_name.text = "Unknown"
            lblNewCases.text = countryStat.newCases
            lblTotalCases.text = countryStat.totalCases
            lblTotalRecovered.text = countryStat.totalRecovered
            lblTotalDeath.text = countryStat.totalDeath
        }
    }
    override fun getLayout() = R.layout.item_countery_state

}
