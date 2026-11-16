package com.example.xpressocafe.network

import android.os.Handler
import android.os.Looper
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

/**
 * Reusable Network Helper for executing asynchronous HTTP GET and POST requests.
 */
object NetworkHelper {

    private val executor = Executors.newFixedThreadPool(4)
    private val mainHandler = Handler(Looper.getMainLooper())

    interface NetworkCallback {
        fun onSuccess(responseString: String)
        fun onError(errorMessage: String)
    }

    /**
     * Executes an HTTP GET request asynchronously.
     */
    fun sendGetRequest(urlString: String, callback: NetworkCallback) {
        executor.execute {
            var conn: HttpURLConnection? = null
            try {
                val url = URL(urlString)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 7000
                conn.readTimeout = 7000

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseString = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    mainHandler.post { callback.onSuccess(responseString) }
                } else {
                    mainHandler.post { callback.onError("Server returned HTTP error code: $responseCode") }
                }
            } catch (e: Exception) {
                val msg = e.localizedMessage ?: e.message ?: "Network connection failed"
                mainHandler.post { callback.onError("Connection Failed: $msg\nCheck XAMPP server connection.") }
            } finally {
                conn?.disconnect()
            }
        }
    }

    /**
     * Executes an HTTP POST request with URL-encoded form parameters or raw JSON.
     */
    fun sendPostRequest(
        urlString: String,
        postParams: Map<String, String>? = null,
        jsonBody: String? = null,
        callback: NetworkCallback
    ) {
        executor.execute {
            var conn: HttpURLConnection? = null
            try {
                val url = URL(urlString)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 7000
                conn.readTimeout = 7000

                if (jsonBody != null) {
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    val writer = OutputStreamWriter(conn.outputStream, Charsets.UTF_8)
                    writer.write(jsonBody)
                    writer.flush()
                    writer.close()
                } else if (postParams != null) {
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    val postData = postParams.map { (key, value) ->
                        "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
                    }.joinToString("&")

                    val writer = OutputStreamWriter(conn.outputStream, Charsets.UTF_8)
                    writer.write(postData)
                    writer.flush()
                    writer.close()
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseString = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    mainHandler.post { callback.onSuccess(responseString) }
                } else {
                    mainHandler.post { callback.onError("Server returned HTTP error code: $responseCode") }
                }
            } catch (e: Exception) {
                val msg = e.localizedMessage ?: e.message ?: "Network request failed"
                mainHandler.post { callback.onError("Connection Failed: $msg\nCheck XAMPP server connection.") }
            } finally {
                conn?.disconnect()
            }
        }
    }
}
