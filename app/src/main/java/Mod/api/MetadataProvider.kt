package Mod.api;

import android.service.settings.preferences.MetadataResult


// Чем этот гражданин будет отличаться от встроенного в Media3 MediaMetadata?
interface MetadataProvider {
    suspend fun fetchMetadata(track: MetadataTrack): Map<String, String>

}