package com.primetech.safechat.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.primetech.safechat.chat.adapter.MessageAdapter
import com.primetech.safechat.databinding.ActivityChatBinding
import com.primetech.safechat.db.Chat
import com.primetech.safechat.db.Database
import com.primetech.safechat.db.UserDao
import com.primetech.safechat.db.UserEntity
import com.primetech.safechat.utils.BaseUtils.Companion.decrypt
import com.primetech.safechat.utils.BaseUtils.Companion.encrypt
import com.primetech.safechat.utils.BaseUtils.Companion.fromGsonToJson
import com.primetech.safechat.utils.BaseUtils.Companion.hideKeyboard
import com.primetech.safechat.utils.BaseUtils.Companion.jsonToGson
import com.primetech.safechat.utils.PrefManager

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var chatList: ArrayList<Chat>
    private lateinit var prefManager: PrefManager
    private lateinit var userDao: UserDao
    private lateinit var userEntity: UserEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setMessageAdapter()
        setOnClickListener()
        getChatList()
    }

    private fun initViews() {
        chatList = ArrayList()
        prefManager = PrefManager(this)
        userDao = Database.getDatabase(this).userDao()
    }

    private fun getChatList() {
        userEntity = userDao.getUser(prefManager.session)[0]
        if (userEntity.chatList != null){
            binding.tvNoMessage.isVisible = false
            binding.rvMessages.isVisible = true
            // decrypt the json string and convert to Message list
            chatList = jsonToGson(decrypt(userEntity.chatList))!!
            messageAdapter.updateMessage(chatList)
        }else{
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
                if (etMessage.text.trim().isNotEmpty()){
                    sendMessage()
                }
            }
        }
    }

    private fun sendMessage() {
        hideKeyboard()
        binding.tvNoMessage.isVisible = false
        binding.rvMessages.isVisible = true
        val message = Chat(type = 1,binding.etMessage.text.toString().trim())
        // encrypt the message and send in reply
        val encryptedReply = Chat(type = 0, encrypt(binding.etMessage.text.toString().trim())!!)
        chatList.add(message)
        chatList.add(encryptedReply)
        messageAdapter.updateMessage(chatList)
        binding.etMessage.setText("")
        binding.rvMessages.scrollToPosition(chatList.size-1)
    }

    private fun setMessageAdapter() {
        messageAdapter = MessageAdapter(messageList = chatList)
        layoutManager = LinearLayoutManager(this@ChatActivity)
        layoutManager.stackFromEnd = true
        binding.rvMessages.layoutManager = layoutManager
        binding.rvMessages.setHasFixedSize(true)
        binding.rvMessages.adapter = messageAdapter
    }

    private fun saveChat(){
        if (chatList.isNotEmpty()) {
            // convert chat list into json string
            val jsonChatList = fromGsonToJson(chatList)
            // encrypt the json string
            val encryptedChatList = encrypt(jsonChatList!!)
            // save chat into db
            userEntity.chatList = encryptedChatList
            userDao.updateUser(userEntity)
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