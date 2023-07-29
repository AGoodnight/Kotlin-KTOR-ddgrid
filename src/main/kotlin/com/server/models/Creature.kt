package com.server.models

abstract class BaseCreature {
    abstract val id: String
    abstract val alive: Boolean?
    abstract val hitPoints: Int?
    abstract val initiative: Int?
    abstract val x: Int?
    abstract val y: Int?
    abstract val category:String?
    abstract val timeCreated:Long?
    abstract val timeModified:Long?
    abstract val name:String?
}
data class Creature(
    override val id: String,
    override val alive: Boolean,
    override val hitPoints: Int,
    override val initiative: Int,
    override val x: Int,
    override val y: Int,
    override val category:String,
    override val timeCreated:Long,
    override val timeModified:Long,
    override val name:String
): BaseCreature()
