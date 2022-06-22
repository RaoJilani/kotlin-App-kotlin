package com.exam.kotlinapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.exam.kotlinapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity:AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var actionBar: ActionBar
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar=supportActionBar!!
        actionBar.title = "Profile"

        firebaseAuth=FirebaseAuth.getInstance()
        checkUser()
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser(){
        val firebaseUser=firebaseAuth.currentUser
        if (firebaseUser!=null){
            val firebaseUser=firebaseAuth.currentUser
            val email=firebaseUser!!.email
            binding.emailTv.text=email
        }else{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}