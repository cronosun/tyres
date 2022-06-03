package com.github.cronosun.tyres.kotlin

import com.github.cronosun.tyres.core.Fmt
import com.github.cronosun.tyres.core.Text

interface SimpleTestBundle {
    fun sayHello(): Text
    fun sayHelloTo(name: String): Fmt
}