package com.ressphere.currencyconverter.di

import android.app.Application
import com.ressphere.currencyconverter.CurrencyApi
import com.ressphere.currencyconverter.main.DefaultMainRepository
import com.ressphere.currencyconverter.main.MainRepository
import com.ressphere.currencyconverter.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

private const val BASE_URL = "https://api.apilayer.com/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("OkHttpClient")
    fun provideOkHttpClient(@Named("LoggingInterceptor") loggingInterceptor: Interceptor): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @Named("LoggingInterceptor")
    fun provideLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    //@Named("CurrencyApi")
    @Provides fun provideCurrencyApi(@Named("OkHttpClient") client: OkHttpClient) : CurrencyApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create()
    }

    @Singleton
    //@Named("Dispatcher")
    @Provides fun provideDispatcher() : DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() =  Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }

    @Singleton
    @Provides
    //@Named("MainRepository")
    fun provideMainRepository(api: CurrencyApi): MainRepository = DefaultMainRepository(api)
}