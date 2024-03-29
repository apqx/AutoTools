package me.mudan.sina

import me.mudan.sina.download.download
import me.mudan.sina.tools.LogUtil
import me.mudan.sina.tools.getImgIdListFromUrl
import java.io.*

/**
 * 输出目录
 */
private val DOWNLOAD_DIR = "${System.getProperty("user.home")}/Downloads/SinaDownload"

/**
 * 下载新浪博客原图，根据文章URL自动解析下载博文中的所有图片原图
 */
fun main() {
    LogUtil.info(
        """
            Sina blog picture downloader
            Input page urls, split with ' ', then press enter
        """.trimIndent()
    )
    checkFile()
    download(getImgIdListFromUrl(readln().trim(), DOWNLOAD_DIR), DOWNLOAD_DIR)
}

private fun checkFile() {
    val dirFile = File(DOWNLOAD_DIR)
    if (!dirFile.exists()) dirFile.mkdirs()
}