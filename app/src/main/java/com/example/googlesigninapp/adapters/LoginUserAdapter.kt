package com.example.googlesigninapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.googlesigninapp.R
import com.example.googlesigninapp.model.AuthUserPostData
import com.squareup.picasso.Picasso

class LoginUserAdapter( val loginUserList: List<AuthUserPostData> ,var mInterface:onItemClickListener):RecyclerView.Adapter<LoginUserAdapter.LoginViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.adapter_layout,parent,false)
        return LoginViewHolder(view)



    }

    override fun getItemCount(): Int {
        return loginUserList.size
    }

    override fun onBindViewHolder(holder: LoginViewHolder, position: Int) {
        holder.userName.text=loginUserList[position].name
        holder.email.text=loginUserList[position].email
       // Glide.with(this).load(loginUserList[position].imaged).into(holder.image)
        //glide and picasso used to load url image
        Picasso.get().load(loginUserList[position].imaged).into(holder.image)


        holder.itemClick.setOnClickListener(){
            mInterface.onClickUserChat(position,loginUserList[position])

        }
    }
    class LoginViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val userName:TextView=itemView.findViewById(R.id.tv_name)
        val email:TextView=itemView.findViewById(R.id.tv_email)
        val image:ImageView=itemView.findViewById(R.id.imageView)
        val itemClick:LinearLayout=itemView.findViewById(R.id.item_click)


    }
    interface onItemClickListener{
        fun onClickUserChat(position: Int, loginInfo: AuthUserPostData)
    }

}