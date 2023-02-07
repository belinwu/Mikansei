package com.uragiristereo.mejiboard.core.booru.source.yandere

import android.content.Context
import com.uragiristereo.mejiboard.core.booru.source.BooruSourceRepository
import com.uragiristereo.mejiboard.core.booru.source.yandere.model.toPostList
import com.uragiristereo.mejiboard.core.booru.source.yandere.model.toTagList
import com.uragiristereo.mejiboard.core.model.Constants
import com.uragiristereo.mejiboard.core.model.RatingFilter
import com.uragiristereo.mejiboard.core.model.booru.BooruSource
import com.uragiristereo.mejiboard.core.model.booru.post.PostsResult
import com.uragiristereo.mejiboard.core.model.booru.post.Rating
import com.uragiristereo.mejiboard.core.model.booru.tag.TagsResult
import okhttp3.OkHttpClient

class YandereRepository(
    context: Context,
    okHttpClient: OkHttpClient,
) : BooruSourceRepository {
    override val source = BooruSource.Yandere

    private val client = buildClient(
        baseUrl = source.baseUrl(context),
        okHttpClient = okHttpClient,
        service = YandereApi::class.java,
    )

    override suspend fun getPosts(tags: String, page: Int, filters: List<Rating>): PostsResult {
        // yande.re allows unlimited tags to search so we can filter ratings from server
        val filterTags = when (filters) {
            RatingFilter.NO_EXPLICIT -> " -rating:explicit"
            RatingFilter.UNFILTERED -> ""
            else -> " rating:safe"
        }

        val response = client.getPosts(
            tags = tags + filterTags,
            pageId = page + 1,
            postsPerPage = Constants.POSTS_PER_PAGE,
        )

        if (response.isSuccessful) {
            val posts = response.body()!!.toPostList()

            return PostsResult(
                data = posts,
                canLoadMore = posts.size == Constants.POSTS_PER_PAGE,
            )
        }

        return PostsResult(
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
        )
    }

    override suspend fun searchTerm(term: String, filters: List<Rating>): TagsResult {
        val response = client.searchTerm(term)

        if (response.isSuccessful) {
            return TagsResult(
                data = response.body()!!.toTagList(),
            )
        }

        return TagsResult(
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
        )
    }

    override suspend fun getTags(tags: List<String>): TagsResult {
        val tagsStr = tags.joinToString(separator = " ")

        val response = client.getRelatedTags(tagsStr)

        if (response.isSuccessful) {
            return TagsResult(
                data = response.body()!!.toTagList(),
            )
        }

        return TagsResult(
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
        )
    }
}
