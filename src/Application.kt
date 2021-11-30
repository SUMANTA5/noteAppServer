package com.example

import com.auth0.jwt.interfaces.Payload
import com.example.auth.JwtService
import com.example.auth.hash
import com.example.data.model.User
import com.example.repo.DatabaseFactory
import com.example.repo.Repo
import com.example.routes.noteRoute
import com.example.routes.userRoutes
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.locations.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()
    val db = Repo()
    val jwtService = JwtService()
    val hashFunction = {s:String -> hash(s)}

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Locations) {
    }

    install(Authentication) {
        jwt("jwt"){
            verifier(jwtService.varifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }


    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        userRoutes(db, jwtService, hashFunction)
        noteRoute(db, hashFunction)
    }
}

data class MySession(val count: Int = 0)

