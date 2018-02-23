package com.kevinjanvier.outbox.controllers

import com.kevinjanvier.outbox.model.Student
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

object StudentRepo {

    private val counterId = AtomicInteger()
    private val students = CopyOnWriteArraySet<Student>()

    fun add(p: Student): Student {
        if (students.contains(p)) {
            return students.find { it == p }!!
        }
        p.id = counterId.incrementAndGet()
        students.add(p)
        return p
    }


    fun get(id: String) = students.find { it.id.toString() == id } ?:
    throw IllegalArgumentException("No entitiy found for $id")

    fun get(id: Int) = get(id.toString())

    fun getAll() = students.toList()

    fun remove(p: Student) {
        if (!students.contains(p)) {
            throw IllegalArgumentException("Student not stored in repo.")
        }
        students.remove(p)
    }

    fun remove(id: String) = students.remove(get(id))

    fun remove(id: Int) = students.remove(get(id))


    fun clear() = students.clear()

}