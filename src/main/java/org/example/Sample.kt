package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("Hello and welcome!")

    val elevatorUseCase = ElevatorUseCase(this, 4, 9)
    checkForInput(this, elevatorUseCase)
}

private fun checkForInput(scope: CoroutineScope, elevatorUseCase: ElevatorUseCase) {
    scope.launch(Dispatchers.IO) {
        while (isActive) {
            println("Enter input: ")
            val input = readlnOrNull() ?: continue
            input.toIntOrNull()?.let { whereToGo ->
                elevatorUseCase.handleInput(whereToGo)
            }
        }
    }
}







