package com.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.server.plugins.*
import com.apurebase.kgraphql.GraphQL
import com.server.graphql.playerSchema
import com.server.services.PlayerService
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
        val playerService = PlayerService()
        playground=true
        schema{
            playerSchema(playerService)
        }
    }
}

fun Application.module() {
    configureRouting()
    configureCORS()
    configureGraphQL()
}