package com.patrice.abellegroup.models

data class SignupResponse(
    val success: Boolean,
    val message: String,
    val token: String?, // null if signup fails
    val role: String?   // optional: e.g., "user" or "admin"
)
