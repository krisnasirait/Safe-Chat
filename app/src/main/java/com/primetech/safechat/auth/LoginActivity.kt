package com.primetech.safechat.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.instances.safechat.utils.PrefManager
import com.primetech.safechat.MainActivity
import com.primetech.safechat.chat.InboxActivity
import com.primetech.safechat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var manager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginInfo()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.tvRegister.setOnClickListener {
            Intent(this,RegisterActivity::class.java).also {
                startActivity(it)
            }
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