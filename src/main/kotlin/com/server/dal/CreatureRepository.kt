package com.server.dal

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.models.Creature
import java.time.Instant


class CreatureRepository {

    private val _tableName = "Entities"
    suspend fun getPlayers(first:Int,after:String,tblName:String = this._tableName):QueryResponse?{

        var response:QueryResponse?

        val expressionAttributes = mutableMapOf<String, AttributeValue>();
        expressionAttributes[":categoryValue"] = AttributeValue.S("Player")

        val request = QueryRequest{
            tableName = tblName
            indexName = "GlobalCategoryIndex"
            keyConditionExpression = "category = :categoryValue"
            expressionAttributeValues = expressionAttributes
            limit=first
        }

        try{
            DynamoDbClient{
                region="us-east-1"
            }.use{ddb->
                val result = ddb.query(
                    request
                )
                response = result
            }
        }catch(e:Exception){
            println(e.message)
            response = null
        }

        return response
    }

    suspend fun categorizeCreature(id:String,category:String,tblName:String = this._tableName):PutItemResponse?{

        val client = DynamoDbClient{region="us-east-1"}
        var response:PutItemResponse?
        var queryResult:Map<String,AttributeValue>

        try {
            val namedAttributes = mutableMapOf<String, String>();
            namedAttributes["#id"] = "id"

            val expressionAttributes = mutableMapOf<String, AttributeValue>()
            expressionAttributes[":id"] = AttributeValue.S(id)

            val entityQuery = QueryRequest {
                tableName = tblName
                keyConditionExpression = "#id = :id"
                expressionAttributeValues = expressionAttributes
                expressionAttributeNames = namedAttributes
            }

            // find the current Item
            client.use { ddb ->
                val result = ddb.query(
                    entityQuery
                )
                queryResult = result.items!!.first()
            }
        }catch(e:Exception){
            println("Query Request Exception: ${e.message}")
            return null
        }

        try {
            val deleteEntityKey = mutableMapOf<String, AttributeValue>()
            deleteEntityKey["id"] = AttributeValue.S(queryResult["id"]!!.asS())
            deleteEntityKey["category"] = AttributeValue.S(queryResult["category"]!!.asS())

            val deleteEntity = DeleteItemRequest {
                tableName = tblName
                key = deleteEntityKey
            }

            // Delete the old item
            DynamoDbClient { region = "us-east-1" }.use { ddb ->
                ddb.deleteItem(
                    deleteEntity
                )
            }

        }catch(e:Exception){
            println("Delete Request Exception: ${e.message}")
            return null
        }

        try{
            val itemValues = queryResult.toMutableMap()
            itemValues["category"] = AttributeValue.S(category)
            itemValues["timeModified"] = AttributeValue.N(Instant.now().toEpochMilli().toString())

            val request = PutItemRequest{
                tableName = tblName
                item = itemValues
            }

            // create a new item with time modified
            DynamoDbClient{region="us-east-1"}.use{ddb->
                val result = ddb.putItem(
                    request
                )
                response = result
            }
        }catch(e:Exception){
            println("Put Request Exception: ${e.message}")
            return null
        }

        return response


    }

    suspend fun getPlayerRequest(keyValue:String,tblName:String = this._tableName):QueryResponse?{
        var player:QueryResponse?;
        val expressionAttributes = mutableMapOf<String, AttributeValue>();
        expressionAttributes[":id"] = AttributeValue.S(keyValue)

        val request = QueryRequest{
            tableName = tblName
            keyConditionExpression = "id = :id"
            expressionAttributeValues = expressionAttributes
        }

        try{
            DynamoDbClient {
                region="us-east-1"
            }.use { ddb ->
                val response = ddb.query(request)
                player = response
            }
        }catch(e:Exception){
            println("Query Exception: ${e.message}")
            return null
        }

        return player
    }


    suspend fun createPlayerRequest(payload:Creature,tblName:String = this._tableName):PutItemResponse? {
        val isAlive:Int = if(payload.alive) 1 else 0

        val itemValues = mutableMapOf<String, AttributeValue>()
        itemValues["id"] = AttributeValue.S(payload.id)
        itemValues["name"] = AttributeValue.S(payload.name.toString())
        itemValues["x"] = AttributeValue.N(payload.x.toString())
        itemValues["y"] = AttributeValue.N(payload.y.toString())
        itemValues["initiative"] = AttributeValue.N(payload.initiative.toString())
        itemValues["hitPoints"] = AttributeValue.N(payload.hitPoints.toString())
        itemValues["alive"] = AttributeValue.N(isAlive.toString())
        itemValues["category"] = AttributeValue.S(payload.category)
        itemValues["timeCreated"] = AttributeValue.N(payload.timeCreated.toString())
        itemValues["timeModified"] = AttributeValue.N(payload.timeModified.toString())

        val mapper = jacksonObjectMapper()
        println(mapper.writeValueAsString(itemValues))

        var response: PutItemResponse?

        val request = PutItemRequest {
            tableName = tblName
            item = itemValues
        }

        try{
            DynamoDbClient {
                region = "us-east-1"
            }.use { ddb ->
                val result = ddb.putItem(request)
                response = result
            }
        }catch(e:Exception){
            println("Put Item Exception ${e.message}")
            return null
        }

        return response
    }

    suspend fun movePlayerRequest(id:String,x:Int,y:Int,tblName:String = this._tableName):UpdateItemResponse?{
        val itemKey = mutableMapOf<String,AttributeValue>()
        itemKey["id"] = AttributeValue.S(id)
        itemKey["category"] = AttributeValue.S("Player")

        try {
            val namedAttributes = mutableMapOf<String, String>();
            namedAttributes["#id"] = "id"

            val expressionAttributes = mutableMapOf<String, AttributeValue>()
            expressionAttributes[":id"] = AttributeValue.S(id)

            val getCreature = GetItemRequest {
                tableName = tblName
                key=itemKey
            }

            // find the current Item
            DynamoDbClient{region="us-east-1"}.use { ddb ->
                val result = ddb.getItem(
                    getCreature
                )
                if(result.item === null) throw Exception("Player Does not exist")
            }
        }catch(e:Exception){
            println("Query Request Exception: ${e.message}")
            return null
        }

        val updateValues = mutableMapOf<String,AttributeValueUpdate>()
        updateValues["x"] = AttributeValueUpdate {
            value = AttributeValue.N(x.toString())
            action = AttributeAction.Put
        }

        updateValues["y"] = AttributeValueUpdate {
            value = AttributeValue.N(y.toString())
            action = AttributeAction.Put
        }

        updateValues["timeModified"] = AttributeValueUpdate {
            value = AttributeValue.N(Instant.now().toEpochMilli().toString())
            action = AttributeAction.Put
        }

        var response:UpdateItemResponse?

        val request = UpdateItemRequest{
            tableName=tblName
            key = itemKey
            attributeUpdates = updateValues
        }

        try{
            DynamoDbClient {
                region="us-east-1"
            }.use { ddb ->
                val result = ddb.updateItem(request)
                response = result
            }
        }catch(e:Exception){
            println("Update Item Exception: ${e.message}")
            return null
        }

        return response


    }

}