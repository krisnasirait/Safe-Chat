package com.primetech.safechat.chat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.primetech.safechat.auth.LoginActivity
import com.primetech.safechat.databinding.ActivityInboxBinding
import com.primetech.safechat.db.Database
import com.primetech.safechat.db.UserDao
import com.primetech.safechat.utils.BaseUtils
import com.primetech.safechat.utils.PrefManager

class InboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInboxBinding
    private lateinit var prefManager: PrefManager
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setOnClickListeners()
    }

    private fun initViews() {
        prefManager = PrefManager(this)
        userDao = Database.getDatabase(this).userDao()
    }

    private fun setOnClickListeners() {
        binding.apply {
            cvMessage.setOnClickListener {
                startActivity(Intent(this@InboxActivity,ChatActivity::class.java))
            }
            cvClear.setOnClickListener {
                clearChat()
            }
            btnLogout.setOnClickListener {
                prefManager.logout()
                startActivity(Intent(this@InboxActivity,LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun clearChat() {
        val user = userDao.getUser(prefManager.session)[0]
        user.chatList = null
        userDao.updateUser(user)
        BaseUtils.showMessage(binding.root, "Chat Clear")
    }
}