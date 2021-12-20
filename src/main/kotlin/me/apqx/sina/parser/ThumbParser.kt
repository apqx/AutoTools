package me.apqx.sina.parser

import me.apqx.sina.*
import java.io.File


class ThumbParser {
    private var findCount = 0

    fun parse(thumbDirFile: File): List<String> {
        val imgIdList = ArrayList<String>()
        thumbDirFile.listFiles()?.forEach {
            val extension = it.extension.lowercase()
            if (!extension.contains("jpg")
                && !extension.contains("jpeg")
            ) {
                println("jump ${it.name}")
                return@forEach
            }
            val imgId = it.nameWithoutExtension
            val imgUrl = getImgUrl(imgId)
            imgIdList.add(imgId)
            println("find ${Tools.formatCount(++findCount)} img $imgId $imgUrl")
        }
        return imgIdList
    }
}

/**
 * 拼接照片原图的URL
 */
private fun getImgUrl(imgId: String): String {
    val prefix = "http://album.sina.com.cn/pic/"
    return prefix + imgId
}