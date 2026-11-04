package Mod.engine

import com.example.player_api.api.AudioEffect
import android.content.Context
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.DefaultAudioSink

@UnstableApi
class AudioEngine(private val context: Context) {

    private val modEffects: MutableMap<String, MutableList<AudioProcessorBridge>> = mutableMapOf()

    private var activeProcessors: Array<AudioProcessor> = emptyArray()

    private lateinit var audioSink: DefaultAudioSink

    val player: ExoPlayer

    init {
        val renderersFactory = object : DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): androidx.media3.exoplayer.audio.AudioSink {

                val processorChain = DefaultAudioSink.DefaultAudioProcessorChain(
                    *activeProcessors // Звездочка * раскладывает Array в vararg аргументы
                )
                val sink = DefaultAudioSink.Builder(context)
                    .setAudioProcessorChain(processorChain)
                    .build()

                audioSink = sink
                return sink

            }
        }
        player = ExoPlayer.Builder(context, renderersFactory).build()
    }

    private fun updateProcessors() {
        activeProcessors = modEffects.values.flatten().toTypedArray()
        if (::audioSink.isInitialized) {
            // flush() заставит DefaultAudioSink пересобрать процессоры на следующем фрейме
            audioSink.flush()
        }
    }

    fun registerModEffect(modId: String, effect: AudioEffect) {
        val bridge = AudioProcessorBridge(effect)
        modEffects.getOrPut(modId) { mutableListOf() }.add(bridge)

        updateProcessors()
    }

    fun removeEffectsForMod(modId: String) {
        val removed = modEffects.remove(modId)
        removed?.forEach { it.reset() }
        updateProcessors()
    }

    fun unregisterModEffect(modId: String, effect: AudioEffect) {
        val list = modEffects[modId] ?: return

        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val bridge = iterator.next()

            if (bridge.modEffect == effect) {
                bridge.reset()
                iterator.remove()
                break
            }
        }

        if (list.isEmpty()) {
            modEffects.remove(modId)
        }
        updateProcessors()
    }
}

