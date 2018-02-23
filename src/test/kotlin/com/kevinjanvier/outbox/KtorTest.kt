package com.kevinjanvier.outbox

import com.google.gson.Gson
import com.kevinjanvier.outbox.controllers.main
import com.kevinjanvier.outbox.constant.STUDENTS
import com.kevinjanvier.outbox.model.Student
import com.kevinjanvier.outbox.controllers.StudentRepo
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class KtorTest {

    private val content = """{"name":"Pan", "age":12}"""
    private val json = "application/json"

    private val gson = Gson()

    @After
    fun clear() = StudentRepo.clear()

    @Test
    fun firstGetTest() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun getAllPersonsTest() = withTestApplication(Application::main) {
        val person = savePerson(gson.toJson(Student("Solomon", "Uganda", 40)))
        val person2 = savePerson(gson.toJson(Student("Chris", "Kenyan", 25)))
        handleRequest(HttpMethod.Get, STUDENTS) {
            addHeader("Accept", json)
        }.response.let {
            assertEquals(HttpStatusCode.OK, it.status())
            val response = gson.fromJson(it.content, Array<Student>::class.java)
            response.forEach { println(it) }
            response.find { it.name == person.name } ?: fail()
            response.find { it.name == person2.name } ?: fail()
        }
        assertEquals(2, StudentRepo.getAll().size)
    }


    @Test
    fun savePersonTest() = withTestApplication(Application::main) {
        val person = savePerson()
        handleRequest(HttpMethod.Get, "${STUDENTS}/${person.id}") {
            addHeader("Accept", json)
        }.response.let {
            assertEquals(HttpStatusCode.OK, it.status())
        }
        assertEquals(1, StudentRepo.getAll().size)
    }

    @Test
    fun deletePersonTest() = withTestApplication(Application::main) {
        val person = savePerson()
        assertEquals(1, StudentRepo.getAll().size)
        handleRequest(HttpMethod.Delete, "${STUDENTS}/${person.id}") {
            addHeader("Accept", json)
        }.response.let {
            assertEquals(HttpStatusCode.OK, it.status())
        }
        assertEquals(0, StudentRepo.getAll().size)

    }

    private fun TestApplicationEngine.savePerson(person: String = content): Student {
        val post = handleRequest(HttpMethod.Post, STUDENTS) {
            body = person
            addHeader("Content-Type", json)
            addHeader("Accept", json)

        }
        with(post) {
            assertEquals(HttpStatusCode.OK, response.status())
            println(response.content)
            return gson.fromJson(response.content, Student::class.java)
        }
    }


}