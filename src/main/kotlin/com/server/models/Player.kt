package com.server.models

import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty

interface Entity {
    var id: String
    var alive: Boolean
    var hitPoints: Int
    var initiative: Int
    var x: Int
    var y: Int
}

data class Player (
    override var id: String,
    override var alive: Boolean,
    override var hitPoints: Int,
    override var initiative: Int,
    override var x: Int,
    override var y: Int,
    var name:String,
): Entity

data class PlayerPutResponse(
    var success:Boolean
)