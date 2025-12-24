package com.indemand.elevator

enum class ElevatorState(val displayText: String) {
    MOVING("Moving"), IDLE("Idle")
}

enum class DIRECTION {
    UP, DOWN
}

data class Elevator(
    var id: Int,
    var displayName: String,
    var level: Int = 0,
    var state: ElevatorState = ElevatorState.IDLE,
    var direction: DIRECTION? = null
) {
    var isMoving: Boolean
        get() = state == ElevatorState.MOVING
        set(value) {
            state = if (value) {
                ElevatorState.MOVING
            } else {
                ElevatorState.IDLE
            }
        }

    var isGoingUp: Boolean
        get() = direction == DIRECTION.UP
        set(value) {
            direction = if (value) {
                DIRECTION.UP
            } else {
                DIRECTION.DOWN
            }
        }

    fun stateDisplayText() = state.displayText
}
