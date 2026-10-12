package com.altankoc.couplecounter

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform