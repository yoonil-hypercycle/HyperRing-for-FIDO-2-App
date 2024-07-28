package com.example.hyperringidappsimple.api

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SampleRequest {
    fun req( onResult: (String) -> Unit) {
        Thread {
            // Make an API Request
            // OkHttpClient 인스턴스를 생성합니다.
            val client = OkHttpClient()

            // 요청을 생성합니다.
            val request = Request.Builder()
                .url("https://api.heroring.io:8001/v1/hyperring-id/issuance")
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