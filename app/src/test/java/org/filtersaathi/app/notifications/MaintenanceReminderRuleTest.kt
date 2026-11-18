package org.filtersaathi.app.notifications

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MaintenanceReminderRuleTest {

    @Test
    fun evaluateStatus_over30Days_returnsOk() {
        assertEquals(MaintenanceStatus.OK, MaintenanceReminderRule.evaluateStatus(31))
        assertEquals(MaintenanceStatus.OK, MaintenanceReminderRule.evaluateStatus(75))
        assertEquals(MaintenanceStatus.OK, MaintenanceReminderRule.evaluateStatus(180))
    }

    @Test
    fun evaluateStatus_exactly30Days_returnsOk() {
        assertEquals(MaintenanceStatus.OK, MaintenanceReminderRule.evaluateStatus(30))
    }

    @Test
    fun evaluateStatus_under30Days_returnsSoon() {
        assertEquals(MaintenanceStatus.SOON, MaintenanceReminderRule.evaluateStatus(29))
        assertEquals(MaintenanceStatus.SOON, MaintenanceReminderRule.evaluateStatus(18))
        assertEquals(MaintenanceStatus.SOON, MaintenanceReminderRule.evaluateStatus(7))
    }

    @Test
    fun evaluateStatus_under7Days_returnsUrgent() {
        assertEquals(MaintenanceStatus.URGENT, MaintenanceReminderRule.evaluateStatus(6))
        assertEquals(MaintenanceStatus.URGENT, MaintenanceReminderRule.evaluateStatus(1))
        assertEquals(MaintenanceStatus.URGENT, MaintenanceReminderRule.evaluateStatus(0))
        assertEquals(MaintenanceStatus.URGENT, MaintenanceReminderRule.evaluateStatus(-3))
    }

    @Test
    fun isQuietHour_duringDay_returnsFalse() {
        assertFalse(MaintenanceReminderRule.isQuietHour(7))  // 07:00 morning boundary
        assertFalse(MaintenanceReminderRule.isQuietHour(8))
        assertFalse(MaintenanceReminderRule.isQuietHour(12)) // Noon
        assertFalse(MaintenanceReminderRule.isQuietHour(18))
        assertFalse(MaintenanceReminderRule.isQuietHour(20)) // 8 PM
    }

    @Test
    fun isQuietHour_atNight_returnsTrue() {
        assertTrue(MaintenanceReminderRule.isQuietHour(21)) // 21:00 night boundary
        assertTrue(MaintenanceReminderRule.isQuietHour(22))
        assertTrue(MaintenanceReminderRule.isQuietHour(23))
        assertTrue(MaintenanceReminderRule.isQuietHour(0))  // Midnight
        assertTrue(MaintenanceReminderRule.isQuietHour(3))
        assertTrue(MaintenanceReminderRule.isQuietHour(6))  // 06:00 AM
    }
}
