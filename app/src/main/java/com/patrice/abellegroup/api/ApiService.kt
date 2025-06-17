package com.patrice.abellegroup.api
import com.patrice.abellegroup.models.SignupRequest
import com.patrice.abellegroup.models.SignupResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/signup")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>
}