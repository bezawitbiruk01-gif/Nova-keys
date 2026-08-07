package com.novakeys.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.sin

data class AudioSessionState(
    val isReady: Boolean = false,
)

interface AudioEngine {
    fun noteOn(midiNote: Int)

    fun noteOff(midiNote: Int)

    fun setSustain(enabled: Boolean)

    fun setMasterVolume(volume: Float)

    fun release()
}

class SimpleAudioEngine : AudioEngine {
    private val voices = ConcurrentHashMap<Int, Voice>()
    private val running = AtomicBoolean(true)
    @Volatile
    private var sustainEnabled = false
    @Volatile
    private var masterVolume = 0.8f
    private val audioTrack: AudioTrack
    private val audioThread: Thread

    init {
        val sampleRate = SAMPLE_RATE.toInt()
        val minimumBufferBytes = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        val bufferSizeBytes = maxOf(
            minimumBufferBytes,
            BUFFER_FRAMES * Short.SIZE_BYTES,
        )
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(bufferSizeBytes)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        audioTrack.play()

        audioThread = Thread(::renderAudio).apply {
            name = "NovaKeys-Audio"
            isDaemon = true
            start()
        }
    }

    override fun noteOn(midiNote: Int) {
        if (midiNote in 0..127 && running.get()) {
            voices[midiNote] = Voice(
                midiNote = midiNote,
                frequency = frequencyForMidiNote(midiNote),
            )
        }
    }

    override fun noteOff(midiNote: Int) {
        voices[midiNote]?.let { voice ->
            voice.isHeld = false
            if (!sustainEnabled) {
                voice.isReleasing = true
            }
        }
    }

    override fun setSustain(enabled: Boolean) {
        sustainEnabled = enabled
        if (!enabled) {
            voices.values
                .filter { !it.isHeld }
                .forEach { it.isReleasing = true }
        }
    }

    override fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
    }

    override fun release() {
        if (!running.compareAndSet(true, false)) {
            return
        }
        voices.clear()
        audioThread.interrupt()
        audioThread.join(500)
        if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop()
        }
        audioTrack.release()
    }

    private fun renderAudio() {
        val buffer = ShortArray(BUFFER_FRAMES)
        while (running.get()) {
            val activeVoices = voices.values.toList()
            for (index in buffer.indices) {
                var mixedSample = 0.0
                activeVoices.forEach { voice ->
                    mixedSample += sin(voice.phase) * voice.level
                    voice.phase += voice.phaseStep
                    if (voice.phase >= TWO_PI) {
                        voice.phase -= TWO_PI
                    }
                    if (voice.isReleasing) {
                        voice.level *= RELEASE_FACTOR
                    }
                }
                buffer[index] = (
                    (mixedSample.coerceIn(-1.0, 1.0) * masterVolume) * Short.MAX_VALUE * OUTPUT_LEVEL
                    ).toInt().toShort()
            }

            activeVoices
                .filter { it.isReleasing && it.level < RELEASE_THRESHOLD }
                .forEach { voice -> voices.remove(voice.midiNote, voice) }

            try {
                audioTrack.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING)
            } catch (_: IllegalStateException) {
                return
            }
        }
    }

    private fun frequencyForMidiNote(midiNote: Int): Double {
        return 440.0 * exp((midiNote - 69) / 12.0)
    }

    private class Voice(
        val midiNote: Int,
        frequency: Double,
        var phase: Double = 0.0,
        var level: Double = INITIAL_LEVEL,
    ) {
        val phaseStep: Double = TWO_PI * frequency / SAMPLE_RATE

        @Volatile
        var isHeld: Boolean = true

        @Volatile
        var isReleasing: Boolean = false
    }

    private companion object {
        const val SAMPLE_RATE = 44_100.0
        const val BUFFER_FRAMES = 256
        const val TWO_PI = PI * 2.0
        const val INITIAL_LEVEL = 0.8
        const val OUTPUT_LEVEL = 0.16
        const val RELEASE_FACTOR = 0.995
        const val RELEASE_THRESHOLD = 0.001
    }
}

private fun exp(value: Double): Double = kotlin.math.exp(ln(2.0) * value)