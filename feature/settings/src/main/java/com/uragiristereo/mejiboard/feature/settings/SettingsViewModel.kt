package com.uragiristereo.mejiboard.feature.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uragiristereo.mejiboard.core.data.RatingFilter
import com.uragiristereo.mejiboard.core.model.booru.BooruSources
import com.uragiristereo.mejiboard.core.model.preferences.PreferenceItem
import com.uragiristereo.mejiboard.core.preferences.PreferencesRepository
import com.uragiristereo.mejiboard.core.preferences.model.Preferences
import com.uragiristereo.mejiboard.core.preferences.model.RatingPreference
import com.uragiristereo.mejiboard.core.resources.R
import com.uragiristereo.mejiboard.feature.settings.preference.BottomSheetPreferenceData
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    var preferences by mutableStateOf(Preferences())
        private set

    init {
        preferencesRepository.flowData.onEach {
            preferences = it
        }.launchIn(viewModelScope)
    }

    fun updatePreferences(data: (Preferences) -> Preferences) {
        viewModelScope.launch {
            preferencesRepository.update(data(preferences))
        }
    }

    var booruSources by mutableStateOf(
        value = BottomSheetPreferenceData(
            preferenceKey = "booru_sources",
            preferenceTextResId = R.string.settings_default_booru_source_select,
            items = BooruSources.toPreferenceItemList(),
            selectedItem = BooruSources.getBooruByKey(preferences.booru)?.toPreferenceItem() ?: BooruSources.Gelbooru.toPreferenceItem(),
        ),
    )
        private set

    var ratingFilters by mutableStateOf(
        value = BottomSheetPreferenceData(
            preferenceKey = "rating_filters",
            preferenceTextResId = R.string.settings_booru_listing_mode_select,
            items = when {
                preferences.developerMode -> RatingPreference.items
                else -> RatingPreference.itemsSafe
            },
            selectedItem = RatingPreference.getItemByKey(
                key = RatingFilter.itemsToKey(preferences.ratingFilters),
            ),
        ),
    )
        private set

    fun setBottomSheetPreferenceState(
        preferenceKey: String,
        selectedItem: PreferenceItem?,
    ) {
        when (preferenceKey) {
            booruSources.preferenceKey -> {
                booruSources = booruSources.copy(selectedItem = selectedItem)

                updatePreferences {
                    it.copy(
                        booru = booruSources.selectedItem?.key ?: BooruSources.Gelbooru.key,
                    )
                }
            }

            ratingFilters.preferenceKey -> {
                ratingFilters = ratingFilters.copy(selectedItem = selectedItem)

                updatePreferences {
                    it.copy(
                        ratingFilters = RatingFilter.getItemsByKey(key = ratingFilters.selectedItem?.key ?: RatingPreference.KEY_SAFE)
                    )
                }
            }
        }
    }
}
