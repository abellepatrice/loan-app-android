package com.patrice.abellegroup.models

data class LoanResponse(
    val success: Boolean,
    val message: String
)

data class LoanDetails(
    val _id: String,
    val userId: String,
    val amount: Int,
    val purpose: String,
    val durationMonths: Int,
    val status: String,
    val createdAt: String
)

