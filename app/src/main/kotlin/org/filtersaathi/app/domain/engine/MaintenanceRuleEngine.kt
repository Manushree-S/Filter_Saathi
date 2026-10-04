package org.filtersaathi.app.domain.engine

import org.filtersaathi.app.data.local.entity.FilterEntity
import org.filtersaathi.app.domain.model.WaterQualityParameter
import org.filtersaathi.app.domain.model.WaterQualityStatus

object MaintenanceRuleEngine {

    /**
     * Calculates the overall health score (0-100) based on components and maintenance history.
     */
    @JvmStatic
    fun calculateFilterHealthScore(
        cartridgeDaysLeft: Int,
        uvLampDaysLeft: Int,
        membraneDaysLeft: Int,
        hasOpenFault: Boolean
    ): Int {
        if (hasOpenFault) return 35 // Severe drop if there's an active breakdown

        // Cartridge weight 25%, UV lamp weight 35%, Membrane weight 40%
        val cartridgeScore = (cartridgeDaysLeft.toFloat() / 90f).coerceIn(0f, 1f) * 25
        val uvScore = (uvLampDaysLeft.toFloat() / 365f).coerceIn(0f, 1f) * 35
        val membraneScore = (membraneDaysLeft.toFloat() / 730f).coerceIn(0f, 1f) * 40

        val total = (cartridgeScore + uvScore + membraneScore).toInt()
        return total.coerceIn(0, 100)
    }

    /**
     * Evaluates water safety parameters against Jal Jeevan Mission / IS 10500 standards.
     */
    @JvmStatic
    fun evaluateTds(tdsPpm: Float): WaterQualityParameter {
        val status = when {
            tdsPpm <= 300f -> WaterQualityStatus.SAFE
            tdsPpm <= 500f -> WaterQualityStatus.WATCH
            else -> WaterQualityStatus.UNSAFE
        }
        return WaterQualityParameter("TDS", "${tdsPpm.toInt()} ppm", status)
    }

    @JvmStatic
    fun evaluatePh(ph: Float): WaterQualityParameter {
        val status = when {
            ph in 6.5f..8.5f -> WaterQualityStatus.SAFE
            ph in 6.0f..9.0f -> WaterQualityStatus.WATCH
            else -> WaterQualityStatus.UNSAFE
        }
        return WaterQualityParameter("pH", String.format("%.1f", ph), status)
    }

    @JvmStatic
    fun evaluateChlorine(chlorineMgL: Float): WaterQualityParameter {
        val status = when {
            chlorineMgL in 0.2f..0.5f -> WaterQualityStatus.SAFE
            chlorineMgL in 0.1f..1.0f -> WaterQualityStatus.WATCH
            else -> WaterQualityStatus.UNSAFE
        }
        return WaterQualityParameter("Chlorine", String.format("%.1f mg/L", chlorineMgL), status)
    }

    @JvmStatic
    fun evaluateNitrate(nitrateMgL: Float): WaterQualityParameter {
        val status = when {
            nitrateMgL <= 45f -> WaterQualityStatus.SAFE
            nitrateMgL <= 50f -> WaterQualityStatus.WATCH
            else -> WaterQualityStatus.UNSAFE
        }
        return WaterQualityParameter("Nitrate", "${nitrateMgL.toInt()} mg/L", status)
    }
}
