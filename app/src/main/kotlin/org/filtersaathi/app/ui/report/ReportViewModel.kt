package org.filtersaathi.app.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.R
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.ProblemReport
import org.filtersaathi.app.domain.model.ProblemType
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    private val _latestReport = MutableLiveData<ProblemReport?>(null)
    val latestReport: LiveData<ProblemReport?> = _latestReport

    private val _errorMessageRes = MutableLiveData<Int?>(null)
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    fun submitReport(problemIndex: Int, village: String, details: String, hasPhoto: Boolean): Boolean {
        if (problemIndex !in 0..3 || village.trim().isEmpty()) {
            _errorMessageRes.value = R.string.ui_err_rep
            return false
        }

        val type = when (problemIndex) {
            0 -> ProblemType.BAD_TASTE_SMELL
            1 -> ProblemType.FILTER_NOT_WORKING
            2 -> ProblemType.LEAK
            else -> ProblemType.OTHER
        }

        val report = repository.submitProblemReport(
            problemType = type,
            village = village.trim(),
            details = details.trim(),
            hasPhoto = hasPhoto
        )

        _errorMessageRes.value = null
        _latestReport.value = report
        return true
    }

    fun resetReport() {
        _errorMessageRes.value = null
        _latestReport.value = null
    }
}
