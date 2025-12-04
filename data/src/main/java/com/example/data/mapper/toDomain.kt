package com.example.data.mapper

import com.example.domain.model.GoogleUser
import com.example.domain.model.User
import com.example.network.dto.auth.GoogleUserDto
import com.example.network.dto.auth.UserDto

fun UserDto.toDomain(): User {
    return User(
        username = ""
    )
}

fun GoogleUserDto.toDomain(): GoogleUser {
    return GoogleUser(
        idToken = idToken,
        displayName = displayName,
        email = email,
        profilePicUrl = profilePicUrl

    )
}

fun GoogleUserDto.toUser(): User {
    return User(
        username = displayName!!
    )
}