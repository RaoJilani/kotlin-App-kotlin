package com.exam.kotlinapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PatternMatcher
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.exam.kotlinapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    //for check for update on playstore
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var task: Task<AppUpdateInfo>
    public val update_code = 22
    private lateinit var installUpdateListener: InstallStateUpdatedListener
    private lateinit var snackbar: Snackbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.setTitle("Login")

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Logged In")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.signUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            validateData()
        }
        installUpdateListener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popup()
            }
        }
        inAppUp()
    }

    private fun popup() {
        snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "App update almost done", Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("reload", View.OnClickListener {
            appUpdateManager.completeUpdate()
        })
        snackbar.setTextColor(Color.parseColor("#FF0000"))
        snackbar.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == update_code) {
            if (resultCode != RESULT_OK) {

            }
        }
    }

    private fun inAppUp() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        task = appUpdateManager.appUpdateInfo
        task.addOnSuccessListener { e ->
            if (e.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && e.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    e,
                    AppUpdateType.IMMEDIATE,
                    this,
                    update_code
                )


            }
        }
        appUpdateManager.registerListener(installUpdateListener)
    }


    private fun validateData() {
        email = binding.inputEtEmail.text.toString().trim()
        password = binding.inputEtPassword.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEtEmail.error = "Invalidate Email Format"
        } else if (TextUtils.isEmpty(password)) {
            binding.inputEtPassword.error = "Please enter password"
        } else {
            firebaseLogin()
        }

    }

    private fun firebaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "you are logged In as ${email}", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(this, "Log In failed due to ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}