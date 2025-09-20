package com.patrice.abellegroup.models

data class Loan(
    val _id: String? = null,          // MongoDB loan ID
    val userId: String,              // Reference to User
    val amount: Int,              // Loan amount
    val interestRate: Double = 10.0, // Default 10% interest
    val durationMonths: Int,         // Repayment duration (max 3 months)
    val purpose: String,             // Loan purpose
    val status: String = "pending",  // pending | approved | rejected
    val createdAt: String? = null    // Loan creation timestamp
)

