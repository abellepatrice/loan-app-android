package com.patrice.abellegroup.models

data class LoanRequest(
    val userId: String,
    val amount: Int,
    val purpose: String,
    val durationMonths: Int
)
