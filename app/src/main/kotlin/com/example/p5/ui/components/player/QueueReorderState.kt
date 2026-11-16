package com.example.p5.ui.components.player

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

class QueueReorderState(
    val lazyListState: LazyListState,
    private val onMove: (fromIndex: Int, toIndex: Int) -> Unit
) {
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    var draggedOffset by mutableFloatStateOf(0f)
        private set

    fun onDragStart(index: Int) {
        draggedIndex = index
        draggedOffset = 0f
    }

    fun onDrag(dragAmountY: Float) {
        val currentIndex = draggedIndex ?: return
        draggedOffset += dragAmountY

        val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
        val currentItemInfo = visibleItems.firstOrNull { it.index == currentIndex } ?: return
        val currentCenter = currentItemInfo.offset + (currentItemInfo.size / 2) + draggedOffset

        val targetItem = visibleItems.firstOrNull { item ->
            if (item.index == currentIndex) return@firstOrNull false
            val itemCenter = item.offset + (item.size / 2)
            if (draggedOffset > 0) {
                currentCenter > itemCenter && item.index > currentIndex
            } else {
                currentCenter < itemCenter && item.index < currentIndex
            }
        }

        if (targetItem != null) {
            val targetIndex = targetItem.index
            onMove(currentIndex, targetIndex)
            draggedIndex = targetIndex
            draggedOffset += (currentItemInfo.offset - targetItem.offset)
        }
    }

    fun onDragEnd() {
        draggedIndex = null
        draggedOffset = 0f
    }

    fun onDragCancel() {
        draggedIndex = null
        draggedOffset = 0f
    }
}

@Composable
fun rememberQueueReorderState(
    lazyListState: LazyListState,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit
): QueueReorderState {
    return remember(lazyListState, onMove) {
        QueueReorderState(lazyListState, onMove)
    }
}

fun Modifier.dragReorderHandle(
    index: Int,
    state: QueueReorderState
): Modifier = this.pointerInput(index) {
    detectDragGestures(
        onDragStart = { state.onDragStart(index) },
        onDrag = { change, dragAmount ->
            change.consume()
            state.onDrag(dragAmount.y)
        },
        onDragEnd = { state.onDragEnd() },
        onDragCancel = { state.onDragCancel() }
    )
}
