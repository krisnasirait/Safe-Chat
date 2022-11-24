package com.primetech.safechat.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var userName: String,
    var email: String,
    var password: String,
    var chatList: String? = null,
    var decryptionChatList: String? = null
    )

data class Chat(
    val type: Int,
    val form: String,
    val Message: String
)
