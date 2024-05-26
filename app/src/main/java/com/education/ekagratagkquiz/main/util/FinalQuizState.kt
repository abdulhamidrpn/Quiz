package com.education.ekagratagkquiz.main.util

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class FinalQuizState(
    val attemptedCount: Int = 0,
    val optionsState: SnapshotStateList<FinalQuizOptionState> = mutableStateListOf()
)
