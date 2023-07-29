package com.server.graphql

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.server.models.Creature
import com.server.services.CreatureService

fun SchemaBuilder.creatureSchema(creatureService:CreatureService){
    type<Creature>()
    query("creature"){
        resolver{id:String ->
            val result = creatureService.getCreature(id)
            result
        }
    }
    query("players"){
        resolver{first:Int,after:String->
            val result = creatureService.getPlayers(first,after)
            result
        }
    }
    mutation("categorizeCreature"){
        resolver{id:String, category:String ->
            val result = creatureService.categorizeCreature(id,category)
            result
        }
    }
    mutation("createRandomPlayer"){
        resolver{ ->
            val result = creatureService.createRandomPlayer()
            result
        }
    }
    mutation("movePlayer"){
        resolver{id:String, x:Int, y:Int->
            val result = creatureService.movePlayer(id,x,y)
            result
        }
    }
}