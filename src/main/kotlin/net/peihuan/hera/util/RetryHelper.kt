package net.peihuan.hera.util



inline fun <reified T> blockWithTry(retryTime: Int = 5, initDelay: Long = 1200, block: () -> T): T {
    var cnt = 0;
    var delay = initDelay
    var lastE: Exception? = null
    try {
        while (++cnt < retryTime) {
            return block()
        }
    } catch (e: Exception) {
        Thread.sleep(delay)
        lastE = e
        delay += 1000
    }
    throw lastE!!


}