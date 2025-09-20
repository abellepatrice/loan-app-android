package com.patrice.abellegroup.api

//import com.google.android.gms.common.api.Response
import com.patrice.abellegroup.models.LoanRequest
import com.patrice.abellegroup.models.LoanResponse
//import com.patrice.abellegroup.models.LoanResponse
import com.patrice.abellegroup.models.LoginRequest
import com.patrice.abellegroup.models.LoginResponse
import com.patrice.abellegroup.models.MyLoansResponse
import com.patrice.abellegroup.models.RefreshRequest
import com.patrice.abellegroup.models.SignupResponse
import com.patrice.abellegroup.models.UpdateProfileResponse
import com.patrice.abellegroup.models.UploadResponse
import com.patrice.abellegroup.models.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
//import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Signup with image upload
    @Multipart
    @POST("auth/signup")
    fun signup(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("password") password: RequestBody,
        @Part profileImage: MultipartBody.Part? // Nullable image
    ): Call<SignupResponse>

    // Login
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Update profile with optional image
    @Multipart
    @PUT("user/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Call<UpdateProfileResponse>

    // Direct image upload
    @Multipart
    @POST("upload")
    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadResponse>
    @POST("auth/refresh")
    fun refreshToken(@Body request: RefreshRequest): LoginResponse

    @GET("user/profile")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserProfileResponse>

    @POST("loan/apply")
    fun applyLoan(
        @Header("Authorization") token: String,
        @Body loanRequest: LoanRequest): Call<LoanResponse>
    @GET("loan/my-loans")
    fun getMyLoans(
        @Header("Authorization") token: String
    ): Call<MyLoansResponse>

}

//package com.patrice.abellegroup.api
//import com.patrice.abellegroup.models.LoginRequest
//import com.patrice.abellegroup.models.LoginResponse
//import com.patrice.abellegroup.models.SignupRequest
//import com.patrice.abellegroup.models.SignupResponse
//import com.patrice.abellegroup.models.UpdateProfileResponse
//import com.patrice.abellegroup.models.UploadResponse
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.http.Body
//import retrofit2.http.Header
//import retrofit2.http.Headers
//import retrofit2.http.Multipart
//import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Part
//
//interface ApiService {
//    @Headers("Content-Type: application/json")
//    @POST("/api/auth/signup")
//    fun signup(@Body request: SignupRequest): Call<SignupResponse>
//    @Headers("Content-Type: application/json")
//    @POST("/api/auth/login")
//    fun login(@Body request: LoginRequest): Call<LoginResponse>
//    @Multipart
//    @PUT("/api/user/profile")
//    fun updateProfile(
//        @Header("Authorization") token: String,
//        @Part("username") username: RequestBody,
//        @Part("email") email: RequestBody,
//        @Part profileImage: MultipartBody.Part?
//    ): Call<UpdateProfileResponse>
//    @Multipart
//    @POST("/api/upload")
//    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadResponse>
//
//}