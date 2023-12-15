package com.example.googlesigninapp.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.googlesigninapp.R
import com.example.googlesigninapp.model.ChatUserPostData


class SetUserChatAdapter( var userChatList: ArrayList<ChatUserPostData>,var loginUserId:String,var context:Context)
    :RecyclerView.Adapter<SetUserChatAdapter.MyChatViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.chat_design,parent,false)
        return MyChatViewHolder(view)


    }

    override fun getItemCount(): Int {
        return userChatList.size
    }

    override fun onBindViewHolder(holder: MyChatViewHolder, position: Int) {
       // for (i in 0 until userChatList.size) {
             holder.tvMessage.text = userChatList[position].message
            if (userChatList[position].userId == loginUserId)

            {
               holder.linearLayout.gravity = Gravity.END
                holder.tvMessage.setBackgroundResource(R.drawable.sender_user_shape)

                holder.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.white))

            }
            else{

                 holder.linearLayout.gravity=Gravity.START
                holder.tvMessage.setBackgroundResource(R.drawable.receiver_user_shape)

                holder.tvMessage.setTextColor(ContextCompat.getColor(context, R.color.black))
            }


    }
    class MyChatViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tvMessage:TextView=itemView.findViewById(R.id.tvMessage)
        val tvDate:TextView=itemView.findViewById(R.id.tvDate)
        val linearLayout:LinearLayout=itemView.findViewById(R.id.chat_linearlayout)





    }



}