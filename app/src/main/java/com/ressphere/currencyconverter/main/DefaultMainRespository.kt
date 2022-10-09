package com.ressphere.currencyconverter.main

import com.ressphere.currencyconverter.CurrencyApi
import com.ressphere.currencyconverter.data.models.CurrencyResponse
import com.ressphere.currencyconverter.utils.Resource
import javax.inject.Inject
import javax.inject.Named

class DefaultMainRepository @Inject constructor(
    private val api: CurrencyApi
): MainRepository {
    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates(API_KEY, base)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }

        } catch(e:Exception) {
            Resource.Error(e.message?:"An error occured")
        }
    }

    companion object {
        const val API_KEY = "qvVSP0MZD4DqbAnnUOgHpveuFCln8HQ6"
    }
}