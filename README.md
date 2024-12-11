# SinaBlogPicDownloader

新浪博客文章原图的下载器，根据文章链接解析文中所有缩略图对应的原图地址，批量下载，支持同时处理多篇文章。

参见[我的这篇博文](https://mudan.me/post/original/2021/12/12/关于-编程-的一件小事.html)。

# 使用方式

解压`SinaBlogPicDownloader-*.*.*.zip`，根据所使用的操作系统双击`SinaBlogPicDownloader`或`SinaBlogPicDownloader.bat`即可启动命令行程序，遵循提示输入文章链接。

或手动编译执行：

```sh
# 工程根目录下执行
./gradlew run
```

下载目录为`[user]/Downlaod/SinaDownload/`。

# 运行环境

程序使用`Kotlin`编写，目标运行环境`JRE 21`，在`Windows`、`macOS`、`Linux`平台的`JVM`上均可运行。
