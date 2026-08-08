package com.novakeys.sampler

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.io.Closeable
import java.util.LinkedHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BasicSampleEngine : SampleEngine, Closeable {
    private data class Voice(
        val midiNote: Int,
        val velocity: Int,
        val frequencyHz: Double,
        var phase: Double = 0.0,
        var release: Boolean = false,
        var held: Boolean = true,
        var amplitude: Double = (velocity / 127.0) * 0.35,
    )

    private val lock = Any()
    private val running = AtomicBoolean(true)
    private val voices = LinkedHashMap<Int, Voice>()
    private val loadedSoundFonts = mutableListOf<SoundFontInfo>()
    private val stateFlow = MutableStateFlow(SamplerState(engineReady = true))
    override val state: StateFlow<SamplerState> = stateFlow.asStateFlow()

    private var polyphonyLimit: Int = 128
    @Volatile
    private var sustainEnabled: Boolean = false
    @Volatile
    private var masterVolume: Float = 0.8f
    private val sampleRate = 44_100
    private val audioTrack: AudioTrack
    private val audioThread: Thread

    init {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
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
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(minBufferSize.coerceAtLeast(sampleRate / 10))
            .build()
        audioTrack.play()

        audioThread = Thread { renderLoop() }.apply {
            name = "NovaKeys-SampleEngine"
            isDaemon = true
            start()
        }
    }

    override fun loadSoundFont(soundFont: SoundFontInfo) {
        synchronized(lock) {
            if (loadedSoundFonts.none { it.id == soundFont.id }) {
                loadedSoundFonts += soundFont
                updateState()
            }
        }
    }

    override fun noteOn(midiNote: Int, velocity: Int) {
        if (midiNote !in 0..127) return
        synchronized(lock) {
            if (voices.size >= polyphonyLimit) {
                val oldest = voices.entries.firstOrNull()?.key
                if (oldest != null) voices.remove(oldest)
            }
            voices[midiNote] = Voice(
                midiNote = midiNote,
                velocity = velocity.coerceIn(1, 127),
                frequencyHz = midiToFrequency(midiNote),
            )
            updateState()
        }
    }

    override fun noteOff(midiNote: Int) {
        synchronized(lock) {
            voices[midiNote]?.let { voice ->
                voice.held = false
                if (!sustainEnabled) {
                    voice.release = true
                }
            }
            updateState()
        }
    }

    override fun setPolyphonyLimit(limit: Int) {
        synchronized(lock) {
            polyphonyLimit = limit.coerceIn(1, 128)
            while (voices.size > polyphonyLimit) {
                voices.remove(voices.entries.firstOrNull()?.key)
            }
            updateState()
        }
    }

    override fun setSustain(enabled: Boolean) {
        sustainEnabled = enabled
        synchronized(lock) {
            if (!enabled) {
                voices.values.filter { !it.held }.forEach { it.release = true }
            }
            updateState()
        }
    }

    override fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
    }

    override fun release() {
        if (!running.compareAndSet(true, false)) return
        audioThread.join(250)
        runCatching {
            audioTrack.pause()
            audioTrack.flush()
            audioTrack.stop()
            audioTrack.release()
        }
    }

    override fun close() {
        release()
    }

    fun loadDemoSoundFont() {
        loadSoundFont(
            SoundFontInfo(
                id = "demo-sine",
                displayName = "Demo Sine Set",
                description = "Offline placeholder sound source",
            ),
        )
    }

    private fun renderLoop() {
        val buffer = ShortArray(256)
        while (running.get()) {
            for (index in buffer.indices) {
                var sample = 0.0
                synchronized(lock) {
                    val iterator = voices.entries.iterator()
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        val voice = entry.value
                        if (voice.release) {
                            voice.amplitude *= 0.992
                            if (voice.amplitude < 0.0005) {
                                iterator.remove()
                                continue
                            }
                        }
                        sample += sin(voice.phase) * voice.amplitude
                        voice.phase += (2.0 * PI * voice.frequencyHz) / sampleRate
                        if (voice.phase > 2.0 * PI) voice.phase -= 2.0 * PI
                    }
                }
                buffer[index] = (
                    (sample.coerceIn(-1.0, 1.0) * masterVolume) * Short.MAX_VALUE
                ).roundToInt().toShort()
            }

            updateState()
            runCatching { audioTrack.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING) }
        }
    }

    private fun updateState() {
        stateFlow.value = SamplerState(
            engineReady = true,
            polyphonyLimit = polyphonyLimit,
            activeVoices = synchronized(lock) { voices.size },
            loadedSoundFonts = loadedSoundFonts.toList(),
            selectedSoundFontId = loadedSoundFonts.lastOrNull()?.id,
        )
    }

    private fun midiToFrequency(midiNote: Int): Double {
        return 440.0 * 2.0.pow((midiNote - 69) / 12.0)
    }
}
