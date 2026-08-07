package com.novakeys.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.novakeys.audio.AudioEngine
import com.novakeys.audio.SimpleAudioEngine
import com.novakeys.storage.AppStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PerformanceState(
    val splitEnabled: Boolean = false,
    val splitPoint: Int = 60,
    val layerEnabled: Boolean = false,
    val transpose: Int = 0,
    val sustainEnabled: Boolean = false,
)

data class KeyboardState(
    val octave: Int = 4,
    val activeNotes: Set<Int> = emptySet(),
    val performance: PerformanceState = PerformanceState(),
    val selectedRegistration: Int = 1,
    val chordName: String = "—",
)

data class KeyboardKey(
    val midiNote: Int,
    val isBlack: Boolean,
    val label: String,
)

object KeyboardLayout {
    const val FIRST_MIDI_NOTE = 21
    const val LAST_MIDI_NOTE = 108

    val keys: List<KeyboardKey> = (FIRST_MIDI_NOTE..LAST_MIDI_NOTE).map { midiNote ->
        KeyboardKey(
            midiNote = midiNote,
            isBlack = midiNote % 12 in BLACK_PITCH_CLASSES,
            label = noteLabel(midiNote),
        )
    }

    val whiteKeys: List<KeyboardKey> = keys.filterNot(KeyboardKey::isBlack)
    val blackKeys: List<KeyboardKey> = keys.filter(KeyboardKey::isBlack)

    fun whiteKeyIndexBefore(midiNote: Int): Int {
        return keys.takeWhile { it.midiNote < midiNote }.count { !it.isBlack }
    }

    private fun noteLabel(midiNote: Int): String {
        val octave = (midiNote / 12) - 1
        return "${PITCH_NAMES[midiNote % 12]}$octave"
    }

    private val BLACK_PITCH_CLASSES = setOf(1, 3, 6, 8, 10)
    private val PITCH_NAMES = listOf(
        "C", "C♯", "D", "D♯", "E", "F",
        "F♯", "G", "G♯", "A", "A♯", "B",
    )
}

