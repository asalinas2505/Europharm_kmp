package org.example.project.scanner

interface Scanner {
    fun startScanning(onResult: (String) -> Unit)
    fun stopScanning()
}
