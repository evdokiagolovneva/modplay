package Mod.api
import androidx.media3.common.audio.AudioProcessor
interface AudioEffect
{
    val effectId: String

    fun processAudioFrame(buffer: java.nio.ByteBuffer)
}

