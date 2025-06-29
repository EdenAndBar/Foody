package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val background = Color.White // רקע לבן
    val textPrimary = Color(0xFF1C1C1E) // טקסט כהה
    val textSecondary = Color(0xFF636366) // אפור בינוני
    val inputBackground = Color(0xFFF2F2F7) // רקע תיבות קלט
    val buttonGray = Color(0xFFC7C7CC)
    val buttonTextColor = textPrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // לוגו
            Image(
                painter = painterResource(id = R.drawable.foody_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(250.dp)
                    .width(250.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Welcome! Log in to your account",
                fontSize = 16.sp,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = textSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = textPrimary,
                    focusedContainerColor = inputBackground,
                    unfocusedContainerColor = inputBackground
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = textSecondary) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = textPrimary,
                    focusedContainerColor = inputBackground,
                    unfocusedContainerColor = inputBackground
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    FirebaseAuthManager.login(email, password,
                        onSuccess = { user ->
                            isLoading = false
                            onLoginSuccess(user.email ?: "")
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonGray,
                    contentColor = buttonTextColor
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = buttonTextColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Log In", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Don't have an account? Register here",
                    fontSize = 14.sp,
                    color = textSecondary
                )
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
    }
}
