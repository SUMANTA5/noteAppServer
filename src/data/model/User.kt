package com.example.data.model

import org.jetbrains.exposed.sql.Column

data class User(
    val email: String,
    val hashPassword: String,
    val userName: String
)