package com.dragdrop.data.models

import com.google.gson.annotations.SerializedName

data class Time(
  @SerializedName("datetime") val time: String
)