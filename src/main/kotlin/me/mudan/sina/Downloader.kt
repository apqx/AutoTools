package me.mudan.sina

import java.awt.Desktop
import java.io.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.util.*
import java.util.concurrent.Executors

/**
 * 并发下载指定的图片列表，并发量为[parallelJobCount]
 */
class Downloader(private val terminalReader: BufferedReader, private val imgIdList: List<String>,
                 private val outDir: String) {
    /**
     * 并发量
     */
    private val parallelJobCount = 4
    private val executor = Executors.newFixedThreadPool(parallelJobCount)
    private val queue = Collections.synchronizedList(LinkedList(imgIdList))
    private val failedQueue = Collections.synchronizedList(LinkedList<String>())
    private var roundCount = 0
    private var doneCount = 0
    private val httpClient: HttpClient by lazy {
        HttpClient.newBuilder()
            // 支持重定向，因为新浪博客的原图跳转有一次重定向
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build()
    }

    /**
     * 开始下载，同时执行[parallelJobCount]个任务
     */
    fun start() {
        roundCount = queue.size
        doneCount = 0
        for (i in 0 until if (roundCount <= parallelJobCount) roundCount else parallelJobCount) {
            executor.execute(DownloadJob(queue.removeFirst()))
        }
    }

    private fun download(imgId: String): Boolean = try {
        httpClient.send(
            buildHttpRequest(getImgUrl(imgId)),
            HttpResponse.BodyHandlers.ofFile(Path.of("$outDir/$imgId.jpg"))
        ).body().toFile()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    private fun buildHttpRequest(imgUrl: String) =
        HttpRequest.newBuilder()
            .uri(URI(imgUrl))
            // 添加Referer，新浪博客的图床设置了防盗链
            .header("Referer", imgUrl)
            .GET()
            .build()

    /**
     * 拼接照片原图的URL
     */
    private fun getImgUrl(imgId: String): String = "http://album.sina.com.cn/pic/$imgId"

    inner class DownloadJob(private val imgId: String) : Runnable {
        override fun run() {
            if (download(imgId))
                println(
                    "${Tools.formatCount(++doneCount)}/${Tools.formatCount(roundCount)} " +
                            "img $imgId download success ${getImgUrl(imgId)}"
                )
            else {
                println(
                    "${Tools.formatCount(++doneCount)}/${Tools.formatCount(roundCount)} " +
                            "img $imgId download failed ${getImgUrl(imgId)}"
                )
                failedQueue.add(imgId)
            }
            if (queue.size > 0)
                executor.execute(DownloadJob(queue.removeFirst()))
            else
                checkDownloadDone()
        }
    }

    private fun checkDownloadDone() {
        if (doneCount != roundCount) return
        if (failedQueue.size > 0) {
            val successCount = imgIdList.size - failedQueue.size
            println("download img success $successCount, failed ${failedQueue.size}")
            println("should retry? y/n")
            if (terminalReader.readLine() == "y") {
                queue.addAll(failedQueue)
                failedQueue.clear()
                start()
            } else {
                val logFile = File(outDir, "failed.log")
                BufferedWriter(OutputStreamWriter(FileOutputStream(logFile))).use { writer ->
                    failedQueue.forEach {
                        writer.appendLine("$it ${getImgUrl(it)}")
                    }
                }
                println("failed img list saved to ${logFile.absolutePath}")
                executor.shutdown()
                Desktop.getDesktop().open(File(outDir))
            }
        } else {
            println("download done")
            executor.shutdown()
            Desktop.getDesktop().open(File(outDir))
        }
    }
}
