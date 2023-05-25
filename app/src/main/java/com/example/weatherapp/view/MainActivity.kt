package com.example.weatherapp.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var tasarim: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasarim = ActivityMainBinding.inflate(layoutInflater)
        setContentView(tasarim.root)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityName", "ankara")
        tasarim.edtCityName.setText(cName)

        viewModel.refreshData(cName!!)//bu metodu ben olusturdum

        getLiveData()

        tasarim.swipeRefresLayout.setOnRefreshListener {
            tasarim.llDataView.visibility=View.GONE
            tasarim.tvError.visibility=View.GONE
            tasarim.pbLoading.visibility=View.GONE

            var cityName=GET.getString("cityName",cName)
            tasarim.edtCityName.setText(cityName)
            viewModel.refreshData(cityName!!)
            tasarim.swipeRefresLayout.isRefreshing=false
        }

        tasarim.imgSearchCityName.setOnClickListener {
            val cityName = tasarim.edtCityName.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
            //Log.i(TAG, "onCreate: " + cityName)
        }

    }

    private fun getLiveData() {
        viewModel.weather_data.observe(this, Observer { data ->

            data?.let {
                tasarim.llDataView.visibility = View.VISIBLE
                tasarim.pbLoading.visibility=View.GONE
                tasarim.tvDegree.text =
                    data.main.temp.toString() + "°c"//model package içindekı sınıflardakı degerler
                tasarim.tvCountyCode.text = data.sys.country.toString()
                tasarim.tvCityName.text = data.name.toString()
                tasarim.tvHumidity.text =": "+ data.main.humidity.toString()
                tasarim.tvSpeed.text =": "+ data.wind.speed.toString()
                tasarim.tvLat.text =": "+ data.coord.lat.toString()
                tasarim.tvLon.text =": "+ data.coord.lon.toString()

                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(tasarim.imgWeatherIcon)
            }

        })

        viewModel.weather_load.observe(this, Observer {load->
        load?.let {
            if (it){
                tasarim.pbLoading.visibility=View.VISIBLE
                tasarim.tvError.visibility=View.GONE
                tasarim.llDataView.visibility=View.GONE

            }else{
                tasarim.pbLoading.visibility=View.GONE
            }
        }
        })

        viewModel.weather_eror.observe(this, Observer {error->
            error?.let {
                if (error) {
                   tasarim.tvError.visibility = View.VISIBLE
                   tasarim.pbLoading.visibility = View.GONE
                   tasarim.llDataView.visibility = View.GONE
                } else {
                    tasarim.tvError.visibility = View.GONE
                }
            }

        })


        viewModel.weather_load.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                tasarim.pbLoading.visibility = View.VISIBLE
                tasarim.tvError.visibility = View.GONE
                tasarim.llDataView.visibility = View.GONE
                } else {
                    tasarim.pbLoading.visibility = View.GONE
                }
            }
        })




    }
}

