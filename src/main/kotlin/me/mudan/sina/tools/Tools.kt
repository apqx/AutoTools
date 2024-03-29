package me.mudan.sina.tools

val Int.formatStyle: String
    get() : String {
    if (this >= 1000) return this.toString()
    val zeroBuilder = StringBuilder()
    for (i in 0 until (3 - this.toString().length)) {
        zeroBuilder.append("0")
    }
    return zeroBuilder.append(this).toString()
}