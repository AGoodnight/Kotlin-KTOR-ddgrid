package com.server

import io.ktor.server.application.*
import io.ktor.server.netty.*
import com.apurebase.kgraphql.GraphQL
import com.server.graphql.creatureSchema
import com.server.services.CreatureService
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.configureCORS() {
    install(CORS){
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader("AccessToken")
        allowHeader("RefreshToken")
        allowCredentials = true
        anyHost()
    }
}

fun Application.configureGraphQL() {
    install(GraphQL) {
        val creatureService = CreatureService()
        playground=true
        schema{
            creatureSchema(creatureService)
        }
    }
}

fun Application.module() {
    configureCORS()
    configureGraphQL()
}