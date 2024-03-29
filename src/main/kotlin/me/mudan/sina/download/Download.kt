package me.mudan.sina.download

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import me.mudan.sina.tools.formatStyle
import me.mudan.sina.tools.LogUtil
import java.awt.Desktop
import java.io.*
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.measureTime

private val client: HttpClient by lazy {
    HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
    }
}

private val dispatcher = Executors.newFixedThreadPool(6).asCoroutineDispatcher()
private val coroutineScope = CoroutineScope(dispatcher)

private var totalTime = Duration.ZERO
private val mutex = Mutex()
fun download(imgList: List<ImgItem>, outDir: String) {
    val failedList = ArrayList<ImgItem>()
    val jobCount = imgList.size
    var doneCount = 0
    val job = coroutineScope.launch {
        imgList.forEach {
            launch {
                val result = download(it, outDir)
                mutex.lock()
                if (result.isSuccess) {
                    LogUtil.info(
                        "${(++doneCount).formatStyle}/${jobCount.formatStyle} " +
                                "img ${it.id} download success, ${it.url}"
                    )
                } else {
                    LogUtil.info(
                        "${(++doneCount).formatStyle}/${jobCount.formatStyle} " +
                                "img ${it.id} download failed, ${it.url}"
                    )
                    failedList.add(it)
                }
                mutex.unlock()
            }
        }
    }
    totalTime += measureTime {
        runBlocking {
            job.join()
        }
    }
    if (failedList.size > 0) {
        LogUtil.info("Total time ${totalTime.inWholeSeconds} s, failed count = ${failedList.size}")
        LogUtil.info("Should retry? y/n")
        if (readln() == "y") {
            download(failedList, outDir)
        } else {
            Desktop.getDesktop().open(File(outDir))
            dispatcher.close()
            client.close()
        }
    } else {
        LogUtil.info("Download done, total time ${totalTime.inWholeSeconds} s")
        Desktop.getDesktop().open(File(outDir))
        dispatcher.close()
        client.close()
    }
}

private suspend fun download(imgItem: ImgItem, outDir: String) = runCatching {
    val response = client.get(imgItem.url) {
        headers {
            append("Referer", imgItem.url)
        }
    }
    if (response.status.value == 200) {
        val file = File(outDir, imgItem.id + ".jpg")
        response.bodyAsChannel().copyTo(file.writeChannel())
    } else {
        LogUtil.info("Download ${imgItem.id}  failed, status = ${response.status.value}")
    }
}

