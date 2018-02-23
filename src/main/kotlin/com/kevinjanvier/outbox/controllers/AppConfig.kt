package com.kevinjanvier.outbox.controllers

import com.kevinjanvier.outbox.config.LOG
import com.kevinjanvier.outbox.constant.STUDENTS
import com.kevinjanvier.outbox.model.Student
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.pipeline.*
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import java.time.Duration
import java.text.DateFormat



fun Application.main() {

    install(DefaultHeaders)
    install(CORS) {
        maxAge = Duration.ofDays(1)
    }
    install(ContentNegotiation){
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        get("${STUDENTS}/{id}") {
            errorAware {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                LOG.debug("Get Student entity with Id=$id")
                call.respond(StudentRepo.get(id))
            }
        }
        get(STUDENTS) {
            errorAware {
                LOG.debug("Get all Student entities")
                call.respond(StudentRepo.getAll())
            }
        }
        delete("${STUDENTS}/{id}") {
            errorAware {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                LOG.debug("Delete Student entity with Id=$id")
                call.respondSuccessJson(StudentRepo.remove(id))
            }
        }
        delete(STUDENTS) {
            errorAware {
                LOG.debug("Delete all Student entities")
                StudentRepo.clear()
                call.respondSuccessJson()
            }
        }
        post(STUDENTS) {
            errorAware {
                val receive = call.receive<Student>()
                println("Received Post Request: $receive")
                call.respond(StudentRepo.add(receive))
            }
        }
        get("/") {
            call.respondHtml {
                head {
                    title("Outbox ")
                }
                body {

                    div {
                        h1 { +"Welcome to Outbox Kotlin Session" }
                        p {
                            +"Let's Have Fun Kotlin "
                        }
                        br
                    }

                }
            }
        }
    }
}

private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText("""{"error":"$e"}""", ContentType.parse("application/json"),
                HttpStatusCode.InternalServerError)
        null
    }
}

private suspend fun ApplicationCall.respondSuccessJson(value: Boolean = true)
        = respond("""{"success": "$value"}""")