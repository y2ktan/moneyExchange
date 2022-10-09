package com.ressphere.currencyconverter.main

import com.ressphere.currencyconverter.data.models.CurrencyResponse
import com.ressphere.currencyconverter.utils.Resource

interface MainRepository {
    suspend fun getRates(base:String): Resource<CurrencyResponse>
}