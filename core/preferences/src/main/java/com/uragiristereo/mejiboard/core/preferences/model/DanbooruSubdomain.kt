package com.uragiristereo.mejiboard.core.preferences.model

enum class DanbooruSubdomain {
    DANBOORU,
    SAFEBOORU,
    HIJIRIBE,
    SONOHARA,
    SHIMA,
    SAITOU,
    KAGAMIHARA;

    val baseUrl = "https://${this.name.lowercase()}.donmai.us"
}
