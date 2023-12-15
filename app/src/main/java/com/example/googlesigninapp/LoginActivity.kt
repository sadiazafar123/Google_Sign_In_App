package com.example.googlesigninapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.googlesigninapp.model.AuthUserPostData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {
    lateinit var authen: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
  //  lateinit var databaseRef: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authen= FirebaseAuth.getInstance()


        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
         requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()


        googleSignInClient = GoogleSignIn.getClient(this,gso)
        firebaseDatabase= FirebaseDatabase.getInstance()
      //  databaseRef= firebaseDatabase.getReference("LoginUsersList")


        var btnSignIn = findViewById<MaterialButton>(R.id.btn_sign_in)
        btnSignIn.setOnClickListener(){
            signInGoogle()
        }

        //signingreport
    }

    override fun onStart() {
        super.onStart()
        if (authen.currentUser!=null){

            val intent= Intent(this,UserListActivity::class.java)
            startActivity(intent)
            finish()

        }
        Log.v("current user","user"+ Gson().toJson(authen.currentUser))
    }


    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }


   private val launcher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if (result.resultCode== Activity.RESULT_OK){
            val data:Intent= result.data!!
            val task= GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account=task.getResult(ApiException::class.java)
                firebaseUserInfo(account)

            }catch (e:ApiException){
                Log.v("signInuser","ApiException"+ e)
            }
        }else{
            Log.v("signInuser","resultCode"+ result.resultCode)
        }
    }

    private fun firebaseUserInfo(account: GoogleSignInAccount) {

        val credentials=GoogleAuthProvider.getCredential(account.idToken,null)
        authen.signInWithCredential(credentials)
            .addOnCompleteListener {task->
            val user:FirebaseUser= authen.currentUser!!
            val authUserPostData=AuthUserPostData()
            authUserPostData.userId=user.uid
            authUserPostData.name=user.displayName
            authUserPostData.mobNumber=user.phoneNumber
            authUserPostData.email=user.email
            authUserPostData.imaged=user.photoUrl.toString()

            saveUserInfo(authUserPostData)


        }
            .addOnFailureListener {
                Log.v("signInuser","firebaseUserInfo"+ it.cause)
            }


    }

    private fun saveUserInfo(authUserPostData: AuthUserPostData) {
        firebaseDatabase.reference
            .child("LoginUserInfoList")
            .child(authUserPostData.userId.toString())
            .setValue(authUserPostData)
            .addOnCompleteListener { task->
                val intent= Intent(this,UserListActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this@LoginActivity,"Login Successful",Toast.LENGTH_SHORT).show()
             }
            .addOnFailureListener {
                Log.v("signInuser","saveUserInfo"+ it.cause)
            }
    }
}