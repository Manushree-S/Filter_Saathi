package org.filtersaathi.app.domain.model

/**
 * Water filter installation data for maintenance tracking.
 * Labelled explicitly as Sample data.
 */
data class SampleFilter(
    val filterId: String,
    val villageName: String,
    val installDate: String,
    val daysUntilService: Int,
    val isSampleData: Boolean = true
)
