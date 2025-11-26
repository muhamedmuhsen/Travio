package com.example.data.mapper

import com.example.domain.model.User
import com.example.network.dto.auth.UserDto

fun UserDto.toDomain(): User {
    return User(
        username = ""
    )
}