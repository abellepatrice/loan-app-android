package com.patrice.abellegroup.models


data class SignupRequest(
    val username: String,
    val email: String,
    val phone: String,
    val dob: String,
    val profileImage: String,
    val password: String
)
