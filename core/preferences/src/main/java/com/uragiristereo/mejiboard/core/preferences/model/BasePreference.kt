package com.uragiristereo.mejiboard.core.preferences.model

import com.uragiristereo.mejiboard.core.model.preferences.PreferenceItem

interface BasePreference {
    val items: List<PreferenceItem>

    fun getItemByKey(key: String): PreferenceItem? {
        return items.firstOrNull { it.key == key }
    }
}