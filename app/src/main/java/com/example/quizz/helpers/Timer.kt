package com.example.quizz.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Timer(private val timerScope: CoroutineScope) {

    private var _timerStateFlow = MutableStateFlow(TimerState())
    val timerStateFlow: StateFlow<TimerState> = _timerStateFlow

    private var job: Job? = null

    fun toggleTime(totalSeconds: Int) {
        job = if (job == null) {
            timerScope.launch {
                initTimer(totalSeconds)
                    .onCompletion { _timerStateFlow.emit(TimerState(0)) }
                    .collect { _timerStateFlow.emit(it) }
            }
        } else {
            job?.cancel()
            null
        }
    }
    /**
     * The timer emits the total seconds immediately.
     * Each second after that, it will emit the next value.
     */
    private fun initTimer(totalSeconds: Int): Flow<TimerState> =
//        generateSequence(totalSeconds - 1 ) { it - 1 }.asFlow()
        (totalSeconds - 1 downTo 0).asFlow() // Emit total - 1 because the first was emitted onStart
            .onEach { delay(1000) } // Each second later emit a number
            .onStart { emit(totalSeconds) } // Emit total seconds immediately
            .conflate() // In case the operation onTick takes some time, conflate keeps the time ticking separately
            .transform { remainingSeconds: Int ->
                emit(TimerState(remainingSeconds))
            }
    }

