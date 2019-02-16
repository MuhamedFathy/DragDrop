package com.dragdrop.data.api

import com.dragdrop.data.models.Time
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.GET

interface TimeApi {

  @GET("/") fun getCurrentTime(): Flowable<Response<Time>>
}