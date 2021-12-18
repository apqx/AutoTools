package me.apqx.downie

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.awt.Desktop
import java.io.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * 保存着缩略图的目录
 */
const val inDir = "/Users/apqx/Downloads/opera";

/**
 * 下载文件的输出目录
 */
const val outDir = "/Users/apqx/Downloads/downie";

/**
 * 日志文件
 */
val logFile = File(outDir, "downie.log")

/**
 * 下载新浪博客原图的工具，2种下载模式
 * 1. 根据URL下载，自动解析URL，下载所有博文中的图片原图，必须是
 * 2. 根据缩略图下载，把想要的缩略图从浏览器拖到[inDir]中，执行此程序即可下载原图
 *
 * 确认下载完成后，需要手动执行合并
 * 3. 把Downie下载的文件合并为正确格式的剧照
 */
fun main() {
    println("sina blog picture download tools, select option, then press enter")
    println("1. download from url")
    println("2. download from thumb in $inDir")
    println("3. merge")
    val reader = BufferedReader(InputStreamReader(System.`in`))
    when (reader.readLine()) {
        // 根据URL下载
        "1" -> {
            println("input url, split with ' ', then press enter")
            downloadPicsByUrl(reader.readLine())
            checkToMerge(reader)
        }
        // 根据缩略图下载
        "2" -> {
            downloadPicsByThumbFile(File(inDir))
            checkToMerge(reader)
        }
        // 合并，因为无法监测Downie是否下载完成，所以需要手动确认下载完成后再执行合并操作
        "3" -> mergePics()
    }
}

private fun checkToMerge(reader: BufferedReader) {
    println("if confirm download is done, press enter to proceed merge, or other keys to close")
    if (reader.readLine().isEmpty()) mergePics()
}

/**
 * 根据提供的新浪博文URL，自动解析博客中的照片，并发送给Downie下载
 */
fun downloadPicsByUrl(urls: String) {
    val descriptionBuilder = StringBuilder()
    urls.split(" ").forEach {
        val doc = Jsoup.connect(it).get()
        println("parsing ${doc.title()}")
        // 桌面版网站
        doc.select("div.articalContent").forEach {
            it.select("a > img").forEach {
                parseImgTag(it, 1)
            }
        }
        doc.select("div.BNE_cont").forEach {
            it.select("a > img").forEach {
                parseImgTag(it, 2)
            }
        }
        descriptionBuilder.appendLine(doc.title()).appendLine(it).appendLine()
    }
    // 移动版网站
    // 生成一个README.md文件
    generateReadme(descriptionBuilder.toString())
}

fun generateReadme(str: String) {
    BufferedWriter(OutputStreamWriter(FileOutputStream(File(outDir, "README.md")))).apply {
        write(str)
        flush()
        close()
    }
}

private var count = 0

private fun parseImgTag(img: Element, parseType: Int) {
    val imgUrl = img.attr("real_src")
    val imgId = getPicId(imgUrl)
    println("find ${formatCount(++count)} img by type $parseType $imgUrl $imgId")
    download(imgId)
}

fun formatCount(count: Int): String {
    if (count >= 1000) return count.toString()
    val zeroBuilder = StringBuilder()
    for (i in 0 until  (3 - count.toString().length)){
        zeroBuilder.append("0")
    }
    return zeroBuilder.append(count).toString()
}

fun getPicId(imgUrl: String): String = imgUrl.split("/").last().removeSuffix("&690")

/**
 * 根据已有的新浪博客缩略图，下载原始照片，把缩略图的文件名，加上下面URL为前缀，发送给Downie下载
 * http://s16.sinaimg.cn/orignal/
 */
fun downloadPicsByThumbFile(thumb: File) {
    if (thumb.isFile) {
        val extension = thumb.extension.lowercase()
        if (!extension.contains("jpg")
            && !extension.contains("jpeg")
        ) {
            println("jump ${thumb.name}")
            return
        }
        download(thumb.nameWithoutExtension)
    } else {
        thumb.listFiles().forEach {
            downloadPicsByThumbFile(it)
        }
    }
}

private fun download(imgId: String) {
    val picUrl = getPicUrl(imgId)
    println("process $picUrl")
    val downieUrl = getDownieUrl(picUrl, "$outDir/$imgId")
    val shell = "open -a 'Downie 4' '$downieUrl'"
    println(shell)
    // 发送到Downie的下载队列中
    val processBuilder = ProcessBuilder("open", "-a", "Downie 4", downieUrl)
    processBuilder.redirectErrorStream(true)
    processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))

    val process = processBuilder.start()
    process.waitFor()
}

/**
 * 拼接向Downie中添加下载任务所需的URL
 */
private fun getDownieUrl(picUrl: String, outDir: String): String {
    val encodedPicUrl = URLEncoder.encode(picUrl, StandardCharsets.UTF_8)
    return "downie://XUOpenURL?url=$encodedPicUrl&destination=$outDir"

}

/**
 * 拼接照片原图的URL
 */
private fun getPicUrl(fileName: String): String {
    val prefix = "http://s16.sinaimg.cn/orignal/"
    return prefix + fileName
}

/**
 * 整理下载下来的照片，重命名并移动到[outDir]中
 */
fun mergePics() {
    File(outDir).apply {
        listFiles().forEach {
            // 跳过文件
            if (it.isFile) return@forEach
            // 下载的文件就在文件夹里，把文件重命名为父文件夹的名字，移动到outDir中
            it.listFiles().forEach { pic ->
                println("merge ${it.name}/${pic.name}")
                Files.move(
                    pic.toPath(),
                    File(it.parent, it.name + ".jpg").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
                it.delete()
            }
        }
        Desktop.getDesktop().open(this)
    }
}