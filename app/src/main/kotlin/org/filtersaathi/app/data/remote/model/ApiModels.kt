package org.filtersaathi.app.data.remote.model

data class ApiHealthResponse(
    val status: String,
    val serverTime: Long
)

data class SumpDeviceRequest(
    val villageId: String,
    val contactName: String,
    val contactPhone: String,
    val preferredDate: String
)

data class SumpDeviceResponse(
    val requestId: String,
    val status: String, // "REQUESTED", "SCHEDULED"
    val scheduledDate: String?
)
