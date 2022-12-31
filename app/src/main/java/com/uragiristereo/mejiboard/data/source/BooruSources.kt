package com.uragiristereo.mejiboard.data.source

import com.uragiristereo.mejiboard.R
import com.uragiristereo.mejiboard.data.preferences.entity.PreferenceItem
import com.uragiristereo.mejiboard.domain.entity.source.BooruSource

object BooruSources {
    val Gelbooru = BooruSource(
        key = "gelbooru",
        nameResId = R.string.gelbooru_label,
        domainResId = R.string.gelbooru_domain,
        dateFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy",
        webUrlPattern = "https://gelbooru.com/index.php?page=post&s=view&id={postId}",
    )
    val Danbooru = BooruSource(
        key = "danbooru",
        nameResId = R.string.danbooru_label,
        domainResId = R.string.danbooru_domain,
        dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        webUrlPattern = "https://danbooru.donmai.us/posts/{postId}",
    )
    val SafebooruOrg = BooruSource(
        key = "safebooruorg",
        nameResId = R.string.safebooruorg_label,
        domainResId = R.string.safebooruorg_domain,
        dateFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy",
        webUrlPattern = "https://safebooru.org/index.php?page=post&s=view&id={postId}",
    )

    val list = listOf(
        Gelbooru,
        Danbooru,
        SafebooruOrg,
    )

    val map = list.associate { it.toPair() }

    fun getBooruByKey(key: String): BooruSource? {
        return map[key]
    }

    fun toPreferenceItemList(): List<PreferenceItem> {
        return list.map { it.toPreferenceItem() }
    }
}
