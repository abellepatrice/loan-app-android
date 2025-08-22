package com.patrice.abellegroup.models

data class
LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
