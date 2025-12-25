package com.example.sorms_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.rememberCoroutineScope
import com.example.sorms_app.BuildConfig
import com.example.sorms_app.data.repository.AuthRepository
import com.example.sorms_app.presentation.navigation.AppNavigation
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GoogleSignInDebug", "Using GOOGLE_CLIENT_ID: ${BuildConfig.GOOGLE_CLIENT_ID}")
        enableEdgeToEdge()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    Log.d("GoogleSignInDebug", "Received idToken: $idToken")
                    if (idToken.isNullOrBlank()) {
                        Toast.makeText(this, "Không lấy được idToken từ Google", Toast.LENGTH_SHORT).show()
                        return@registerForActivityResult
                    }
                    authViewModel.loginWithIdToken(idToken)
                } catch (e: ApiException) {
                    val message = when (e.statusCode) {
                        12501 -> "Bạn đã huỷ chọn tài khoản Google"
                        12500, 10 -> {
                            Log.e("GoogleSignInError", "DEVELOPER_ERROR (${e.statusCode}): ${e.message}", e)
                            Log.e("GoogleSignInError", "Client ID used: ${BuildConfig.GOOGLE_CLIENT_ID}")
                            Log.e("GoogleSignInError", "Package name: ${packageName}")
                            "Lỗi cấu hình đăng nhập Google (mã ${e.statusCode}).\n" +
                            "Vui lòng kiểm tra:\n" +
                            "1. SHA-1 fingerprint đã được thêm vào Google Cloud Console\n" +
                            "2. Package name: $packageName\n" +
                            "3. Client ID: ${BuildConfig.GOOGLE_CLIENT_ID}"
                        }
                        else -> {
                            Log.e("GoogleSignInError", "Error code ${e.statusCode}: ${e.message}", e)
                            "Đăng nhập Google thất bại (mã ${e.statusCode}): ${e.message}"
                        }
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }

        setContent {
            SORMS_appTheme {
                val scope = rememberCoroutineScope()
                AppNavigation(
                    authViewModel = authViewModel,
                    onGoogleSignInClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
                    onLogout = {
                        scope.launch {
                            AuthRepository(applicationContext).logout()
                            googleSignInClient.signOut()
                            authViewModel.reset()
                        }
                    }
                )
            }
        }
    }
}
