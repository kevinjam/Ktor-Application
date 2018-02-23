package com.kevinjanvier.outbox

import com.kevinjanvier.outbox.model.Student
import com.kevinjanvier.outbox.controllers.StudentRepo
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StudentRepoTest {

    @After
    fun clear() = StudentRepo.clear()

    @Test
    fun getTest() {
        assertSize(0)
        val p = Student("P1", "Uganda", 40)
        val added = StudentRepo.add(p)
        assertSize(1)
        assertEquals(p, StudentRepo.get(added.id ?: fail()))
        assertEquals(p, StudentRepo.get(added.id?.toString() ?: fail()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getNonExistingTest() {
        StudentRepo.get(1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getNonExistingByStringTest() {
        StudentRepo.get("1")
    }

    /**
     * Saving the same object twice will have no effect
     */
    @Test
    fun saveTest() {
        assertSize(0)
        val add1 = StudentRepo.add(Student("P1", "",40))
        assertSize(1)
        val add2 = StudentRepo.add(Student("P1", "",40))
        assertSize(1)
        assertEquals(add1, add2)
    }

    @Test
    fun deleteTest() {
        val added = StudentRepo.add(Student("P1", "",40))
        assertSize(1)
        StudentRepo.remove(added.id ?: fail())
        assertSize(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deleteNonExistingTest() {
        StudentRepo.remove(1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deleteNonExistingByStringTest() {
        StudentRepo.remove("1")
    }

    @Test
    fun deleteByObjectTest() {
        val p = Student("P1", "",40)
        StudentRepo.add(p)
        assertSize(1)
        StudentRepo.remove(p)
        assertSize(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deleteNonExistingByObjectTest() {
        StudentRepo.remove(Student("", "",1))
    }

    private fun assertSize(int: Int) {
        assertEquals(int, StudentRepo.getAll().size)
    }

}