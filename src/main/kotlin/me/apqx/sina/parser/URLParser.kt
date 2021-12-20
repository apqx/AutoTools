package me.apqx.sina.parser

import me.apqx.sina.Tools
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/**
 * 新浪博客的文章URL解析器
 */
class URLParser(private val outDir: String) {
    private var findCount = 0

    fun parse(urls: String): List<String> {
        val descriptionBuilder = StringBuilder()
        val imgIdList = ArrayList<String>()
        urls.split(" ").forEach { url ->
            val doc = Jsoup.connect(url).get()
            println("parsing ${doc.title()}")
            // 桌面版网站
            doc.select("div.articalContent").forEach {
                it.select("a > img").forEach { imgE ->
                    imgIdList.add(parseImgTag4Id(imgE, 1))
                }
            }
            doc.select("div.BNE_cont").forEach {
                it.select("a > img").forEach { imgE ->
                    imgIdList.add(parseImgTag4Id(imgE, 2))
                }
            }
            descriptionBuilder.appendLine(doc.title()).appendLine(url).appendLine()
        }
        // 移动版网站
        // 生成一个README.md文件
        generateReadme(descriptionBuilder.toString())
        return imgIdList
    }

    private fun generateReadme(str: String) {
        BufferedWriter(OutputStreamWriter(FileOutputStream(File(outDir, "README.md")))).use {
            it.write(str)
            it.flush()
        }
    }


    private fun parseImgTag4Id(img: Element, parseType: Int): String {
        val imgUrl = img.parent()!!.attr("href")
        val imgId = getPicId(imgUrl)
        println("find ${Tools.formatCount(++findCount)} img by type $parseType $imgUrl $imgId")
        return imgId
    }

    private fun getPicId(imgUrl: String): String = imgUrl.split("/").last().removeSuffix("&690")
}