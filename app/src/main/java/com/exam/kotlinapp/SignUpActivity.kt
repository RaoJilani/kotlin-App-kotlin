package com.exam.kotlinapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.exam.kotlinapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity :AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email=""
    private var password=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar=supportActionBar!!
        actionBar.title = "Sign Up"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Creating account in")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth=FirebaseAuth.getInstance()
        binding.signUpBtn.setOnClickListener{
            validateData()
        }
    }
    private fun validateData() {
        email=binding.inputEtEmail.text.toString().trim()
        password=binding.inputEtPassword.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.inputEtEmail.error="Invalidate Email Format"
        }else if (TextUtils.isEmpty(password)){
            binding.inputEtPassword.error="Please enter password"
        }else if (password.length<6){
            binding.inputEtPassword.error="Password must be greater than 6"
        }
        else{
            firebaseSignUp()
        }

    }

    private fun firebaseSignUp() {
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
            progressDialog.dismiss()
            val firebaseUser=firebaseAuth.currentUser
            val email=firebaseUser!!.email
            Toast.makeText(this,"you are Sign up as ${email}",Toast.LENGTH_LONG).show()
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }.addOnFailureListener{ e ->
            progressDialog.dismiss()
            Toast.makeText(this,"Sign up failed due to ${e.message}", Toast.LENGTH_LONG).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}