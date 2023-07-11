package com.server.dal

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import com.server.models.Player
import com.server.utils.Utils

class PlayerDAL {

    private val _utils = Utils()

    suspend fun getPlayerRequest(tblName:String,keyName:String,keyValue:String):GetItemResponse{
        var player:GetItemResponse;
        val keyToGet = mutableMapOf<String, AttributeValue>();
        keyToGet[keyName] = AttributeValue.S(keyValue)

        val request = GetItemRequest{
            key = keyToGet
            tableName = tblName
        }

        DynamoDbClient {
            region="us-east-1"
            credentialsProvider = EnvironmentCredentialsProvider()
        }.use { ddb ->
            val response = ddb.getItem(request)
            player = response
        }

        return player
    }

    suspend fun createPlayerRequest(tblName:String,payload:Player):PutItemResponse{

        val attributes:Map<out String, Any?> = _utils.toMap(payload)
        val att:Map<out String, AttributeValue> = attributes.mapValues { value -> AttributeValue.S(value.value.toString()) }
        val itemValues = mutableMapOf<String,AttributeValue>()
        itemValues.putAll(att)
        var response:PutItemResponse

        val request = PutItemRequest{
            tableName=tblName
            item=itemValues
        }

        DynamoDbClient {
            region="us-east-1"
            credentialsProvider = EnvironmentCredentialsProvider()
        }.use { ddb ->
            val result = ddb.putItem(request)
            response = result
        }

        return response
    }

}