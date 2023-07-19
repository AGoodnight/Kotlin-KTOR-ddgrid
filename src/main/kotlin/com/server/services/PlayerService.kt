package com.server.services

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemResponse
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemResponse
import com.github.javafaker.Faker
import com.server.mocks.MockPlayer
import com.server.models.Player
import com.server.models.PlayerPutResponse
import com.server.utils.Utils
import java.util.Random
import java.util.UUID
import com.server.dal.PlayerDAL as playerDAL

class PlayerService {

    private val _utils = Utils()
    private val _faker = Faker()
    private val _random = Random()
    private val _dal = playerDAL()

    var player = MockPlayer

    suspend fun getPlayer(id:String):Player?{
        val result = _dal.getPlayerRequest("MatrixEntities","id",id)
        val resultingItem = result.item
        var resultMap:MutableMap<String,Any> = mutableMapOf()
        if(resultingItem != null){
            for( keyname in _utils.toMap(player).keys){
                val att:AttributeValue? = resultingItem[keyname.toString()]
                if(att != null){
                    resultMap[keyname.toString()] = att.asS()
                }
            }
            return Player(
                resultMap["id"].toString(),
                resultMap["alive"].toString().toBoolean(),
                resultMap["hitPoints"].toString().toInt(),
                resultMap["initiative"].toString().toInt(),
                resultMap["x"].toString().toInt(),
                resultMap["y"].toString().toInt(),
                resultMap["name"].toString(),
           )

        }else{
            return null
        }
    }

    suspend fun createRandomPlayer():PlayerPutResponse{
        val newPlayer = Player(
            id=UUID.randomUUID().toString(),
            name=_faker.name().firstName(),
            hitPoints=_random.nextInt(20),
            initiative=_random.nextInt(20),
            alive=true,
            x=_random.nextInt(20),
            y=_random.nextInt(20),
        )
        val result:PutItemResponse = _dal.createPlayerRequest("MatrixEntities",newPlayer)
        return PlayerPutResponse(success = true)
    }

    suspend fun movePlayer(id:String, x:Int, y:Int):PlayerPutResponse{
        val result:UpdateItemResponse = _dal.movePlayerRequest("MatrixEntities",id,x,y)
        return PlayerPutResponse(success = true);
    }

}