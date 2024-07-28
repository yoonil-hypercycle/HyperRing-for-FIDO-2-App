package com.hyperring.core.api

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import java.io.IOException

data class RequestBodyData(
    val uid: String,
    val nfccounter: String,
    val cmac: String
)

class IssuanceID {
    fun req(onResult: (String) -> Unit) {
        Thread {
            // Create the JSON body
            val requestBodyData = RequestBodyData(
                uid = "04C767F2066180",
                nfccounter = "000001",
                cmac = "54A45B2C3A558765"
            )

            val gson = Gson()
            val jsonBodyString = gson.toJson(requestBodyData)

            // Create the request body
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody: RequestBody = jsonBodyString.toRequestBody(mediaType)

            // Build the request
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.heroring.io:8001/v1/hyperring-id/issuance")
                .post(requestBody)
                .build()

            // Send the request and handle the response
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                // Pass the response body to the callback function
                val responseBody = response.body!!.string()
                onResult(responseBody)

                // Print the response body
                println("[YOONIL] API Response: $responseBody")
            }
        }.start()
    }
}
