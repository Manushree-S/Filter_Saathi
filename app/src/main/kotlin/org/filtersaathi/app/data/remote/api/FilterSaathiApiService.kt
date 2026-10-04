package org.filtersaathi.app.data.remote.api

import org.filtersaathi.app.data.remote.model.ApiHealthResponse
import org.filtersaathi.app.data.remote.model.SumpDeviceRequest
import org.filtersaathi.app.data.remote.model.SumpDeviceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FilterSaathiApiService {

    @GET("api/v1/health")
    suspend fun checkHealth(): Response<ApiHealthResponse>

    @POST("api/v1/sump-devices/request")
    suspend fun requestSumpInstallation(
        @Body request: SumpDeviceRequest
    ): Response<SumpDeviceResponse>
}
