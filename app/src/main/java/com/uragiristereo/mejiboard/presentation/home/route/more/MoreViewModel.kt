package com.uragiristereo.mejiboard.presentation.home.route.more

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uragiristereo.mejiboard.data.database.DatabaseRepository
import com.uragiristereo.mejiboard.data.source.BooruSources
import com.uragiristereo.mejiboard.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreViewModel(
    preferencesRepository: PreferencesRepository,
    databaseRepository: DatabaseRepository,
) : ViewModel() {
    var preferences by mutableStateOf(preferencesRepository.data)
        private set

    var selectedBooru by mutableStateOf(BooruSources.getBooruByKey(preferences.booru) ?: BooruSources.Gelbooru)
        private set

    var enabledFiltersCount by mutableStateOf(0)

    init {
        preferencesRepository.flowData
            .onEach {
                preferences = it
                selectedBooru = BooruSources.getBooruByKey(it.booru) ?: BooruSources.Gelbooru
            }
            .launchIn(viewModelScope)

        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.filtersDao().getEnabledFiltersCount()
                .collect {
                    withContext(Dispatchers.Main) {
                        enabledFiltersCount = it
                    }
                }
        }
    }
}
