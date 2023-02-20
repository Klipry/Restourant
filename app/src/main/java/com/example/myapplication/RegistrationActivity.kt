package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

    class RegistrationActivity : ComponentActivity() {
        private var verificationId: String = ""
        val db = Firebase.firestore


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {

                MyApplicationTheme {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val phone = intent.getStringExtra("phone")
                        val password = intent.getStringExtra("password")

                        var smsCode by remember { mutableStateOf("") }

                        TextField(
                            value = smsCode,
                            onValueChange = { smsCode = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            label = { Text("SMS Code") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            textStyle = MaterialTheme.typography.body1,
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (smsCode.length == 6) {
                                    val credential = PhoneAuthProvider.getCredential(
                                        verificationId,
                                        smsCode
                                    )
                                    FirebaseAuth.getInstance().signInWithCredential(credential)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val intent = Intent(
                                                    this@RegistrationActivity,
                                                    MainUserPage::class.java
                                                )
                                                val database = Firebase.database.reference
                                                val user = hashMapOf(
                                                    "phone" to phone,
                                                    "password" to password
                                                )
                                                database.child("users").push().setValue(user)

                                                startActivity(intent)
                                            } else {
                                                Toast.makeText(
                                                    this@RegistrationActivity,
                                                    "SMS code is incorrect",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                } else {
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "SMS code should be 6 digits long",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Verify")
                        }



                        LaunchedEffect(Unit) {
                            val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                .setPhoneNumber(phone!!)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(this@RegistrationActivity)
                                .setCallbacks(object :
                                    PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                        // Auto-retrieval is successful, do something here
                                    }

                                    override fun onVerificationFailed(exception: FirebaseException) {
                                        // Auto-retrieval failed, handle the error here
                                    }

                                    override fun onCodeSent(
                                        verificationId: String,
                                        token: PhoneAuthProvider.ForceResendingToken,
                                    ) {
                                        super.onCodeSent(verificationId, token)
                                        this@RegistrationActivity.verificationId = verificationId
                                    }
                                })
                                .build()
                            PhoneAuthProvider.verifyPhoneNumber(options)
                        }
                    }
                }
            }
        }
    }