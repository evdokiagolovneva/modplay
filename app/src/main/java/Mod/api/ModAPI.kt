package Mod.api
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaItem
import com.example.moddyplayer.data.db.dataa.Track
import com.example.moddyplayer.player.PlayerCore

interface ModAPI {


    fun onEnable(playerHost: PlayerHostAPI)

    fun onDisable()


    // По мере необходимости интерфейс может понадобиться расширить
}