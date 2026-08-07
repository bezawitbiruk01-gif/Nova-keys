package com.novakeys.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.novakeys.audio.AudioEngine
import com.novakeys.audio.SimpleAudioEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class KeyboardState(
    val octave: Int = 4,
    val activeNotes: Set<Int> = emptySet(),
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
) : ViewModel() {
    private val mutableState = MutableStateFlow(KeyboardState())
    val state: StateFlow<KeyboardState> = mutableState.asStateFlow()

    fun noteOn(midiNote: Int) {
        audioEngine.noteOn(midiNote)
        mutableState.value = mutableState.value.copy(
            activeNotes = mutableState.value.activeNotes + midiNote,
        )
    }

    fun noteOff(midiNote: Int) {
        audioEngine.noteOff(midiNote)
        mutableState.value = mutableState.value.copy(
            activeNotes = mutableState.value.activeNotes - midiNote,
        )
    }

    override fun onCleared() {
        audioEngine.release()
        super.onCleared()
    }
}

class KeyboardViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(KeyboardViewModel::class.java)) {
            "Unsupported ViewModel type: ${modelClass.name}"
        }
        return KeyboardViewModel(SimpleAudioEngine()) as T
    }
}