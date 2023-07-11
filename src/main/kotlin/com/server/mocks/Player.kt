package com.server.mocks

import com.server.models.Player
import java.util.*

var MockPlayer: Player = Player(
    id = UUID.randomUUID().toString(),
    name = "Derman",
    x = 10,
    y = 20,
    initiative = 10,
    hitPoints = 20,
    alive = true
)
