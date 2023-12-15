package com.example.googlesigninapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googlesigninapp.adapters.LoginUserAdapter
import com.example.googlesigninapp.model.AuthUserPostData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class UserListActivity : AppCompatActivity(), LoginUserAdapter.onItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var userListDBRef: DatabaseReference
    lateinit var rootDatabase: FirebaseDatabase
    lateinit var authen: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    var loginUserList = ArrayList<AuthUserPostData>()
    lateinit var dialog: Dialog
    lateinit var btnYes: AppCompatButton
    lateinit var btnNo: AppCompatButton
    lateinit var progressBar:ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        recyclerView = findViewById(R.id.recyclerView)
        var loginUserImg = findViewById<CircleImageView>(R.id.loginImg)
        var loginUserEmail = findViewById<TextView>(R.id.loginEmail)
        progressBar=findViewById(R.id.pBar)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

/*
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
        requestIdToken("568362431764-5n491kr7m0s5ou9ap7u79kc179km68ec.apps.googleusercontent.com").requestEmail().build()
*/

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //get auth instnace
        authen = FirebaseAuth.getInstance()
        rootDatabase = FirebaseDatabase.getInstance()
        //to set login user image and email


        userListDBRef = rootDatabase.getReference("LoginUserInfoList")

        progressBar.visibility=View.VISIBLE
        Picasso.get().load(authen.currentUser!!.photoUrl).into(loginUserImg)
        loginUserEmail.text = authen.currentUser!!.email

        readLoginUserList()

        loginUserEmail.setOnClickListener() {
            dialog = Dialog(this)
            dialog.setContentView(R.layout.signout_dialogue_box)
            //find id of dilogue button
            btnYes = dialog.findViewById(R.id.btnYes)
            btnNo = dialog.findViewById(R.id.btnNo)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
            btnYes.setOnClickListener() {
                /*authen or FirebaseAuth.getinstance().signout() will use to signout from the firebase session,
                //However If You Use googleSignInClient.signOut() It Signs You Off from The account being referenced
                 by GoogleSignIn/Play Services Inside The App,
                  */
                authen.signOut()
                dialog.dismiss()
                googleSignInClient.signOut().addOnCompleteListener {
                    val intent= Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            btnNo.setOnClickListener() {
                dialog.dismiss()
            }


        }
    }


    private fun readLoginUserList() {
        userListDBRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("sadia", "snapvalue" + snapshot)

                if (loginUserList.size > 0) {
                    loginUserList.clear()
                }
                for (value in snapshot.children) {
                    val user = value.getValue(AuthUserPostData::class.java)
                    if (user != null && authen.currentUser?.email != user.email) {
                        loginUserList.add(user)

                    }
                }
                Log.v("loginUserList", "loginUserList " + Gson().toJson(loginUserList))
                loginUserAdp(loginUserList)
                progressBar.visibility=View.GONE

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    fun loginUserAdp(loginUserList: List<AuthUserPostData>) {
        val userInfo: LoginUserAdapter = LoginUserAdapter(loginUserList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userInfo

    }

    override fun onClickUserChat(position: Int, loginInfo: AuthUserPostData) {
        val intent = Intent(this, FirebbaseChatActivity::class.java)
        Log.v("loginInfo", "user" + Gson().toJson(loginInfo))

        intent.putExtra("chatuser", loginInfo)
        startActivity(intent)


    }

/*
    fun loginUserAdp(loginUserList: ArrayList<LoginInfo>) {
        val userInfo:LoginUserAdapter = LoginUserAdapter(loginUserList)
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=userInfo

    }
*/
}