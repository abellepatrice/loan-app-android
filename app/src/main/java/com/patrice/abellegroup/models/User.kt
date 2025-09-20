package com.patrice.abellegroup.models

import com.google.gson.annotations.SerializedName

data class User (
   @SerializedName("_id") val id: String,  // 👈 map _id → id
   val username: String,
   val phone: String,
   val dob: String,
   val email : String,
   val profileImage: String? = null,
   val role: String? = "user",
   val createdAt: String

   )