# SinaBlogPicDownloader

获取新浪博客照片原图的下载器，根据博文URL解析出文章中所有缩略图对应的原图地址，批量下载。

参见[我的这篇文章](https://apqx.me/post/original/2021/12/12/关于-编程-的一件小事.html)。

# 使用方式

解压`SinaBlogPicDownloader-*.*.*.zip`，根据所使用的操作系统双击`SinaBlogPicDownloader`或`SinaBlogPicDownloader.bat`即可启动命令行程序。

程序使用`Kotlin`编写，目标运行环境`JRE 17`，在`Windows`、`macOS`、`Linux`平台的`JVM`上均可运行。

支持2种下载模式，下载目录为`[user]/Downlaod/SinaDownload/`。

## 模式1

根据文章`URL`下载，自动解析`URL`并下载博文中所有的图片原图。 

## 模式2 

根据缩略图下载，把想要的缩略图从浏览器拖到`[user]/Downlaod/SinaThumb/`中，执行此程序即可下载原图。