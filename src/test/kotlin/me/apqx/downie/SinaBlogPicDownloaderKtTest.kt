package me.apqx.downie

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SinaBlogPicDownloaderKtTest {

    @Test
    fun getPicId() {
        assertEquals("001MZoW0gy6ROOgX7FM22", getPicId("http://s3.sinaimg.cn/mw690/001MZoW0gy6ROOgX7FM22&690"))
    }
}