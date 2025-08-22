package com.patrice.abellegroup.models


data class SignupResponse(
    val accessToken: String,
    val refreshToken: String,
    val role: String,
    val user: User
)

