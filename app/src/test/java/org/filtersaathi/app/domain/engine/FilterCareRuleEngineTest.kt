package org.filtersaathi.app.domain.engine

import org.filtersaathi.app.domain.model.FilterCareStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterCareRuleEngineTest {

    @Test
    fun testEvaluateStatus_overdue_returnsUrgent() {
        assertEquals(FilterCareStatus.URGENT, FilterCareRuleEngine.evaluateStatus(-5))
        assertEquals(FilterCareStatus.URGENT, FilterCareRuleEngine.evaluateStatus(0))
    }

    @Test
    fun testEvaluateStatus_exactlySevenDays_returnsUrgent() {
        assertEquals(FilterCareStatus.URGENT, FilterCareRuleEngine.evaluateStatus(7))
    }

    @Test
    fun testEvaluateStatus_betweenSevenAndThirtyDays_returnsSoon() {
        assertEquals(FilterCareStatus.SOON, FilterCareRuleEngine.evaluateStatus(8))
        assertEquals(FilterCareStatus.SOON, FilterCareRuleEngine.evaluateStatus(15))
    }

    @Test
    fun testEvaluateStatus_exactlyThirtyDays_returnsSoon() {
        assertEquals(FilterCareStatus.SOON, FilterCareRuleEngine.evaluateStatus(30))
    }

    @Test
    fun testEvaluateStatus_moreThanThirtyDays_returnsOk() {
        assertEquals(FilterCareStatus.OK, FilterCareRuleEngine.evaluateStatus(31))
        assertEquals(FilterCareStatus.OK, FilterCareRuleEngine.evaluateStatus(90))
    }

    @Test
    fun testCalculateDaysLeft_dateDifference() {
        val daysLeft = FilterCareRuleEngine.calculateDaysLeft(90, "2026-10-01", "2026-11-01")
        assertEquals(59, daysLeft)
    }
}
