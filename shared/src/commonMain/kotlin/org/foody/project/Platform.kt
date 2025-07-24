package org.foody.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun currentTimeMillis(): Long
