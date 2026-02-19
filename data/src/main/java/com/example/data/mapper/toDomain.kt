package com.example.data.mapper

import com.example.domain.model.GoogleUser
import com.example.domain.model.User
import com.example.network.dto.auth.social.GoogleUserDto
import com.example.network.dto.auth.UserDto
import com.example.network.dto.user_managment.GetUserResponse
import com.example.network.dto.user_managment.UserData

fun UserDto.toDomain(): User {
    return User(
        username = userName,
        firstName = "firstName",
        lastName = "lastName",
        email = email,
        profilePictureUrl = "profilePictureUrl"
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
        username = displayName ?: "",
        firstName = "givenName",
        lastName = "familyName",
        email = email,
        profilePictureUrl = profilePicUrl
    )
}

fun GetUserResponse.toDomain(): User {
    return User(
        username = data.userName,
        firstName = data.firstName,
        lastName = data.lastName,
        email = data.email,
        profilePictureUrl = data.profilePictureUrl
    )
}

fun UserData.toDomain(): User {
    return User(
        firstName = firstName,
        lastName = lastName,
        username = userName,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}