package me.mudan.sina.download

import kotlin.time.Duration

data class DownloadResult(val imgItem: ImgItem, val success: Boolean, val timeUsed: Duration)
