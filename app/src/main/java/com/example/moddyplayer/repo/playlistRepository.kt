package com.example.moddyplayer.repo

import com.example.moddyplayer.data.db.Playlist
import com.example.moddyplayer.data.db.PlaylistDao
import com.example.moddyplayer.data.db.PlaylistTrack
import com.example.moddyplayer.data.db.Track
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val dao: PlaylistDao) {
    fun observeAll(): Flow<List<Playlist>> = dao.observeAll()

    suspend fun createPlaylist(playlist: Playlist): Long = dao.insert(playlist)

    suspend fun addTrackToPlaylist(link: PlaylistTrack) = dao.addTrack(link)

    fun observePlaylistTracks(playlistId: Long): Flow<List<Track>> = dao.observeTracks(playlistId)
}