package com.kevalpatel2106.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Kevalpatel2106 on 21-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class ValidatorTest{
    /**
     * Unit test to validate the email address.
     */
    @Test
    @Throws(Exception::class)
    fun isValidEmail() {
        assertTrue(!Validator.isValidEmail(""))
        assertTrue(!Validator.isValidEmail(null))
        assertTrue(!Validator.isValidEmail("examplegmail.com"))
        assertTrue(!Validator.isValidEmail("example@g.com"))
        assertTrue(!Validator.isValidEmail("example@gmail.co.m"))
        assertTrue(!Validator.isValidEmail("example@gmail.c"))
        assertTrue(Validator.isValidEmail("example@gmail.com"))
    }

    /**
     * Unit test to validate the password address.
     */
    @Test
    @Throws(Exception::class)
    fun isValidPassword() {
        assertTrue(!Validator.isValidPassword(null))
        assertTrue(!Validator.isValidPassword("123456"))
        assertTrue(Validator.isValidPassword("123456789"))
        assertTrue(!Validator.isValidPassword("1234567890123456789"))
        assertTrue(!Validator.isValidPassword(""))
    }

    /**
     * Unit test to validate the name.
     */
    @Test
    @Throws(Exception::class)
    fun isValidName() {
        assertTrue(!Validator.isValidName(null))
        assertTrue(!Validator.isValidName(""))
        assertTrue(!Validator.isValidName("123456"))
        assertTrue(Validator.isValidName("123456789"))
        assertTrue(Validator.isValidName("123456789012345678901234567890"))
        assertTrue(!Validator.isValidName("1234567890123456789012345678901"))
    }
}