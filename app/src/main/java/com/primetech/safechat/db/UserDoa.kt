package com.instances.safechat.db

import androidx.room.*
import com.primetech.safechat.db.UserEntity

@Dao
interface UserDoa {
    @Query("SELECT * FROM userEntity where email = :email")
    fun getUser(email:String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: UserEntity)

    @Update
    fun updateUser(user: UserEntity)

    @Query("DELETE FROM UserEntity")
    fun deleteAllUsers()
}