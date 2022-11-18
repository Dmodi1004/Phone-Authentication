package com.example.datingapp.Auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.datingapp.MainActivity
import com.example.datingapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        sendOtp.setOnClickListener {
            if (userNumber.text.toString().isNotEmpty()) {
                sendOtp(userNumber.text.toString().trim())
            } else {
                userNumber.error = "Please enter your number"
            }
        }

        verifyOtp.setOnClickListener {
            if (userOtp.text.toString().isNotEmpty()) {
                verifyOtp(userOtp.text.toString())
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)

        verifyOtp.visibility = View.GONE
        progressBar2.visibility = View.VISIBLE

        signInWithPhoneAuthCredential(credential)
    }

    private fun sendOtp(number: String) {
        sendOtp.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                sendOtp.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                this@LoginActivity.verificationId = verificationId

                sendOtp.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                numbLayout.visibility = View.GONE
                otpLayout.visibility = View.VISIBLE

            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91 ${userNumber.text.toString().trim()}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
    }

}