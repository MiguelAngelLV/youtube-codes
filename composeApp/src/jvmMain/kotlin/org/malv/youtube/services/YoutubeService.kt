package org.malv.youtube.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.VideoLocalization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.malv.youtube.models.Video

class YoutubeService(
    private val credential: Credential,
) {

    private val youtube = YouTube.Builder(credential.transport, credential.jsonFactory, credential)
        .setApplicationName("youtube")
        .build()


    suspend fun getVideos(): List<Video> = withContext(Dispatchers.IO) {
        val channels = youtube.channels().list("contentDetails")
            .setMine(true)
            .execute()
            .items

        var next: String? = null
        val playlistIds = channels[0].contentDetails.relatedPlaylists.uploads
        sequence {
            do {
                val response = youtube.playlistItems()
                    .list("contentDetails")
                    .setPlaylistId(playlistIds)
                    .setMaxResults(50)
                    .setPageToken(next)
                    .execute()

                next = response.nextPageToken
                val playlists = response.items
                val videos = youtube.videos()
                    .list("snippet, localizations")
                    .setId(playlists.joinToString(",") { it.contentDetails.videoId })
                    .execute()
                    .items

                videos.forEach { v ->
                    if (v.localizations == null) yield(
                        Video(
                            id = v.id,
                            title = v.snippet.title,
                            description = v.snippet.description,
                            language = "es",
                            single = true
                        )
                    )
                    else v.localizations.forEach {
                        yield(
                            Video(
                                id = v.id,
                                title = it.value.title,
                                description = it.value.description,
                                language = it.key,
                                single = false
                            )
                        )
                    }
                }
            } while (next != null)
        }.toList()
    }

    suspend fun updateVideo(video: Video) = withContext(Dispatchers.IO) {
        val original = youtube.videos()
            .list("snippet,localizations")
            .setId(video.id)
            .execute()
            .items.firstOrNull() ?: return@withContext

        if (original.localizations == null)
            original.localizations = mutableMapOf<String, VideoLocalization>()

        original.localizations[video.language] = VideoLocalization().apply {
            title = video.title
            description = video.description
        }

        original.snippet.defaultLanguage = "es-ES"

        if (original.snippet.defaultLanguage == video.language) {
            original.snippet.title = video.title
            original.snippet.description = video.description
        }

        youtube.videos().update("localizations, snippet", original).execute()
    }
}