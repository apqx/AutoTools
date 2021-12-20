package me.apqx.sina

object Tools {
    fun formatCount(count: Int): String {
        if (count >= 1000) return count.toString()
        val zeroBuilder = StringBuilder()
        for (i in 0 until  (3 - count.toString().length)){
            zeroBuilder.append("0")
        }
        return zeroBuilder.append(count).toString()
    }
}