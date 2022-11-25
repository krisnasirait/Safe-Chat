package com.primetech.safechat.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.primetech.safechat.databinding.ActivityRegisterBinding
import com.primetech.safechat.db.Database
import com.primetech.safechat.db.UserDao
import com.primetech.safechat.db.UserEntity
import com.primetech.safechat.utils.BaseUtils.Companion.encrypt
import com.primetech.safechat.utils.BaseUtils.Companion.hideKeyboard
import com.primetech.safechat.utils.BaseUtils.Companion.isValidEmail
import com.primetech.safechat.utils.BaseUtils.Companion.showMessage

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setOnClickListener()
    }

    private fun initViews() {
        userDao = Database.getDatabase(this).userDao()
    }

    private fun setOnClickListener() {
        binding.apply {
            tvLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
            btnRegister.setOnClickListener {
                hideKeyboard()
                validateFields()
            }
        }
    }

    private fun ActivityRegisterBinding.validateFields() {
        if (etUsername.text.toString().trim().isEmpty()){
            showMessage(binding.root,"Invalid Username",true)
        }else if (!isValidEmail(etEmail.text.toString().trim())){
            showMessage(binding.root,"Invalid Email",true)
        }else if (etPassword.text.toString().trim().length < 3){
            showMessage(binding.root,"Invalid Password",true)
        }else{
            getData()
        }
    }

    private fun ActivityRegisterBinding.getData() {
        // encrypt data with AES or CBC algo
        val username = encrypt(etUsername.text.toString().trim())
        val email = encrypt(etEmail.text.toString().trim())
        val password = encrypt(etPassword.text.toString().trim())

        val usersTable = UserEntity(
            userName = username!!,
            email = email!!,
            password = password!!,
            chatList = null)
        // To check user already exist
        val user = userDao.getUser(email)

        if (user.isEmpty()){
            userDao.addUser(usersTable)
            Toast.makeText(this@RegisterActivity,"Signup successfully",Toast.LENGTH_SHORT).show()
            finish()
        }else{
            showMessage(binding.root,"User Already Exist",true)
        }

    }
}