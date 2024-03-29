package me.mudan.sina.tools

import me.mudan.sina.download.ImgItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

/**
 * 新浪博客的文章URL解析器
 */

fun getImgIdListFromUrl(urls: String, outDir: String): List<ImgItem> {
    var findCount = 0
    val descriptionBuilder = StringBuilder()
    val imgIdList = ArrayList<ImgItem>()
    urls.split(" ").forEach { url ->
        val doc = Jsoup.connect(url).get()
        LogUtil.info("Parsing ${doc.title()}")
        // 桌面版网站
        doc.select("div.articalContent").forEach {
            it.select("a > img").forEach { imgE ->
                val imgId = getImgIdFromTag(imgE, 1, ++findCount)
                imgIdList.add(ImgItem(imgId, getImgUrl(imgId)))
            }
        }
        doc.select("div.BNE_cont").forEach {
            it.select("a > img").forEach { imgE ->
                val imgId = getImgIdFromTag(imgE, 2, ++findCount)
                imgIdList.add(ImgItem(imgId, getImgUrl(imgId)))
            }
        }
        descriptionBuilder.appendLine(doc.title()).appendLine(url).appendLine()
    }
    // 移动版网站
    // 生成一个README.md文件
    File(outDir, "README.txt").writeText(descriptionBuilder.toString())
    return imgIdList
}

private fun getImgIdFromTag(img: Element, parseType: Int, findCount: Int): String {
    val imgUrl = img.parent()!!.attr("href")
    val imgId = getPicId(imgUrl)
    LogUtil.info("Find ${findCount.formatStyle} img by type $parseType $imgUrl $imgId")
    return imgId
}

private fun getPicId(imgUrl: String): String = imgUrl.split("/").last().removeSuffix("&690")

/**
 * 获取照片的原图URL
 */
fun getImgUrl(imgId: String): String = "http://album.sina.com.cn/pic/$imgId"
