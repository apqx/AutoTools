package me.apqx.downie

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SinaBlogPicDownloaderKtTest {

    @Test
    fun getPicId() {
        assertEquals("001MZoW0gy6ROOgX7FM22", getPicId("http://s3.sinaimg.cn/mw690/001MZoW0gy6ROOgX7FM22&690"))
        println("12 12".split(" "))
        println("12".split(" "))
    }

    @Test
    fun formatCountTest() {
        assertEquals("001", formatCount(1))
        assertEquals("011", formatCount(11))
        assertEquals("111", formatCount(111))
        assertEquals("1111", formatCount(1111))
    }
}