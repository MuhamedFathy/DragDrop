package com.dragdrop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dragdrop.data.api.TimeApi
import com.dragdrop.data.repository.TimeRepository

class MainViewModelFactory(
  private val timeApi: TimeApi
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
      return MainViewModel(TimeRepository(timeApi)) as T
    }
    throw IllegalArgumentException("Unknown class name need ${MainViewModel::class.java.simpleName} instance")
  }
}