class KeyboardViewModel(
    private val audioEngine: AudioEngine,
    private val storage: AppStorage,
) : ViewModel() {
    private val soundingNoteOwners = mutableMapOf<Int, Int>()
    private val soundingNotesByKey = mutableMapOf<Int, List<Int>>()
    private val mutableState = MutableStateFlow(
        KeyboardState(
            performance = storage.readPerformanceState(),
            selectedRegistration = storage.readSelectedRegistration(),
        ),
    )
    val state: StateFlow<KeyboardState> = mutableState.asStateFlow()

    init {
        audioEngine.setSustain(mutableState.value.performance.sustainEnabled)
        audioEngine.setMasterVolume(0.8f)
    }

    fun noteOn(midiNote: Int) {
        if (midiNote in mutableState.value.activeNotes) {
            return
        }
        val soundingNotes = soundingNotesFor(midiNote)
        soundingNotesByKey[midiNote] = soundingNotes
        soundingNotes.forEach { note ->
            val owners = soundingNoteOwners[note] ?: 0
            if (owners == 0) {
                audioEngine.noteOn(note)
            }
            soundingNoteOwners[note] = owners + 1
        }
        updateState {
            copy(
                activeNotes = activeNotes + midiNote,
                chordName = ChordDetector.detect(activeNotes + midiNote),
            )
        }
    }

    fun noteOff(midiNote: Int) {
        val soundingNotes = soundingNotesByKey.remove(midiNote).orEmpty()
        soundingNotes.forEach { note ->
            val owners = (soundingNoteOwners[note] ?: 1) - 1
            if (owners <= 0) {
                soundingNoteOwners.remove(note)
                audioEngine.noteOff(note)
            } else {
                soundingNoteOwners[note] = owners
            }
        }
        updateState {
            copy(
                activeNotes = activeNotes - midiNote,
                chordName = ChordDetector.detect(activeNotes - midiNote),
            )
        }
    }

    fun setSplitEnabled(enabled: Boolean) {
        updatePerformance { copy(splitEnabled = enabled) }
    }

    fun setSplitPoint(point: Int) {
        updatePerformance { copy(splitPoint = point.coerceIn(36, 84)) }
    }

    fun setLayerEnabled(enabled: Boolean) {
        updatePerformance { copy(layerEnabled = enabled) }
    }

    fun transposeBy(amount: Int) {
        updatePerformance {
            copy(transpose = (transpose + amount).coerceIn(-12, 12))
        }
    }

    fun setSustainEnabled(enabled: Boolean) {
        audioEngine.setSustain(enabled)
        updatePerformance { copy(sustainEnabled = enabled) }
    }

    fun setMasterVolume(volume: Float) {
        audioEngine.setMasterVolume(volume)
    }

    fun saveRegistration(slot: Int) {
        val validSlot = slot.coerceIn(1, 3)
        storage.writeRegistration(validSlot, mutableState.value.performance)
        storage.writeSelectedRegistration(validSlot)
        updateState { copy(selectedRegistration = validSlot) }
    }

    fun loadRegistration(slot: Int) {
        val validSlot = slot.coerceIn(1, 3)
        val savedState = storage.readRegistration(validSlot) ?: return
        stopActiveNotes()
        storage.writePerformanceState(savedState)
        storage.writeSelectedRegistration(validSlot)
        audioEngine.setSustain(savedState.sustainEnabled)
        updateState {
            copy(
                activeNotes = emptySet(),
                performance = savedState,
                selectedRegistration = validSlot,
                chordName = "—",
            )
        }
    }

    override fun onCleared() {
        stopActiveNotes()
        storage.writePerformanceState(mutableState.value.performance)
        audioEngine.release()
        super.onCleared()
    }

    private fun soundingNotesFor(midiNote: Int): List<Int> {
        val performance = mutableState.value.performance
        val splitOffset = if (performance.splitEnabled && midiNote < performance.splitPoint) {
            -12
        } else {
            0
        }
        val transposedNote = (midiNote + performance.transpose + splitOffset)
            .coerceIn(0, 127)
        val layerNote = (transposedNote + 12).coerceAtMost(127)
        val layerApplies = performance.layerEnabled &&
            (!performance.splitEnabled || midiNote >= performance.splitPoint)
        return if (layerApplies) {
            listOf(transposedNote, layerNote).distinct()
        } else {
            listOf(transposedNote)
        }
    }

    private fun updatePerformance(transform: PerformanceState.() -> PerformanceState) {
        val updatedPerformance = mutableState.value.performance.transform()
        storage.writePerformanceState(updatedPerformance)
        if (updatedPerformance.sustainEnabled != mutableState.value.performance.sustainEnabled) {
            audioEngine.setSustain(updatedPerformance.sustainEnabled)
        }
        updateState { copy(performance = updatedPerformance) }
    }

    private fun updateState(transform: KeyboardState.() -> KeyboardState) {
        mutableState.value = mutableState.value.transform()
    }

    private fun stopActiveNotes() {
        soundingNotesByKey.values
            .flatten()
            .distinct()
            .forEach(audioEngine::noteOff)
        soundingNotesByKey.clear()
        soundingNoteOwners.clear()
    }
}

class KeyboardViewModelFactory(
    private val storage: AppStorage,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(KeyboardViewModel::class.java)) {
            "Unsupported ViewModel type: ${modelClass.name}"
        }
        return KeyboardViewModel(SimpleAudioEngine(), storage) as T
    }
}

object ChordDetector {
    fun detect(midiNotes: Set<Int>): String {
        if (midiNotes.size < 2) {
            return "—"
        }
        val pitchClasses = midiNotes.map { it.mod(12) }.toSet()
        val orderedRoots = midiNotes.sorted().map { it.mod(12) }.distinct()
        orderedRoots.forEach { root ->
            val intervals = pitchClasses.map { (it - root + 12) % 12 }.toSet()
            val quality = when {
                intervals.containsAll(setOf(0, 4, 7)) -> "Major"
                intervals.containsAll(setOf(0, 3, 7)) -> "Minor"
                intervals.containsAll(setOf(0, 3, 6)) -> "Diminished"
                intervals.containsAll(setOf(0, 5, 7)) -> "Sus4"
                intervals.containsAll(setOf(0, 4, 7, 10)) -> "7"
                else -> null
            }
            if (quality != null) {
                return "${PITCH_NAMES[root]} $quality"
            }
        }
        return "Notes"
    }

    private val PITCH_NAMES = listOf(
        "C", "C♯", "D", "D♯", "E", "F",
        "F♯", "G", "G♯", "A", "A♯", "B",
    )
}