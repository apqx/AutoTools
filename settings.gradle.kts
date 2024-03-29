pluginManagement {
    repositories {
        // central+jcenter+google
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public")
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // central+jcenter+google
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public")
        mavenCentral()
        google()
    }
}
rootProject.name = "SinaBlogPicDownloader"