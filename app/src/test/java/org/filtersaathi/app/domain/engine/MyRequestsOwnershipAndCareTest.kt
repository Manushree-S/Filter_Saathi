package org.filtersaathi.app.domain.engine

import org.filtersaathi.app.domain.model.BookingRequest
import org.filtersaathi.app.domain.model.FilterCareStatus
import org.filtersaathi.app.domain.model.ProblemReport
import org.filtersaathi.app.domain.model.ProblemType
import org.filtersaathi.app.domain.model.RequestOrReportItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MyRequestsOwnershipAndCareTest {

    @Test
    fun testOwnershipFilter_returnsOnlyCurrentUsersItems() {
        val userPhone = "+919876543210"
        val otherPhone = "+919999999999"

        val item1 = RequestOrReportItem.Booking(
            BookingRequest("FS-2026-1001", "Bengaluru Urban", "South", "Village 1", "Sump 1", "560001", ownerPhone = userPhone)
        )
        val item2 = RequestOrReportItem.Booking(
            BookingRequest("FS-2026-1002", "Mysuru", "North", "Village 2", "Sump 2", "570001", ownerPhone = otherPhone)
        )
        val item3 = RequestOrReportItem.Report(
            ProblemReport("FS-R-2001", ProblemType.LEAK, "Village 1", ownerPhone = userPhone)
        )
        val item4 = RequestOrReportItem.Report(
            ProblemReport("FS-R-2002", ProblemType.OTHER, "Village 3", ownerPhone = null)
        )

        val allItems = listOf(item1, item2, item3, item4)

        // Filter for userPhone
        val userItems = allItems.filter { it.ownerPhone == userPhone }
        assertEquals(2, userItems.size)
        assertTrue(userItems.all { it.ownerPhone == userPhone })

        // Filter for otherPhone
        val otherItems = allItems.filter { it.ownerPhone == otherPhone }
        assertEquals(1, otherItems.size)
        assertEquals("FS-2026-1002", otherItems[0].id)

        // Signed out (null)
        val signedOutUserPhone: String? = null
        val signedOutItems = if (signedOutUserPhone == null) emptyList() else allItems.filter { it.ownerPhone == signedOutUserPhone }
        assertTrue(signedOutItems.isEmpty())
    }

    @Test
    fun testStatusRule_exactBoundaries() {
        assertEquals(FilterCareStatus.URGENT, RequestCareRuleEngine.evaluateStatus(-1)) // Overdue
        assertEquals(FilterCareStatus.URGENT, RequestCareRuleEngine.evaluateStatus(0))  // Overdue today
        assertEquals(FilterCareStatus.URGENT, RequestCareRuleEngine.evaluateStatus(7))  // Exactly 7 days
        assertEquals(FilterCareStatus.SOON, RequestCareRuleEngine.evaluateStatus(8))    // 8 days -> Soon
        assertEquals(FilterCareStatus.SOON, RequestCareRuleEngine.evaluateStatus(30))   // Exactly 30 days
        assertEquals(FilterCareStatus.OK, RequestCareRuleEngine.evaluateStatus(31))     // 31 days -> OK
    }
}
