package me.apqx.sina

import me.mudan.sina.Tools.formatCount
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MainKtTest {

    @Test
    fun formatCountTest() {
        assertEquals("001", formatCount(1))
        assertEquals("011", formatCount(11))
        assertEquals("111", formatCount(111))
        assertEquals("1111", formatCount(1111))
    }
}