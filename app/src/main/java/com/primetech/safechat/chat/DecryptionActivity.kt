package com.primetech.safechat.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.primetech.safechat.chat.adapter.DecryptionMessageAdapter
import com.primetech.safechat.databinding.ActivityDecryptionBinding
import com.primetech.safechat.db.Chat
import com.primetech.safechat.db.Database
import com.primetech.safechat.db.UserDao
import com.primetech.safechat.db.UserEntity
import com.primetech.safechat.utils.BaseUtils
import com.primetech.safechat.utils.BaseUtils.Companion.hideKeyboard
import com.primetech.safechat.utils.PrefManager

class DecryptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDecryptionBinding
    private lateinit var adapter: DecryptionMessageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var chatList: ArrayList<Chat>
    private lateinit var manager: PrefManager
    private lateinit var userDao: UserDao
    private lateinit var user: UserEntity

    private val MESSAGE = "message"
    private val DOCUMENT = "document"
    private val IMAGE = "image"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDecryptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setMessageAdapter()
        setOnClickListener()
        getChatList()
    }

    private fun initViews() {
        chatList = ArrayList()
        manager = PrefManager(this)
        userDao = Database.getDatabase(this).userDao()
    }

    private fun getChatList() {
        user = userDao.getUser(manager.session)[0]
        if (user.decryptionChatList != null) {
            binding.tvNoMessage.isVisible = false
            binding.rvMessages.isVisible = true
            // decrypt the json string and convert to Message list
            chatList = BaseUtils.jsonToGson(BaseUtils.decrypt(user.decryptionChatList))!!
            adapter.updateMessage(chatList)
        } else {
            binding.tvNoMessage.isVisible = true
            binding.rvMessages.isVisible = false
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                saveChat()
                onBackPressed()
            }
            ivSend.setOnClickListener {
                if (etMessage.text.trim().isNotEmpty()) {
                    sendMessage()
                }
            }
        }
    }

    private fun sendMessage() {
        hideKeyboard()
        binding.tvNoMessage.isVisible = false
        binding.rvMessages.isVisible = true
        val message =
            Chat(type = 1, form = MESSAGE, binding.etMessage.text.toString().trim())
        // encrypt the message and send in reply
        val decryptionMessage = BaseUtils.decrypt(binding.etMessage.text.toString().trim())

        if(decryptionMessage != null) {
            val encryptedReply =
                if (decryptionMessage.contains("com.instances.safechat") && decryptionMessage.contains(
                        ".jpg")
                ) {
                    Chat(type = 0, form = IMAGE, decryptionMessage)
                } else if (decryptionMessage.contains(packageName)) {
                    Chat(type = 0, form = DOCUMENT, decryptionMessage)
                } else {
                    Chat(type = 0, form = MESSAGE, decryptionMessage)
                }
            chatList.add(message)
            chatList.add(encryptedReply)
            adapter.updateMessage(chatList)
            binding.etMessage.setText("")
            binding.rvMessages.scrollToPosition(chatList.size - 1)
        }else{
            Toast.makeText(this@DecryptionActivity,"plz enter encrypted message",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMessageAdapter() {
        adapter = DecryptionMessageAdapter(messageList = chatList)
        layoutManager = LinearLayoutManager(this@DecryptionActivity)
        layoutManager.stackFromEnd = true
        binding.rvMessages.layoutManager = layoutManager
        binding.rvMessages.setHasFixedSize(true)
        binding.rvMessages.adapter = adapter
    }

    private fun saveChat() {
        if (chatList.isNotEmpty()) {
            // convert chat list into json string
            val jsonChatList = BaseUtils.fromGsonToJson(chatList)
            // encrypt the json string
            val encryptedChatList = BaseUtils.encrypt(jsonChatList!!)
            // save chat into db
            user.decryptionChatList = encryptedChatList
            userDao.updateUser(user)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveChat()
    }

    override fun onPause() {
        super.onPause()
        saveChat()
    }
}