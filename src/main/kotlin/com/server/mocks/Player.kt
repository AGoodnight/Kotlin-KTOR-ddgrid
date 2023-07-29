package com.server.mocks

import com.server.models.Creature
import java.time.Instant
import java.util.*

var MockPlayer: Creature = Creature(
    id = UUID.randomUUID().toString(),
    name = "Derman",
    x = 10,
    y = 20,
    initiative = 10,
    hitPoints = 20,
    alive = true,
    category = "player",
    timeCreated = Instant.now().toEpochMilli(),
    timeModified = Instant.now().toEpochMilli()
)
