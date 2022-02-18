package net.chrisrichardson.liveprojects.servicetemplate.util

import java.util.concurrent.TimeUnit

object Eventually {

    fun <T> withConfiguration(iterations: Int = 15, sleepInMillis: Long = 1000): (() -> T) -> T {
        return { function -> eventuallyInternal(iterations, sleepInMillis, function) }
    }

    fun <T> eventually(function: () -> T): T {
        return withConfiguration<T>(15, 1000)(function)
    }

    private fun <T> eventuallyInternal(iterations: Int, sleepInMillis: Long, function: () -> T): T {
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