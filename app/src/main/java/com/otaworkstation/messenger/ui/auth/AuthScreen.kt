package com.otaworkstation.messenger.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.otaworkstation.messenger.ui.theme.BackgroundLightGreen
import com.otaworkstation.messenger.ui.theme.PrimaryGreen
import com.otaworkstation.messenger.ui.theme.TextDark
import com.otaworkstation.messenger.ui.theme.TextGray

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        } else if (authState is AuthState.RegisterSuccess) {
            isLogin = true
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLightGreen),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Icon
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = BackgroundLightGreen.copy(alpha = 0.6f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = PrimaryGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isLogin) "Welcome back" else "Create account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = if (isLogin) "Sign in to your account" else "Fill in your details",
                    fontSize = 16.sp,
                    color = TextGray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Username", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Enter your username", color = TextGray.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryGreen,
                            cursorColor = PrimaryGreen
                        ),
                        singleLine = true
                    )
                }

                if (!isLogin) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Email", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Enter your email", color = TextGray.copy(alpha = 0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedBorderColor = PrimaryGreen,
                                cursorColor = PrimaryGreen
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        if (isLogin) {
                            TextButton(onClick = { /* TODO: Forgot Password */ }, contentPadding = PaddingValues(0.dp)) {
                                Text("Forgot password?", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    if (!isLogin) Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your password", color = TextGray.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryGreen,
                            cursorColor = PrimaryGreen
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = PrimaryGreen)
                } else {
                    Button(
                        onClick = {
                            if (isLogin) viewModel.login(username, password)
                            else viewModel.register(username, email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(if (isLogin) "Sign in" else "Sign up", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isLogin) "Don't have an account? " else "Already have an account? ",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                        TextButton(onClick = { isLogin = !isLogin }, contentPadding = PaddingValues(0.dp)) {
                            Text(if (isLogin) "Sign up" else "Sign in", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (authState is AuthState.Error) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}