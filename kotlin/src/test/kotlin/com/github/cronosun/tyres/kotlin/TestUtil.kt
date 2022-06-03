package com.github.cronosun.tyres.kotlin

import com.github.cronosun.tyres.core.DefaultNotFoundConfig
import com.github.cronosun.tyres.core.Resources
import com.github.cronosun.tyres.implementation.ResourcesBuilder

object TestUtil {
    fun resources(notFoundConfig: DefaultNotFoundConfig): Resources {
        return ResourcesBuilder().defaultNotFoundConfig(notFoundConfig).build()
    }
}