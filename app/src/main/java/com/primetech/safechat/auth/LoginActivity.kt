package com.primetech.safechat.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.primetech.safechat.utils.PrefManager
import com.primetech.safechat.chat.InboxActivity
import com.primetech.safechat.databinding.ActivityLoginBinding
import com.primetech.safechat.db.Database
import com.primetech.safechat.db.UserDao
import com.primetech.safechat.utils.BaseUtils
import com.primetech.safechat.utils.BaseUtils.Companion.decrypt
import com.primetech.safechat.utils.BaseUtils.Companion.encrypt
import com.primetech.safechat.utils.BaseUtils.Companion.hideKeyboard

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var manager: PrefManager
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVies()
        setOnClickListener()
    }

    private fun initVies() {
        userDao = Database.getDatabase(this).userDao()
        manager = PrefManager(this)
    }

    private fun setOnClickListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                hideKeyboard()
                validateFields()
            }
            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun ActivityLoginBinding.validateFields() {
        if (!BaseUtils.isValidEmail(etEmail.text.toString().trim())){
            BaseUtils.showMessage(binding.root, "Invalid Email", true)
        }else if (etPassword.text.toString().trim().length < 3){
            BaseUtils.showMessage(binding.root, "Invalid Password", true)
        }else{
            checkUserInfo()
        }
    }

    private fun ActivityLoginBinding.checkUserInfo() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        // checking user already exist with encrypted email
        val user = userDao.getUser(encrypt(email)!!)
        if (user.isNotEmpty()){
            if (decrypt(user[0].email) != email){
                BaseUtils.showMessage(binding.root, "Email is Incorrect", true)
            }else if(decrypt(user[0].password) != password){
                BaseUtils.showMessage(binding.root, "Password is Incorrect", true)
            }else{
                manager.session = user[0].email
                startActivity(Intent(this@LoginActivity,InboxActivity::class.java))
                Toast.makeText(this@LoginActivity,"Login Successfully",Toast.LENGTH_SHORT).show()
                finish()
            }
        }else{
            BaseUtils.showMessage(binding.root, "User doesn't Exist", true)
        }

    }

    private fun checkLoginInfo(){
        Handler(Looper.getMainLooper()).postDelayed({
            manager = PrefManager(this)
            val intent = if (manager.session.isNotEmpty()){
                Intent(this, InboxActivity::class.java)
            }else{
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}