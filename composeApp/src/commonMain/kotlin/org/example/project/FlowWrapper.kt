package org.example.project

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FlowWrapper<T>(private val flow: StateFlow<T>) {
    fun collect(callback: (T) -> Unit): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            flow.collect {
                callback(it)
            }
        }
    }
}