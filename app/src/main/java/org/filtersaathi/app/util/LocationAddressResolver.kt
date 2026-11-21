package org.filtersaathi.app.util

import android.location.Address
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ResolvedAddress(
    val villageOrPanchayat: String? = null,
    val sumpLocation: String? = null,
    val postalCode: String? = null,
    val district: String? = null,
    val isOutsideKarnataka: Boolean = false
)

object LocationAddressResolver {

    private val OFFICIAL_DISTRICTS = listOf(
        "Bagalkot", "Ballari", "Belagavi", "Bengaluru Rural", "Bengaluru South",
        "Bengaluru Urban", "Bidar", "Chamarajanagar", "Chikkaballapura", "Chikkamagaluru",
        "Chitradurga", "Dakshina Kannada", "Davanagere", "Dharwad", "Gadag",
        "Hassan", "Haveri", "Kalaburagi", "Kodagu", "Kolar",
        "Koppal", "Mandya", "Mysuru", "Raichur", "Shivamogga",
        "Tumakuru", "Udupi", "Uttara Kannada", "Vijayanagara", "Vijayapura", "Yadgir"
    )

    private val ALIAS_MAP = mapOf(
        "bangalore urban" to "Bengaluru Urban",
        "bangalore rural" to "Bengaluru Rural",
        "ramanagara" to "Bengaluru South",
        "mysore" to "Mysuru",
        "belgaum" to "Belagavi",
        "bellary" to "Ballari",
        "bijapur" to "Vijayapura",
        "gulbarga" to "Kalaburagi",
        "shimoga" to "Shivamogga",
        "tumkur" to "Tumakuru",
        "chikmagalur" to "Chikkamagaluru",
        "chikballapur" to "Chikkaballapura",
        "davangere" to "Davanagere",
        "bagalkote" to "Bagalkot",
        "coorg" to "Kodagu",
        "north canara" to "Uttara Kannada",
        "hospet" to "Vijayanagara",
        "mangalore" to "Dakshina Kannada"
    )

    @JvmStatic
    fun resolveAddress(address: Address): ResolvedAddress {
        val adminArea = address.adminArea ?: ""
        if (adminArea.isNotBlank() && !adminArea.lowercase().contains("karnataka")) {
            return ResolvedAddress(isOutsideKarnataka = true)
        }

        val villageOrPanchayat = address.locality?.takeIf { it.isNotBlank() }
            ?: address.subLocality?.takeIf { it.isNotBlank() }

        val sumpLocation = address.thoroughfare?.takeIf { it.isNotBlank() }
            ?: address.featureName?.takeIf { it.isNotBlank() }
            ?: (0..address.maxAddressLineIndex).mapNotNull { address.getAddressLine(it) }.firstOrNull { it.isNotBlank() }

        val pinCode = address.postalCode?.takeIf { isValidPinCode(it) }

        val rawDistrict = address.subAdminArea?.takeIf { it.isNotBlank() }
            ?: address.locality?.takeIf { it.isNotBlank() }
            ?: address.subLocality?.takeIf { it.isNotBlank() }

        val matchedDistrict = matchDistrict(rawDistrict)

        return ResolvedAddress(
            villageOrPanchayat = villageOrPanchayat,
            sumpLocation = sumpLocation,
            postalCode = pinCode,
            district = matchedDistrict,
            isOutsideKarnataka = false
        )
    }

    @JvmStatic
    fun reverseGeocodeHttp(lat: Double, lng: Double): ResolvedAddress? {
        try {
            val urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lng&accept-language=en"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "FilterSaathiApp/1.0")
            connection.connectTimeout = 6000
            connection.readTimeout = 6000

            if (connection.responseCode == 200) {
                val stream = connection.inputStream
                val jsonString = stream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonString)
                val addrObj = json.optJSONObject("address") ?: return null

                val state = addrObj.optString("state", "")
                if (state.isNotBlank() && !state.lowercase().contains("karnataka")) {
                    return ResolvedAddress(isOutsideKarnataka = true)
                }

                val villageOrPanchayat = addrObj.optString("village", "").ifBlank {
                    addrObj.optString("town", "").ifBlank {
                        addrObj.optString("suburb", "").ifBlank {
                            addrObj.optString("neighbourhood", "").ifBlank {
                                addrObj.optString("city", "")
                            }
                        }
                    }
                }.takeIf { it.isNotBlank() }

                val sumpLocation = addrObj.optString("road", "").ifBlank {
                    addrObj.optString("amenity", "").ifBlank {
                        addrObj.optString("building", "").ifBlank {
                            addrObj.optString("suburb", "")
                        }
                    }
                }.takeIf { it.isNotBlank() }

                val rawPostcode = addrObj.optString("postcode", "")
                val postalCode = rawPostcode.takeIf { isValidPinCode(it) }

                val rawDistrict = addrObj.optString("county", "").ifBlank {
                    addrObj.optString("state_district", "").ifBlank {
                        addrObj.optString("city", "")
                    }
                }
                val district = matchDistrict(rawDistrict)

                return ResolvedAddress(
                    villageOrPanchayat = villageOrPanchayat,
                    sumpLocation = sumpLocation,
                    postalCode = postalCode,
                    district = district,
                    isOutsideKarnataka = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun isValidPinCode(pin: String?): Boolean {
        return pin != null && pin.trim().matches(Regex("^\\d{6}$"))
    }

    @JvmStatic
    fun matchDistrict(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.lowercase()
            .replace("district", "")
            .replace("zilla", "")
            .trim()

        // Check alias map first
        ALIAS_MAP[cleaned]?.let { return it }

        // Check direct match in official list (case-insensitive)
        OFFICIAL_DISTRICTS.firstOrNull { it.equals(cleaned, ignoreCase = true) }?.let { return it }

        // Try matching without spaces or punctuation
        val normalizedCleaned = cleaned.replace(Regex("[^a-z]"), "")
        OFFICIAL_DISTRICTS.firstOrNull {
            it.lowercase().replace(Regex("[^a-z]"), "") == normalizedCleaned
        }?.let { return it }

        return null
    }
}
