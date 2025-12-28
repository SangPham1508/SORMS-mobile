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
import com.example.sorms_app.data.repository.AuthRepository
import com.example.sorms_app.data.utils.GoogleClientConfig
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
        enableEdgeToEdge()

        // Đọc WEB client id (OAuth client type: Web application) từ file JSON
        val webClientId = GoogleClientConfig.getWebClientId(this)

        if (webClientId.isNullOrBlank()) {
            Log.e("GoogleSignInError", "Không thể đọc web_client_id từ file google_client_secret.json")
            Toast.makeText(
                this,
                "Lỗi cấu hình: Không tìm thấy WEB Client ID. Vui lòng kiểm tra file google_client_secret.json trong assets.",
                Toast.LENGTH_LONG
            ).show()
            // Vẫn tiếp tục khởi tạo nhưng sẽ fail khi đăng nhập
        } else {
            Log.d("GoogleSignInDebug", "Đã đọc WEB_CLIENT_ID từ file JSON: $webClientId")
        }

        val finalClientId = webClientId ?: "" // Fallback empty string để tránh crash

        // Dùng Authorization Code flow (giống web): xin serverAuthCode để gửi về backend exchange token
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(finalClientId, true)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val authCode = account.serverAuthCode
                    Log.d("GoogleSignInDebug", "Received serverAuthCode: $authCode")
                    if (authCode.isNullOrBlank()) {
                        Toast.makeText(this, "Không lấy được authorization code từ Google", Toast.LENGTH_SHORT).show()
                        return@registerForActivityResult
                    }
                    authViewModel.loginWithAuthCode(authCode)
                } catch (e: ApiException) {
                    val message = when (e.statusCode) {
                        12501 -> "Bạn đã huỷ chọn tài khoản Google"
                        12500, 10 -> {
                            val errorClientId = GoogleClientConfig.getWebClientId(this) ?: "Không tìm thấy"
                            Log.e("GoogleSignInError", "DEVELOPER_ERROR (${e.statusCode}): ${e.message}", e)
                            Log.e("GoogleSignInError", "Client ID used: $errorClientId")
                            Log.e("GoogleSignInError", "Package name: ${packageName}")
                            "Lỗi cấu hình đăng nhập Google (mã ${e.statusCode}).\n" +
                            "Vui lòng kiểm tra:\n" +
                            "1. SHA-1 fingerprint đã được thêm vào Google Cloud Console\n" +
                            "2. Package name: $packageName\n" +
                            "3. Client ID: $errorClientId\n" +
                            "4. File google_client_secret.json trong assets"
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
