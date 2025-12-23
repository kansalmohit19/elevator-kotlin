package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ElevatorUseCase(val scope: CoroutineScope) {
    val elevatorCount = 4
    val levels = 10
    val currentLevelOfUser = 0
    private val _buttons = MutableStateFlow<List<List<Int>>>(emptyList())
    val buttons = _buttons.asStateFlow()

    private val _elevators = MutableStateFlow<List<Elevator>>(emptyList())
    val elevators = _elevators.asStateFlow()

    init {
        createElevators()
        createButtons()

        printElevatorState()
    }

    private fun createElevators() {
        repeat(elevatorCount) { index ->
            val displayName = ('A' + index).toString()
            _elevators.value += Elevator(id = index + 1, displayName = displayName)
        }
    }

    private fun createButtons() {
        var index = 1
        val buttonsColumn = mutableListOf<List<Int>>()
        repeat(3) {
            val buttonsRow = mutableListOf<Int>()
            repeat(3) {
                buttonsRow.add(index)
                index++
            }
            buttonsColumn.add(buttonsRow)
        }
        _buttons.value = buttonsColumn
    }

    private fun printElevatorState() {
        scope.launch {
            while (true) {
                delay(1000)
                val line = _elevators.value.joinToString(separator = " | ") { elevator ->
                    "Elevator-${elevator.id}: ${elevator.stateDisplayText()}-${elevator.level}"
                }
                println(line)
            }
        }
    }

    fun handleInput(whereToGo: Int) {

        val elevator =
            elevators.value.filter { !it.isMoving }.minByOrNull { it.level - currentLevelOfUser }
        println("Assigned Elevator: ${elevator?.id}")
        elevator?.let {
            if (currentLevelOfUser == elevator.level) {
                assignElevator(whereToGo, it)
            } else if (currentLevelOfUser < elevator.level) {
                scope.launch {
                    bringElevator(currentLevelOfUser, it).join()
                    assignElevator(whereToGo, it)
                }
            }
        }
    }

    fun assignElevator(whereToGo: Int, elevator: Elevator) {
        scope.launch {
            var currentLevel = _elevators.value.first { it.id == elevator.id }.level

            while (currentLevel < whereToGo) {
                currentLevel += 1
                _elevators.value = _elevators.value.map {
                    if (it.id == elevator.id) it.copy(
                        state = ElevatorState.MOVING, level = currentLevel
                    ) else it
                }
                delay(1000)
            }
            _elevators.value = _elevators.value.map {
                if (it.id == elevator.id) it.copy(
                    state = ElevatorState.IDLE
                ) else it
            }
        }
    }

    fun bringElevator(whereToGo: Int, elevator: Elevator): Job {
        return scope.launch {
            var currentLevel = _elevators.value.first { it.id == elevator.id }.level

            while (currentLevel > whereToGo) {
                currentLevel -= 1
                _elevators.value = _elevators.value.map {
                    if (it.id == elevator.id) it.copy(
                        state = ElevatorState.MOVING, level = currentLevel
                    ) else it
                }
                delay(1000)
            }
            _elevators.value = _elevators.value.map {
                if (it.id == elevator.id) it.copy(
                    state = ElevatorState.IDLE
                ) else it
            }
        }
    }
}