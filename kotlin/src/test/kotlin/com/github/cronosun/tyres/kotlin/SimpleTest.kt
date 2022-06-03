package com.github.cronosun.tyres.kotlin

import com.github.cronosun.tyres.core.DefaultNotFoundConfig
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.*

class SimpleTest {
    @Test
    fun testSimpleBundleValidates() {
        val resources = TestUtil.resources(DefaultNotFoundConfig.THROW)
        resources.validate(SimpleTestBundle::class.java, Locale.ENGLISH)
    }

    @Test
    fun testSimpleBundleWorks() {
        val resources = TestUtil.resources(DefaultNotFoundConfig.THROW)
        val bundle = resources.get(SimpleTestBundle::class.java)

        assertEquals("Hello to the world!", bundle.sayHello().maybe(Locale.ENGLISH))
        assertEquals("System says hello to Claudia!", bundle.sayHelloTo("Claudia").maybe(Locale.ENGLISH))
    }
}