package com.primetech.safechat.chat.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.primetech.safechat.R
import com.primetech.safechat.db.Chat


class  MessageAdapter(
    private var messageList: ArrayList<Chat>,
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

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
        var message = messageList[position]
        holder.tvMessage.text = message.Message
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMessage(messageList: ArrayList<Chat>){
        this.messageList = messageList
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMessage: TextView = itemView.findViewById(R.id.tv_message)
    }
}