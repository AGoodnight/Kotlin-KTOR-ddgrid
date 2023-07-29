package com.server.services

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemResponse
import aws.sdk.kotlin.services.dynamodb.model.QueryResponse
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemResponse
import com.github.javafaker.Faker
import com.server.dal.CreatureRepository
import com.server.models.Creature
import com.server.models.PlayerPutResponse
import com.server.utils.Utils
import java.time.Instant
import java.util.Random
import java.util.UUID

class CreatureService {


    private val utils = Utils()
    private val _faker = Faker()
    private val _random = Random()
    private val _dal = CreatureRepository()


    private fun asCreature(item:Map<String,AttributeValue>):Creature{
        this.utils.consoleLog("AS CREATURE ()")
        this.utils.consoleLog(item.toString())

        val isAlive = item["alive"]!!.asN().toInt()>0

        return Creature(
            item["id"]!!.asS(),
            isAlive,
            item["hitPoints"]!!.asN().toInt(),
            item["initiative"]!!.asN().toInt(),
            item["x"]!!.asN().toInt(),
            item["y"]!!.asN().toInt(),
            item["category"]!!.asS(),
            item["timeCreated"]!!.asN().toLong(),
            item["timeModified"]!!.asN().toLong(),
            item["name"]!!.asS()
        )
    }

    suspend fun getCreature(id:String):Creature?{
        val result:QueryResponse? = _dal.getPlayerRequest(id)
        val resultingItem = result?.items?.first()
        return if (resultingItem != null)
            asCreature(resultingItem)
        else
            return null
    }

    suspend fun getPlayers(first:Int,after:String):List<Creature>?{
        val result:QueryResponse? = _dal.getPlayers(first,after)
        val resultingItems:List<Map<String,AttributeValue>>? = result?.items
        val resultsList = mutableListOf<Creature>()

        if(resultingItems !== null) resultingItems.forEach{ item ->
            resultsList.add(asCreature(item))
        }

        if(result?.lastEvaluatedKey !== null){
            this.utils.consoleLog(result.lastEvaluatedKey.toString())
        }


        if(resultsList.isEmpty()){
            return null
        }
        return resultsList;
    }

    suspend fun categorizeCreature(id:String,category: String):PlayerPutResponse{
        val result:PutItemResponse? = _dal.categorizeCreature(id,category)
        println(result?.consumedCapacity)
        return PlayerPutResponse(success=result !== null);
    }

    suspend fun createRandomPlayer():PlayerPutResponse{
        val newPlayer = Creature(
            id=UUID.randomUUID().toString(),
            name=_faker.name().firstName(),
            hitPoints=_random.nextInt(20),
            initiative=_random.nextInt(20),
            alive=true,
            x=_random.nextInt(20),
            y=_random.nextInt(20),
            category = "Player",
            timeCreated = Instant.now().toEpochMilli(),
            timeModified = Instant.now().toEpochMilli()
        )
        val result:PutItemResponse? = _dal.createPlayerRequest(newPlayer)
        println(result?.consumedCapacity)
        return PlayerPutResponse(success=result !== null)
    }

    suspend fun movePlayer(id:String, x:Int, y:Int):PlayerPutResponse{
        val result:UpdateItemResponse? = _dal.movePlayerRequest(id,x,y)
        print(result?.consumedCapacity)
        return PlayerPutResponse(success=result !== null);
    }

}