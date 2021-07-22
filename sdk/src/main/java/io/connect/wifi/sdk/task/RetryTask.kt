package io.connect.wifi.sdk.task

internal interface RetryTask: Runnable {

    fun name(): String

    fun maxRepeatCount(): Int

    fun getAndIncrement(): Int

    fun retryDelayMillis(): Long
}