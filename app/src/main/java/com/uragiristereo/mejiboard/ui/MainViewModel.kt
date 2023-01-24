package com.uragiristereo.mejiboard.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uragiristereo.mejiboard.core.data.Constants
import com.uragiristereo.mejiboard.core.data.util.NumberUtil
import com.uragiristereo.mejiboard.core.database.dao.session.SessionDao
import com.uragiristereo.mejiboard.core.download.DownloadRepository
import com.uragiristereo.mejiboard.core.download.model.DownloadResource
import com.uragiristereo.mejiboard.core.model.ShareOption
import com.uragiristereo.mejiboard.core.model.booru.post.Post
import com.uragiristereo.mejiboard.core.preferences.PreferencesRepository
import com.uragiristereo.mejiboard.core.preferences.model.Preferences
import com.uragiristereo.mejiboard.core.resources.R
import com.uragiristereo.mejiboard.domain.usecase.DownloadPostUseCase
import com.uragiristereo.mejiboard.domain.usecase.DownloadPostWithNotificationUseCase
import com.uragiristereo.mejiboard.ui.core.DownloadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    savedStateHandle: SavedStateHandle,
    preferencesRepository: PreferencesRepository,
    sessionDao: SessionDao,
    private val downloadRepository: DownloadRepository,
    private val downloadPostUseCase: DownloadPostUseCase,
    private val downloadPostWithNotificationUseCase: DownloadPostWithNotificationUseCase,
) : ViewModel() {
    var preferences by mutableStateOf(Preferences(theme = preferencesRepository.getInitialTheme()))
        private set

    var confirmExit by mutableStateOf(true)

    var navigatedBackByGesture by mutableStateOf(false)

    private val initialized = savedStateHandle[Constants.STATE_KEY_INITIALIZED] ?: false

    var selectedPost: Post? = null

    // share
    private var shareJob: Job? = null
    var shareDialogVisible by mutableStateOf(false)

    var downloadState by mutableStateOf(DownloadState())
        private set

    init {
        if (!initialized) {
            savedStateHandle[Constants.STATE_KEY_INITIALIZED] = true

            viewModelScope.launch(Dispatchers.IO) {
                sessionDao.deleteAllSessions()
            }
        }

        preferencesRepository.flowData.onEach {
            preferences = it
        }.launchIn(viewModelScope)
    }

    fun downloadPost(
        context: Context,
        post: Post,
    ) {
        if (downloadRepository.isPostAlreadyAdded(postId = post.id)) {
            Toast.makeText(
                /* context = */ context,
                /* text = */ context.getText(R.string.download_already_running),
                /* duration = */ Toast.LENGTH_LONG,
            ).show()
        } else {
            viewModelScope.launch {
                downloadPostWithNotificationUseCase(post)
            }

            Toast.makeText(
                /* context = */ context,
                /* text = */ context.getText(R.string.download_added),
                /* duration = */ Toast.LENGTH_LONG,
            ).show()
        }
    }

    fun sharePost(
        context: Context,
        post: Post,
        shareOption: ShareOption,
        onDownloadCompleted: () -> Unit,
        onDownloadFailed: (String) -> Unit,
    ) {
        selectedPost = post

        val tempDir = File(context.cacheDir, "temp")
            .also { dir ->
                if (!dir.isDirectory) {
                    dir.mkdir()
                }
            }

        val url = when {
            post.scaled && shareOption == ShareOption.COMPRESSED -> post.scaledImage.url
            else -> post.originalImage.url
        }

        val fileName = File(url).name
        val path = File(tempDir.absolutePath, fileName)

        downloadPostUseCase(
            postId = post.id,
            url = url,
            uri = path.toUri(),
        )
            .onEach { resource ->
                when (resource) {
                    DownloadResource.Starting -> {
                        downloadState = DownloadState()

                        // delay to avoid share dialog flashing if the image is already cached
                        delay(timeMillis = 500L)

                        shareDialogVisible = true
                    }

                    is DownloadResource.Downloading -> {
                        val lengthFmt = NumberUtil.convertFileSize(resource.length)
                        val downloadSpeedFmt = "${NumberUtil.convertFileSize(resource.speed)}/s"
                        val downloadedFmt = NumberUtil.convertFileSize(resource.downloaded)

                        downloadState = DownloadState(
                            downloaded = downloadedFmt,
                            fileSize = lengthFmt,
                            progress = resource.progress,
                            downloadSpeed = downloadSpeedFmt,
                        )
                    }

                    is DownloadResource.Completed -> {
                        shareDialogVisible = false

                        onDownloadCompleted()
                    }

                    is DownloadResource.Failed -> {
                        shareDialogVisible = false

                        onDownloadFailed(resource.t.toString())
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun cancelShare() {
        shareDialogVisible = false
        selectedPost?.let { downloadRepository.remove(it.id) }
    }
}
