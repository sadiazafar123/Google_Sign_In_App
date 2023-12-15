
package com.example.googlesigninapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
    }
}
/*

package com.cyberasol.dogmatching.ui

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyberasol.dogmatching.dataclass.chatdataclass.Message
import com.cyberasol.dogmatching.reterofitApi.Android
import com.cyberasol.dogmatching.reterofitApi.ApiClient
import com.cyberasol.dogmatching.reterofitApi.ApiInterface
import com.cyberasol.dogmatching.reterofitApi.RequestNotification
import com.cyberasol.dogmatching.reterofitApi.SendNotificationModel
import com.example.googlesigninapp.adapters.ChatAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    var chatAdapter: ChatAdapter? = null
    var recyclerView: RecyclerView? = null
    var messagesList: ArrayList<Message?>? = null
    var messageInput: TextInputLayout? = null
    var send: FloatingActionButton? = null
    var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null
    var databaseReference: DatabaseReference? = null
    var senderId: String? = null
    var receiverId: String? = null
    var receiverUserName: String? = null
    var fcmToken: String? = null
    var timeStamp: String? = null
    var chatID1: String? = null
    var chatID2: String? = null
    var finalChatID: String? = null
    var message: String? = null
    var backBtn: ImageView? = null
    var receiverUserImage: String? = null
    var userProfileImage: CircleImageView? = null
    var tv_userName: TextView? = null
    var apiService: ApiInterface? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        send = findViewById(R.id.fab_sendMessage)
        messageInput = findViewById(R.id.til_messageField)
        userProfileImage = findViewById(R.id.iv_act_profile_image)
        tv_userName = findViewById(R.id.tv_act_userName)
        recyclerView = findViewById(R.id.rcl_chatActivity)
        backBtn = findViewById(R.id.iv_back)
        backBtn.setOnClickListener(View.OnClickListener { finish() })
        messagesList = ArrayList<Message?>()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
        senderId = firebaseUser!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference
        timeStamp = SimpleDateFormat("dd-MM-yy HH:mm a").format(Calendar.getInstance().time)
        val intent = intent
        receiverId = intent.getStringExtra("receiverId")
        receiverUserName = intent.getStringExtra("receiverName")
        receiverUserImage = intent.getStringExtra("receiverImage")
        fcmToken = intent.getStringExtra("fcmToken")
        tv_userName.setText(receiverUserName)
        Glide.with(this).load(receiverUserImage).into(userProfileImage)
        chatID1 = "$senderId-$receiverId"
        chatID2 = "$receiverId-$senderId"
        checkIfChats1Exists()
        send.setOnClickListener(View.OnClickListener {
            message = messageInput.getEditText()!!.text.toString()
            if (message != "") {
                sendMessage()
            } else {
                Toast.makeText(
                    this@ChatActivity,
                    "You can't send Empty Message",
                    Toast.LENGTH_SHORT
                ).show()
            }
            messageInput.getEditText()!!.setText("")
        })
        recyclerView.setHasFixedSize(true)
        chatAdapter = ChatAdapter(this, messagesList, firebaseUser!!.uid)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        recyclerView.setLayoutManager(linearLayoutManager)
        recyclerView.setAdapter(chatAdapter)
    }

    private fun checkIfChats1Exists() {
        databaseReference!!.child("Chats").child(chatID1!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        finalChatID = chatID1
                        chat
                    } else {
                        checkIfChats2Exists()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun checkIfChats2Exists() {
        databaseReference!!.child("Chats").child(chatID2!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        finalChatID = chatID2
                        chat
                    } else {
                        finalChatID = chatID1
                        databaseReference!!.child("Chats").child(finalChatID!!).push()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onStart() {
        super.onStart()
    }

    private fun sendMessage() {
        val messageModel = Message()
        messageModel.setMessage(message)
        messageModel.setDateTime(timeStamp)
        messageModel.setUserId(firebaseUser!!.uid)
        databaseReference!!.child("Chats").child(finalChatID!!).child("messages").push()
            .setValue(messageModel)
        sendNotificationToPatner(
            fcmToken,
            "Match Maker",
            firebaseUser!!.displayName + " send message!"
        )
    }

    private val chat: Unit
        private get() {
            databaseReference!!.child("Chats").child(finalChatID!!).child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messagesList!!.clear()
                        for (snapshot1 in snapshot.children) {
                            val msg: Message? = snapshot1.getValue(Message::class.java)
                            messagesList!!.add(msg)
                        }
                        if (chatAdapter != null) {
                            chatAdapter.addMoreMessages(messagesList)
                            recyclerView!!.scrollToPosition(chatAdapter.getItemCount() - 1)
                        }
                        chatAdapter.notifyItemRangeChanged(0, messagesList!!.size - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    private fun sendNotificationToPatner(token: String?, userName: String, message: String) {
        val sendNotificationModel = SendNotificationModel(message, userName)
        val requestNotificaton = RequestNotification()
        requestNotificaton.setSendNotificationModel(sendNotificationModel)
        //token is id , whom you want to send notification ,
        requestNotificaton.setToken(token)
        requestNotificaton.setAndroid(Android("high"))
        apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val responseBodyCall: retrofit2.Call<ResponseBody> =
            apiService.sendChatNotification(requestNotificaton)
        responseBodyCall.enqueue(object : Callback<ResponseBody?>() {
            fun onResponse(
                call: retrofit2.Call<ResponseBody?>?,
                response: retrofit2.Response<ResponseBody?>?
            ) {
                Log.d("kkkk", "done")
            }

            fun onFailure(call: retrofit2.Call<ResponseBody?>?, t: Throwable?) {
                Log.d("kkkk", "failure")
            }
        })
    }
}*/
