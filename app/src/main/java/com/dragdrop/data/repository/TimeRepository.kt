package com.dragdrop.data.repository

import com.dragdrop.data.api.TimeApi
import com.dragdrop.data.models.Time
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.util.concurrent.TimeUnit


class TimeRepository(
  private val timeApi: TimeApi
) {

  fun loadTime(): Flowable<Response<Time>> {
    return timeApi.getCurrentTime()
      .retry()
      .repeatWhen { completed -> completed.delay(200, TimeUnit.MILLISECONDS) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
  }
}