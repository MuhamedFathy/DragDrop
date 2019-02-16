package com.dragdrop.data.network

import com.dragdrop.BuildConfig
import com.dragdrop.data.api.TimeApi
import com.dragdrop.data.api.Urls
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetroConnect {

  fun getTimeApi(): TimeApi = getRetrofit().create(TimeApi::class.java)

  private fun getRetrofit(): Retrofit = Retrofit.Builder()
    .baseUrl(Urls.BASE_URL)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(getGson()))
    .client(getOkHttpClient(getHttpLoggingInterceptor()))
    .build()

  private fun getOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor
  ): OkHttpClient {
    return OkHttpClient.Builder()
      .connectTimeout(20, TimeUnit.SECONDS)
      .writeTimeout(20, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .retryOnConnectionFailure(true)
      .addInterceptor(loggingInterceptor)
      .build()
  }

  private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val logging = HttpLoggingInterceptor()
    logging.level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
    return logging
  }

  private fun getGson(): Gson {
    return GsonBuilder().setLenient().create()
  }
}