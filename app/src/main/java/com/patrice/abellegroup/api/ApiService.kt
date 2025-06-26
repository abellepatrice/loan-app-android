package com.patrice.abellegroup.api
import com.patrice.abellegroup.models.LoginRequest
import com.patrice.abellegroup.models.LoginResponse
import com.patrice.abellegroup.models.SignupRequest
import com.patrice.abellegroup.models.SignupResponse
import com.patrice.abellegroup.models.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/auth/signup")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>
    @Headers("Content-Type: application/json")
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    @Multipart
    @PUT("/api/user/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Call<UpdateProfileResponse>
    @Multipart
    @POST("/api/upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<Map<String, String>>

}