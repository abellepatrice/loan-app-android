package com.patrice.abellegroup.models

data class MyLoansResponse(
    val loans: List<Loan>,
    val message: String
)