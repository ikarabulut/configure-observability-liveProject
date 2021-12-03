package net.chrisrichardson.liveprojects.servicetemplate.util

import java.util.concurrent.TimeUnit

object TestUtil {

    fun <T> eventually(function: () -> T): T {
        return eventually(15, 1000, function)
    }

    fun <T> eventually(iterations: Int = 15, sleepInMillis: Long = 1000, function: () -> T): T {
        var laste: Throwable? = null
        for (n in 1..iterations) {
            try {
                return function.invoke()
            } catch (e: Exception) {
                laste = e
            } catch (e: AssertionError) {
                laste = e
            }
            if (n != iterations)
                TimeUnit.MILLISECONDS.sleep(sleepInMillis)
        }
        throw RuntimeException("Eventually timed out", laste!!)
    }

}