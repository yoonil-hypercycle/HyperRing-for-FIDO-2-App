package com.example.hyperringidappsimple.api

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

data class AuthenticateRequestBodyData(
    val hyperringid: String
)

class AuthenticateRequest {
    fun req( onResult: (String) -> Unit) {
        Thread {
            val authenticateRequestBodyData = AuthenticateRequestBodyData(
                hyperringid="id:hyperring:123e4567-e89b-12d3-a456-426614174000"
            )

            val gson = Gson()
            val jsonBodyString = gson.toJson(authenticateRequestBodyData)

            // Create the request body
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody: RequestBody = jsonBodyString.toRequestBody(mediaType)

            // Make an API Request
            // OkHttpClient 인스턴스를 생성합니다.
            val client = OkHttpClient()

            // 요청을 생성합니다.
            val request = Request.Builder()
                .url("https://api.heroring.io:8001/v1/hyperring-id/authentication-request")
                .post(requestBody)
                .build()

            // 요청을 보내고 응답을 받습니다.
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                // Pass the response body to the callback function
                val responseBody = response.body!!.string()
                onResult(responseBody)

                // 응답 본문을 출력합니다.
                println("[YOONIL] API Response")
                //println(response.body!!.string())
            }
        }.start()
    }
}

data class ChallengeResponse(
    val `challenge-response`: String
)

data class AuthenticateResponseBodyData(
    val hyperring_id: String,
    val challenge: List<ChallengeResponse>
)

class AuthenticateResponse {
    fun req( onResult: (String) -> Unit) {
        Thread {
            val authenticateResponseBodyData = AuthenticateResponseBodyData(
                hyperring_id = "id:hyperring:123e4567-e89b-12d3-a456-426614174000",
                challenge = listOf(
                    ChallengeResponse(
                        `challenge-response` = "blBo8fSKhc/0jvIdN8YRHMIS0P8msaaFGFUqbXwxPiZs79BI1nymnB5nBx4rsQWuJX8pN/EBAGGU4fhL4JacdiGfUI6II803k3GWo3vZS/exTlniFYwOeZHmI+GPbZCNGg7NiPkfhDfXdhsIs3d694RqD0NEAgONPraOjDrd1TkoiDH1+U42kPaaefeDCGIB3SQDdA4Zm76MCo5TXNrIJzAX8oQqT0rRHnrAJ+zzmcIp+7O6R9Bnl7B+3+zT6vM/9fPLjWIyP4DJTy/UmIwi9/l2wd8w+oPyyeJLP4Ij2mH1t4jucBNfGOyQu+hDVdbLo8JPwYKE9Sp4qQjhSL9i3w=="
                    )
                )
            )

            val gson = Gson()
            val jsonBodyString = gson.toJson(authenticateResponseBodyData)

            // Create the request body
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody: RequestBody = jsonBodyString.toRequestBody(mediaType)

            // Make an API Request
            // OkHttpClient 인스턴스를 생성합니다.
            val client = OkHttpClient()

            // 요청을 생성합니다.
            val request = Request.Builder()
                .url("https://api.heroring.io:8001/v1/hyperring-id/authentication-response")
                .post(requestBody)
                .build()

            // 요청을 보내고 응답을 받습니다.
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                // Pass the response body to the callback function
                val responseBody = response.body!!.string()
                onResult(responseBody)

                // 응답 본문을 출력합니다.
                println("[YOONIL] API Response")
                //println(response.body!!.string())
            }
        }.start()
    }
}