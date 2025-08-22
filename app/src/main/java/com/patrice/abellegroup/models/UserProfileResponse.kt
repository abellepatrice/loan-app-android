package com.patrice.abellegroup.models

import android.media.Image

data class UserProfileResponse(
    val username: String,
    val email: String,
    val phone: String,
    val dob: String,
    val profileImage: String?
)
