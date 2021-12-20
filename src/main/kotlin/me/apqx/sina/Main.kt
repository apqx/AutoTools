package me.apqx.sina

import me.apqx.sina.parser.ThumbParser
import me.apqx.sina.parser.URLParser
import java.io.*

/**
 * 保存着缩略图的目录
 */
private const val thumbDir = "/Users/apqx/Downloads/sinaThumb";

/**
 * 下载文件的输出目录
 */
private const val outDir = "/Users/apqx/Downloads/sinaDownload";

private val terminalReader = BufferedReader(InputStreamReader(System.`in`))

/**
 * 下载新浪博客原图的工具，2种下载模式
 * 1. 根据文章URL下载，自动解析URL，下载所有博文中的图片原图
 * 2. 根据缩略图下载，把想要的缩略图从浏览器拖到[thumbDir]中，执行此程序即可下载原图
 */
fun main() {
    checkFile()
    println("sina blog picture download tools, select option, then press enter")
    println("1. download from blog url")
    println("2. download from thumb in $thumbDir")
    when (terminalReader.readLine()) {
        // 根据URL下载
        "1" -> {
            println("input url, split with ' ', then press enter")
            downloadPicsByUrl(terminalReader.readLine())
        }
        // 根据缩略图下载
        "2" -> {
            downloadPicsByThumbFile(File(thumbDir))
        }
    }
}

private fun checkFile() {
    val outDirFile = File(outDir)
    if (!outDirFile.exists()) outDirFile.mkdirs()
}

/**
 * 根据提供的新浪博客文章URL，自动解析博客中的照片，执行下载
 */
private fun downloadPicsByUrl(urls: String) {
    Downloader(terminalReader, URLParser(outDir).parse(urls), outDir).start()
}

/**
 * 根据已有的新浪博客缩略图，下载原始照片，缩略图的文件名应是照片ID
 */
private fun downloadPicsByThumbFile(thumbDirFile: File) {
    Downloader(terminalReader, ThumbParser().parse(thumbDirFile), outDir).start()
}


