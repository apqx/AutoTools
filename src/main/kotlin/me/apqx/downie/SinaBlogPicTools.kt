package me.apqx.downie

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption

const val inDir = "/Users/apqx/Downloads/opera";
const val outDir = "/Users/apqx/Downloads/downie";
val logFile = File(outDir, "downie.log")

/**
 * 下载新浪博客原图的工具
 * 只需要把想要的缩略图从浏览器拖到[inDir]中，执行此程序即可下载原图
 */
fun main() {
    println("select options, then press enter")
    println("1. download from thumb")
    println("2. merge")
    val reader = BufferedReader(InputStreamReader(System.`in`))
    when(reader.readLine()) {
        // 下载
        "1" -> {
            downloadPicsByThumbFile(File(inDir))
            println("if confirm download is done, press enter to proceed merge, or will close")
            if (reader.readLine().isEmpty()) mergePics()
        }
        // 合并，因为无法监测Downie是否下载完成，所以需要手动确认下载完成后再执行合并操作
        "2" -> mergePics()
    }
}

fun downloadPicsByUrl(url: String) {

}

/**
 * 根据已有的新浪博客缩略图，下载新浪博客的原始照片，把缩略图的文件名，加上下面URL为前缀，发送给Downie下载
 * http://s16.sinaimg.cn/orignal/
 */
fun downloadPicsByThumbFile(thumb: File) {
    if(thumb.isFile) {
        val extension = thumb.extension.lowercase()
        if (!extension.contains("jpg")
            && !extension.contains("jpeg")) {
            println("jump ${thumb.name}")
            return
        }
        val picUrl = getPicUrl(thumb.nameWithoutExtension)
        println("process $picUrl")
        val downieUrl = getDownieUrl(picUrl, "$outDir/${thumb.nameWithoutExtension}")
        val shell = "open -a 'Downie 4' '$downieUrl'"
        println(shell)
        // 发送到Downie的下载队列中
        val processBuilder = ProcessBuilder("open", "-a", "Downie 4", downieUrl)
        processBuilder.redirectErrorStream(true)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))

        val process = processBuilder.start()
        process.waitFor()
    } else {
        thumb.listFiles().forEach {
            downloadPicsByThumbFile(it)
        }
    }
}

private fun getDownieUrl(picUrl: String, outDir: String): String {
    val encodedPicUrl = URLEncoder.encode(picUrl, StandardCharsets.UTF_8)
    return "downie://XUOpenURL?url=$encodedPicUrl&destination=$outDir"

}

private fun getPicUrl(fileName: String): String {
    val prefix = "http://s16.sinaimg.cn/orignal/"
    return prefix + fileName
}

/**
 * 整理下载下来的照片，重命名并移动到[outDir]中
 */
fun mergePics() {
    File(outDir).listFiles().forEach {
        // 跳过文件
        if (it.isFile) return@forEach
        // 下载的文件就在文件夹里，把文件重命名为父文件夹的名字，移动到outDir中
        it.listFiles().forEach { pic ->
            println("merge ${it.name}/${pic.name}")
            Files.move(pic.toPath(), File(it.parent, it.name + ".jpg").toPath(), StandardCopyOption.REPLACE_EXISTING)
            it.delete()
        }

    }
}