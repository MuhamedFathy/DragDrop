package com.dragdrop.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dragdrop.data.repository.TimeRepository
import com.dragdrop.extensions.addTo
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(
  private val timeRepository: TimeRepository
) : ViewModel() {

  private val compositeDisposable = CompositeDisposable()

  private val _time = MutableLiveData<String>()

  val time: LiveData<String>
    get() = _time

  fun loadTime() {
    _time.value = "00:00:00"
    timeRepository.loadTime()
      .subscribe({
        val time = it.body()?.time
        time?.let {
          _time.value = time.split(".")[0].split(" ")[1]
        }
      }) { Log.e(MainViewModel::class.java.simpleName, "", it) }
      .addTo(compositeDisposable)
  }

  override fun onCleared() {
    compositeDisposable.clear()
    super.onCleared()
  }
}