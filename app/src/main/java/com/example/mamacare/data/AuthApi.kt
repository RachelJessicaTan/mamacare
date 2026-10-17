package com.example.mamacare.data

import com.example.mamacare.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AuthException(val statusCode: Int, message: String) : Exception(message)

object AuthApi {

    private const val BASE_URL = "http://10.0.2.2:4000/api"
    private const val TIMEOUT_MS = 10_000

    suspend fun register(name: String, email: String, password: String): User =
        post(
            "/register",
            JSONObject()
                .put("name", name)
                .put("email", email)
                .put("password", password),
        )

    suspend fun login(email: String, password: String): User =
        post(
            "/login",
            JSONObject()
                .put("email", email)
                .put("password", password),
        )

    private suspend fun post(path: String, body: JSONObject): User = withContext(Dispatchers.IO) {
        val connection = URL(BASE_URL + path).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { it.write(body.toString().toByteArray()) }

            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
            val responseText = stream?.bufferedReader()?.use { it.readText() }
                ?: throw IOException("Empty response")

            val json = try {
                JSONObject(responseText)
            } catch (e: JSONException) {
                throw AuthException(statusCode, "Unexpected server response.")
            }
            if (statusCode !in 200..299) {
                throw AuthException(statusCode, json.optString("message", "Something went wrong."))
            }
            json.getJSONObject("user").toUser()
        } finally {
            connection.disconnect()
        }
    }

    private fun JSONObject.toUser() = User(
        id = getLong("id"),
        name = getString("name"),
        email = getString("email"),
    )
}
