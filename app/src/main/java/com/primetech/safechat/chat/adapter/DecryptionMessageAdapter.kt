package com.primetech.safechat.chat.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.primetech.safechat.R
import com.primetech.safechat.db.Chat
import java.io.File
import java.util.*


class  DecryptionMessageAdapter(
    var messageList: ArrayList<Chat>,
) : RecyclerView.Adapter<DecryptionMessageAdapter.ViewHolder>() {

    private val MESSAGE = "message"
    private val IMAGE = "image"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_right_message, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_left_message, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].type == 1) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        if (message.form == MESSAGE){
            holder.rlTextMessage.isVisible = true
            holder.cvImage.isVisible = false
            holder.cvVideoDetail.isVisible = false
            holder.tvMessage.text = message.Message
        }else if (message.form == IMAGE){
            holder.rlTextMessage.isVisible = false
            holder.cvImage.isVisible = true
            holder.cvVideoDetail.isVisible = false

            Glide.with(holder.itemView.context)
                .load(message.Message)
                .into(holder.ivMessage)
        }else{
            holder.rlTextMessage.isVisible = false
            holder.cvImage.isVisible = false
            holder.cvVideoDetail.isVisible = true
            holder.tvTitle.text = UUID.randomUUID().toString()
        }

        holder.cvVideoDetail.setOnClickListener {
            openFile(holder,message.Message)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    private fun openFile(holder: ViewHolder, path: String){
        val file = File(path)
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(if (VERSION.SDK_INT >= VERSION_CODES.N) FileProvider.getUriForFile(
                holder.itemView.context,
                "com.primetech.safechat.fileprovider",
                file) else Uri.fromFile(file),
                "*/*")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setPackage("com.android.chrome")
        holder.itemView.context.startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMessage(messageList: ArrayList<Chat>){
        this.messageList = messageList
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        var rlTextMessage: RelativeLayout = itemView.findViewById(R.id.rl_text_message)
        var cvImage: CardView = itemView.findViewById(R.id.cv_image)
        var cvVideoDetail: CardView = itemView.findViewById(R.id.cv_document)
        var ivMessage: ImageView = itemView.findViewById(R.id.iv_message)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    }
}