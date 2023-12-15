package com.example.googlesigninapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googlesigninapp.adapters.SetUserChatAdapter
import com.example.googlesigninapp.model.AuthUserPostData
import com.example.googlesigninapp.model.ChatUserPostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class FirebbaseChatActivity : AppCompatActivity() {
    lateinit var authen: FirebaseAuth
    lateinit var rootDatabase : FirebaseDatabase
    lateinit var aTobDBRef : DatabaseReference
    lateinit var bTOaDBRef : DatabaseReference
    lateinit var finalChatId : String
    lateinit var chatId1: String
    lateinit var chatId2: String
    lateinit var btnSend: ImageView
    lateinit var eTextMessage : EditText
    lateinit var recyclerView: RecyclerView
    var userChatList = ArrayList<ChatUserPostData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebbase_chat)
        recyclerView = findViewById(R.id.chat_recyclerView)
        btnSend = findViewById(R.id.btn_send)
        eTextMessage = findViewById(R.id.et_message)
        authen = FirebaseAuth.getInstance()

        var userInfoObject: AuthUserPostData?
        if(Build.VERSION.SDK_INT >= 33 ) {

            userInfoObject = intent.extras?.getParcelable("chatuser", AuthUserPostData::class.java)
        }
        else {

            userInfoObject = intent.extras?.getParcelable("chatuser")

        }

        val senderUserId=authen.currentUser!!.uid
        val receiverChatId=userInfoObject!!.userId

        Log.v("loginUserId","id"+ Gson().toJson(userInfoObject))
        Log.v("loginUserId","id"+ Gson().toJson(senderUserId))
        rootDatabase= FirebaseDatabase.getInstance()
        aTobDBRef= rootDatabase.getReference("Chats").child(senderUserId+ "-"+receiverChatId)
        bTOaDBRef= rootDatabase.getReference("Chats").child(receiverChatId+ "-"+senderUserId)

        chatId1  = senderUserId+ "-" +receiverChatId
        chatId2  = receiverChatId+ "-" +senderUserId
        finalChatId = chatId1

        checkChat1Exist()
        checkChat2Exist()

        btnSend.setOnClickListener(){
            val chatUserPostData=ChatUserPostData()
          //  val cal = Calendar.getInstance()
          // chatUserPostData.dateTime =cal.time.toString()
            //output
            if (eTextMessage.text.isNullOrEmpty()){
                Toast.makeText(this@FirebbaseChatActivity, "type msg", Toast.LENGTH_SHORT).show()
            } else{
                chatUserPostData.message = eTextMessage.text.toString()
                eTextMessage.setText("")
                chatUserPostData.dateTime = SimpleDateFormat.getDateTimeInstance().format(Date())
                //output output : 18-May-2020 11:00:39 AM
                chatUserPostData.userId = senderUserId
                saveUserChat(chatUserPostData)


            }
        }
    }



     private fun getSnapShotValue(snapshot: DataSnapshot) {
        Log.d("snapValue","getSnapShotValue"+snapshot)
         userChatList.clear()

         for (value in snapshot.children){
           var userChat = value.getValue(ChatUserPostData::class.java)
             userChatList.add(userChat!!)
            Log.d("getSnapShotValue","uservalue "+userChat)
            Log.d("chatuserlist","chatmsg"+ userChatList)

         }
         userChatAdapter(userChatList)
     }

    private fun userChatAdapter(userChatList: ArrayList<ChatUserPostData>) {
        val loginUserId: String =authen.currentUser!!.uid
        val userChatInfo:SetUserChatAdapter= SetUserChatAdapter(userChatList,loginUserId,this@FirebbaseChatActivity)
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=userChatInfo
        //to show chat msg from last msg
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FirebbaseChatActivity).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
    }

    private  fun checkChat1Exist() {
        rootDatabase.reference
            .child("chats")
            .child(chatId1)
            .child("Messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        Log.d("checkChat","chatid1 "+snapshot)
                        finalChatId = chatId1
                        getSnapShotValue(snapshot)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }
    private fun checkChat2Exist(){
        rootDatabase.reference
            .child("chats")
            .child(chatId2)
            .child("Messages")
            .addValueEventListener(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        Log.d("checkChat","chatid2 "+snapshot)
                        finalChatId= chatId2
                        getSnapShotValue(snapshot)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

   private  fun saveUserChat(chatUserPostData: ChatUserPostData) {
         //push( ) is used to generate random id
         rootDatabase.reference
             .child("chats")
             .child(finalChatId)
             .child("Messages")
             .push()
             .setValue(chatUserPostData)
             .addOnCompleteListener {
                 Toast.makeText(this, "chat save succesfully", Toast.LENGTH_SHORT).show()
                 Log.v("saveUserChat","add successfully")
             }.addOnFailureListener(){
                 Log.v("saveUserChat","failure"+it.cause)
                 Toast.makeText(this, "chat not saved", Toast.LENGTH_SHORT).show()
             }
     }
}





