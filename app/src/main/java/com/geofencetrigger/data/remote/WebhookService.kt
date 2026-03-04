package com.geofencetrigger.data.remote

import com.geofencetrigger.data.remote.dto.WebhookPayload
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface WebhookService {

    @POST
    suspend fun sendWebhook(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Body payload: WebhookPayload
    ): Response<ResponseBody>
}
