package org.filtersaathi.app.ui.teststrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.TestStripResult
import javax.inject.Inject

@HiltViewModel
class TestStripViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    val testStripResult: TestStripResult = repository.fixedTestStripResult

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    private val _hasResults = MutableLiveData(false)
    val hasResults: LiveData<Boolean> = _hasResults

    fun onScanStarted() {
        _isScanning.value = true
        _hasResults.value = false
    }

    fun onScanCompleted() {
        _isScanning.value = false
        _hasResults.value = true
    }

    fun resetScan() {
        _isScanning.value = false
        _hasResults.value = false
    }
}
