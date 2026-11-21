package org.filtersaathi.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationAddressResolverTest {

    @Test
    fun testPinCodeValidation_exactSixDigits() {
        assertTrue(LocationAddressResolver.isValidPinCode("560001"))
        assertTrue(LocationAddressResolver.isValidPinCode("570001"))
        assertFalse(LocationAddressResolver.isValidPinCode("56000"))
        assertFalse(LocationAddressResolver.isValidPinCode("5600001"))
        assertFalse(LocationAddressResolver.isValidPinCode("56000A"))
        assertFalse(LocationAddressResolver.isValidPinCode(null))
        assertFalse(LocationAddressResolver.isValidPinCode(""))
    }

    @Test
    fun testDistrictAliasMatcher_allRequiredAliases() {
        assertEquals("Bengaluru Urban", LocationAddressResolver.matchDistrict("Bangalore Urban"))
        assertEquals("Bengaluru Rural", LocationAddressResolver.matchDistrict("Bangalore Rural"))
        assertEquals("Bengaluru South", LocationAddressResolver.matchDistrict("Ramanagara"))
        assertEquals("Mysuru", LocationAddressResolver.matchDistrict("Mysore"))
        assertEquals("Belagavi", LocationAddressResolver.matchDistrict("Belgaum"))
        assertEquals("Ballari", LocationAddressResolver.matchDistrict("Bellary"))
        assertEquals("Vijayapura", LocationAddressResolver.matchDistrict("Bijapur"))
        assertEquals("Kalaburagi", LocationAddressResolver.matchDistrict("Gulbarga"))
        assertEquals("Shivamogga", LocationAddressResolver.matchDistrict("Shimoga"))
        assertEquals("Tumakuru", LocationAddressResolver.matchDistrict("Tumkur"))
        assertEquals("Chikkamagaluru", LocationAddressResolver.matchDistrict("Chikmagalur"))
        assertEquals("Chikkaballapura", LocationAddressResolver.matchDistrict("Chikballapur"))
        assertEquals("Davanagere", LocationAddressResolver.matchDistrict("Davangere"))
        assertEquals("Bagalkot", LocationAddressResolver.matchDistrict("Bagalkote"))
        assertEquals("Kodagu", LocationAddressResolver.matchDistrict("Coorg"))
        assertEquals("Uttara Kannada", LocationAddressResolver.matchDistrict("North Canara"))
        assertEquals("Vijayanagara", LocationAddressResolver.matchDistrict("Hospet"))
        assertEquals("Dakshina Kannada", LocationAddressResolver.matchDistrict("Mangalore"))
    }

    @Test
    fun testDistrictAliasMatcher_withSuffixes() {
        assertEquals("Mysuru", LocationAddressResolver.matchDistrict("Mysore District"))
        assertEquals("Udupi", LocationAddressResolver.matchDistrict("Udupi District"))
        assertEquals("Belagavi", LocationAddressResolver.matchDistrict("Belgaum Zilla"))
    }

    @Test
    fun testDistrictAliasMatcher_unknownDistrict() {
        assertNull(LocationAddressResolver.matchDistrict("Unknown Place"))
        assertNull(LocationAddressResolver.matchDistrict(null))
    }
}
