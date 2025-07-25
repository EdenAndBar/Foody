package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateBackToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val background = Color.White
    val textPrimary = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF636366)
    val inputBackground = Color(0xFFF2F2F7)
    val buttonGray = Color(0xFFC7C7CC)
    val buttonTextColor = textPrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {

        // Back arrow
        IconButton(
            onClick = onNavigateBackToLogin,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Create your account",
                fontSize = 20.sp,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // First Name
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name", color = textSecondary) },
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

            // Last Name
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name", color = textSecondary) },
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

            // Email
            var emailError by remember { mutableStateOf<String?>(null) }

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (isValidEmail(it)) null else "Please enter a valid email"
                },
                label = { Text("Email", color = textSecondary) },
                isError = emailError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = textPrimary,
                    focusedContainerColor = inputBackground,
                    unfocusedContainerColor = inputBackground,
                    errorContainerColor = inputBackground
                ),
                shape = RoundedCornerShape(12.dp)
            )

            emailError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Password
            var passwordError by remember { mutableStateOf<String?>(null) }
            var showPassword by remember { mutableStateOf(false) }

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (isValidPassword(it)) null else "Password must be at least 6 characters, with letters and numbers"
                },
                label = { Text("Password", color = textSecondary) },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                isError = passwordError != null,
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
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    val visibilityIcon = if (showPassword) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    }

                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = visibilityIcon,
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                            tint = textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                }
            )
            passwordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    FirebaseAuthManager.register(
                        email = email,
                        password = password,
                        fullName = "$firstName $lastName",
                        onSuccess = { user ->
                            isLoading = false
                            onRegisterSuccess(user.email ?: "")
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                },
                enabled = !isLoading &&
                        email.isNotBlank() &&
                        firstName.isNotBlank() &&
                        lastName.isNotBlank() &&
                        isValidPassword(password),
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
                    Text("Register", fontSize = 16.sp)
                }
            }

        }
    }
}

fun isValidPassword(password: String): Boolean {
    if (password.length < 6) return false
    if (!password.any { it.isDigit() }) return false
    if (!password.any { it.isLetter() }) return false
    return true
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
