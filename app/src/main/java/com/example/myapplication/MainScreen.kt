package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



@Composable
fun masha(context: Context) {
    var auth: FirebaseAuth
    val db = Firebase.firestore
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.light_blue)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), contentDescription = "logo", modifier =
            Modifier
                .padding(top = 110.dp)
                .size(209.dp, 203.dp)

        )
        val phone = remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }
        TextField(
            value = phone.value,
            onValueChange = { newValue ->
                if (newValue.length <= 12) {
                    phone.value = newValue
                }
            }, modifier = Modifier
                .fillMaxHeight(0.35f)
                .padding(top = 61.dp)
                .background(Color.White),
            placeholder = { Text("Введите номер телефона") },
            textStyle = TextStyle(fontSize = 28.sp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
            ),

            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "Дополнительная информация"
                    )
                }
            },
            maxLines = 1
        )



        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text("Подсказка")
                },
                text = {
                    Text("Введите номер телефона  + и начиная с 7")
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false },
                    ) {
                        Text("OK")
                    }
                }
            )
        }


        var password by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }

        TextField(
            value = password,
            onValueChange = { newValue -> password = newValue },
            modifier = Modifier
                .fillMaxHeight(0.42f)
                .padding(top = 40.dp)
                .background(Color.White),
            placeholder = { Text("пароль") },
            textStyle = TextStyle(fontSize = 28.sp),
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)


        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center
        )
        {
            Button(
                onClick = {
                    if (phone.value.isNotBlank()) {
                        val email = "${phone.value}@example.com"
                        auth = FirebaseAuth.getInstance()
                        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val result = task.result
                                if (result != null && result.signInMethods != null && result.signInMethods!!.isNotEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Этот номер телефона уже зарегистрирован",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    if (password.length < 8) {
                                        Toast.makeText(
                                            context,
                                            "Введите пароль для регистрации - не менее 8 символов",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val intent = Intent(
                                            context,
                                            RegistrationActivity::class.java
                                        ).apply {
                                            putExtra("password",password)
                                            putExtra("phone", phone.value)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            } else {
                                Log.e("TAG", "Error fetching sign-in methods: ", task.exception)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Введите номер телефона", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.35f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
            ) {
                Text(text = "Регистрация", style = TextStyle(fontSize = 16.sp))
            }


            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth(0.5f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
            ) {
                Text(text = "Войти", style = TextStyle(fontSize = 16.sp))

            }
        }
    }


}


