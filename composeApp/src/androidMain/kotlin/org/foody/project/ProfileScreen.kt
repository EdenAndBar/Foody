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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.android.gms.tasks.Tasks
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person

@Composable
fun ProfileScreen() {
    val user = FirebaseAuth.getInstance().currentUser
    val initialName = user?.displayName ?: ""
    val email = user?.email ?: ""

    var firstName by remember { mutableStateOf(initialName.split(" ").firstOrNull() ?: "") }
    var lastName by remember { mutableStateOf(initialName.split(" ").getOrNull(1) ?: "") }
    var newPassword by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

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
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Icon",
                    tint = Color(0xFF1C1C1E),
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Your Profile", fontSize = 20.sp, color = textPrimary)

            Spacer(modifier = Modifier.height(32.dp))

            // First Name
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name", color = textSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(inputBackground, textPrimary, textSecondary),
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
                colors = textFieldColors(inputBackground, textPrimary, textSecondary),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email (not editable)
            TextField(
                value = email,
                onValueChange = {},
                label = { Text("Email", color = textSecondary) },
                singleLine = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(inputBackground, textPrimary, textSecondary),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password (new)
            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password", color = textSecondary) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(inputBackground, textPrimary, textSecondary),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isSaving = true
                    val fullName = "$firstName $lastName".trim()
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()

                    val updateTasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

                    user?.let {
                        updateTasks.add(it.updateProfile(profileUpdates))
                        if (newPassword.isNotBlank()) {
                            updateTasks.add(it.updatePassword(newPassword))
                        }

                        Tasks.whenAllComplete(updateTasks)
                            .addOnCompleteListener { task ->
                                isSaving = false
                                message = if (task.result.all { it.isSuccessful }) {
                                    "Profile updated!"
                                } else {
                                    "Failed to update profile."
                                }
                            }
                    }
                },
                enabled = !isSaving && firstName.isNotBlank(),
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonGray,
                    contentColor = buttonTextColor
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = buttonTextColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            message?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = if (it.contains("Failed")) MaterialTheme.colorScheme.error else Color(0xFF34C759)
                )
            }
        }
    }
}

@Composable
fun textFieldColors(
    background: Color,
    primary: Color,
    secondary: Color
): TextFieldColors = TextFieldDefaults.colors(
    focusedTextColor = primary,
    unfocusedTextColor = primary,
    disabledTextColor = secondary.copy(alpha = 0.7f), // טקסט מאופרר
    disabledLabelColor = secondary.copy(alpha = 0.7f),
    disabledIndicatorColor = Color.Transparent, // אין קו
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    cursorColor = primary,
    focusedContainerColor = background,
    unfocusedContainerColor = background,
    disabledContainerColor = Color(0xFFE5E5EA) // רקע אפור בהיר לשדה לא פעיל
)
