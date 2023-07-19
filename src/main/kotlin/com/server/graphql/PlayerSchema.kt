package com.server.graphql

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.google.gson.Gson
import com.server.models.Player
import com.server.services.PlayerService
import java.util.logging.Logger

fun SchemaBuilder.playerSchema(playerService:PlayerService){
    type<Player>()
    query("player"){
        resolver{id:String, ctx:Context ->
            val result = playerService.getPlayer(id)
            result
        }
    }
    mutation("createRandomPlayer"){
        resolver{ ->
            val result = playerService.createRandomPlayer()
            result
        }
    }
    mutation("move"){
        resolver{id:String, x:Int, y:Int, ctx:Context ->
            val result = playerService.movePlayer(id,x,y)
            result
        }
    }
}