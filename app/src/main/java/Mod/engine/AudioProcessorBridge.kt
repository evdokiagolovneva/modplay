package Mod.engine

import Mod.api.AudioEffect
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer

@UnstableApi
class AudioProcessorBridge(
    internal val modEffect: AudioEffect
) : androidx.media3.common.audio.AudioProcessor
{
    internal var internalBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER
    internal var outputBuffer: ByteBuffer = AudioProcessor.EMPTY_BUFFER

    private var inputEnded: Boolean = false
    private var inputFormat = AudioProcessor.AudioFormat.NOT_SET
    private var outputFormat = AudioProcessor.AudioFormat.NOT_SET
    private var isActive: Boolean = false



    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        inputFormat = inputAudioFormat
        outputFormat = inputAudioFormat
        isActive = inputAudioFormat != AudioProcessor.AudioFormat.NOT_SET

        return outputFormat
    }

    override fun isActive(): Boolean {
        return isActive
    }

    override fun queueInput(inputBuffer: java.nio.ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return

        val count = inputBuffer.remaining()

        if (internalBuffer.capacity() < count) {
            internalBuffer = ByteBuffer.allocateDirect(count).order(inputBuffer.order())
        } else {
            internalBuffer.clear()
        }

        internalBuffer.put(inputBuffer)
        internalBuffer.flip() // Переводим буфер в режим чтения для мода

        modEffect.processAudioFrame(internalBuffer)

        outputBuffer = internalBuffer
    }

    override fun queueEndOfStream() {
        inputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        val output = outputBuffer
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        return output
    }

    override fun isEnded(): Boolean {
        return inputEnded && outputBuffer == AudioProcessor.EMPTY_BUFFER
    }

    override fun flush() {
        outputBuffer = AudioProcessor.EMPTY_BUFFER
        internalBuffer = AudioProcessor.EMPTY_BUFFER
        inputEnded = false
    }

    override fun reset() {
        flush()

        inputFormat = AudioProcessor.AudioFormat.NOT_SET
        outputFormat = AudioProcessor.AudioFormat.NOT_SET
        isActive = false
    }

}