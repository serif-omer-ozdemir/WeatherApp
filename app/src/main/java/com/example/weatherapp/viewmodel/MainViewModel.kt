package com.example.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.WeatherModel
import com.example.weatherapp.service.WeatherApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel :ViewModel() {

    //  rxjava kullanıldı


    private val weatherApiService = WeatherApiService()

    private val disposable = CompositeDisposable()

    val weather_data = MutableLiveData<WeatherModel>()
    val weather_eror = MutableLiveData<Boolean>()
    val weather_load = MutableLiveData<Boolean>()

    fun refreshData(cityName: String) {
        getDataFromApi(cityName)
        // getDataFromLocal()
    }

    private fun getDataFromApi(cityName:String) {
        weather_load.value = true

        disposable.add(
            weatherApiService.getDataService(cityName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>() {

                    override fun onError(e: Throwable) {
                        weather_eror.value=true
                        weather_load.value=false
                    }

                    override fun onSuccess(t: WeatherModel) {
                        weather_data.value = t
                        weather_eror.value = false
                        weather_eror.value = false
                    }

                }))
    }


}