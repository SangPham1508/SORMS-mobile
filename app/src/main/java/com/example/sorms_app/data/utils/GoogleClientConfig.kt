package com.example.sorms_app.data.utils

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.IOException

/**
 * Utility class để đọc thông tin Google OAuth client từ file JSON
 */
object GoogleClientConfig {
    private const val TAG = "GoogleClientConfig"
    private const val ASSETS_FILE_NAME = "google_client_secret.json"
    
    /**
     * Đọc Web Client ID (OAuth client type: Web application) từ file JSON trong assets.
     *
     * File dạng:
     * { "web_client_id": "...apps.googleusercontent.com" }
     */
    fun getWebClientId(context: Context): String? {
        return try {
            val jsonString = context.assets.open(ASSETS_FILE_NAME)
                .bufferedReader()
                .use { it.readText() }

            val jsonObject = JSONObject(jsonString)
            val clientId = jsonObject.getString("web_client_id")

            Log.d(TAG, "Đã đọc web_client_id từ file: $clientId")
            clientId
        } catch (e: IOException) {
            Log.e(TAG, "Không thể đọc file $ASSETS_FILE_NAME từ assets", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi parse file JSON (web_client_id)", e)
            null
        }
    }
}

