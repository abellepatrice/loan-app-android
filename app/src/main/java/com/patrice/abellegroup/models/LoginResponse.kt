package com.patrice.abellegroup.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("accessToken") val token: String,
    val message: String,
    val user: User
)
