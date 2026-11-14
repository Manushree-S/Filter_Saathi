package org.filtersaathi.app.ui.officer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.filtersaathi.app.data.repository.FakeRepository
import org.filtersaathi.app.domain.model.OfficerOverviewData
import javax.inject.Inject

@HiltViewModel
class OfficerOverviewViewModel @Inject constructor(
    private val repository: FakeRepository
) : ViewModel() {

    private val _officerData = MutableLiveData<OfficerOverviewData>()
    val officerData: LiveData<OfficerOverviewData> = _officerData

    init {
        loadData()
    }

    fun loadData() {
        _officerData.value = repository.getOfficerOverviewData()
    }
}
