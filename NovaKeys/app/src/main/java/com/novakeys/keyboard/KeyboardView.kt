package com.novakeys.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitPointerEvent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KeyboardSurface(
    state: KeyboardState,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val whiteKeyWidth = 64.dp
    val blackKeyWidth = 40.dp
    val keyboardHeight = 220.dp

    Column(modifier = modifier) {
        Text(
            text = if (state.activeNotes.isEmpty()) {
                "Tap a key to play"
            } else {
                state.activeNotes.sorted().joinToString(" · ") { midiNoteLabel(it) }
            },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(keyboardHeight)
                .horizontalScroll(scrollState),
        ) {
            Box(
                modifier = Modifier
                    .width(whiteKeyWidth * KeyboardLayout.whiteKeys.size)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    KeyboardLayout.whiteKeys.forEach { key ->
                        PianoKey(
                            key = key,
                            width = whiteKeyWidth,
                            height = keyboardHeight,
                            isPressed = key.midiNote in state.activeNotes,
                            onNoteOn = onNoteOn,
                            onNoteOff = onNoteOff,
                            background = MaterialTheme.colorScheme.surface,
                            pressedBackground = MaterialTheme.colorScheme.primaryContainer,
                            foreground = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                KeyboardLayout.blackKeys.forEach { key ->
                    PianoKey(
                        key = key,
                        width = blackKeyWidth,
                        height = keyboardHeight * 0.62f,
                        isPressed = key.midiNote in state.activeNotes,
                        onNoteOn = onNoteOn,
                        onNoteOff = onNoteOff,
                        background = Color(0xFF20232A),
                        pressedBackground = MaterialTheme.colorScheme.primary,
                        foreground = Color.White,
                        modifier = Modifier.offset(
                            x = whiteKeyWidth * KeyboardLayout.whiteKeyIndexBefore(key.midiNote) -
                                (blackKeyWidth / 2),
                        ),
                    )
                }
                if (state.performance.splitEnabled) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = whiteKeyWidth * KeyboardLayout.whiteKeyIndexBefore(
                                    state.performance.splitPoint,
                                ),
                            )
                            .width(2.dp)
                            .height(keyboardHeight)
                            .background(MaterialTheme.colorScheme.tertiary),
                    )
                }
            }
        }
    }
}

@Composable
private fun PianoKey(
    key: KeyboardKey,
    width: Dp,
    height: Dp,
    isPressed: Boolean,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit,
    background: Color,
    pressedBackground: Color,
    foreground: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(if (isPressed) pressedBackground else background)
            .border(
                width = if (key.isBlack) 1.dp else 0.5.dp,
                color = if (key.isBlack) Color.Black else MaterialTheme.colorScheme.outlineVariant,
            )
            .noteTouch(key.midiNote, onNoteOn, onNoteOff),
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (!key.isBlack && key.label.startsWith("C")) {
            Text(
                text = key.label,
                color = foreground,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

private fun Modifier.noteTouch(
    midiNote: Int,
    onNoteOn: (Int) -> Unit,
    onNoteOff: (Int) -> Unit,
): Modifier = pointerInput(midiNote) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        onNoteOn(midiNote)
        try {
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                if (!change.pressed) break
            }
        } finally {
            onNoteOff(midiNote)
        }
    }
}

private fun midiNoteLabel(midiNote: Int): String {
    val pitchNames = listOf(
        "C", "C♯", "D", "D♯", "E", "F",
        "F♯", "G", "G♯", "A", "A♯", "B",
    )
    return "${pitchNames[midiNote.mod(12)]}${midiNote / 12 - 1}"
}
